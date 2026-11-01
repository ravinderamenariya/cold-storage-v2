package com.example.data.repository

import com.example.data.local.AlertDao
import com.example.data.local.CrateDao
import com.example.data.local.SensorReadingDao
import com.example.data.model.AlertItem
import com.example.data.model.AlertSeverity
import com.example.data.model.ChamberData
import com.example.data.model.ConnectionMode
import com.example.data.model.Crate
import com.example.data.model.CrateStatus
import com.example.data.model.PowerData
import com.example.data.model.SensorNodeStatus
import com.example.data.model.SensorReading
import com.example.data.model.IndianAgriProduce
import com.example.data.model.ShelfLifePrediction
import com.example.data.remote.AgriMarketPriceService
import com.example.data.remote.RaspberryPiApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

class ColdStorageRepository(
    private val crateDao: CrateDao,
    private val sensorReadingDao: SensorReadingDao,
    private val alertDao: AlertDao,
    private val coroutineScope: CoroutineScope
) {
    // Current connection mode & settings
    private val _connectionMode = MutableStateFlow(ConnectionMode.DEMO)
    val connectionMode: StateFlow<ConnectionMode> = _connectionMode.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _syncStatus = MutableStateFlow("Synced")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private val _deviceConnected = MutableStateFlow(true)
    val deviceConnected: StateFlow<Boolean> = _deviceConnected.asStateFlow()

    private val _raspberryPiHost = MutableStateFlow("http://10.190.234.170:5000/")
    val raspberryPiHost: StateFlow<String> = _raspberryPiHost.asStateFlow()

    // Live Telemetry states (Initialized to unavailable/disconnected until actual API response)
    private val _chamber1 = MutableStateFlow(ChamberData("Chamber 1", 0.0, 12.0, 0.0, 65.0, "Disconnected", false, false, false))
    val chamber1: StateFlow<ChamberData> = _chamber1.asStateFlow()

    private val _chamber2 = MutableStateFlow(ChamberData("Chamber 2", 0.0, 4.0, 0.0, 60.0, "Disconnected", false, false, false))
    val chamber2: StateFlow<ChamberData> = _chamber2.asStateFlow()

    private val _outsideTemp = MutableStateFlow(0.0)
    val outsideTemp: StateFlow<Double> = _outsideTemp.asStateFlow()

    private val _outsideHumidity = MutableStateFlow(0.0)
    val outsideHumidity: StateFlow<Double> = _outsideHumidity.asStateFlow()

    private val _gasStatus = MutableStateFlow("Unknown")
    val gasStatus: StateFlow<String> = _gasStatus.asStateFlow()

    private val _airQuality = MutableStateFlow("Unknown")
    val airQuality: StateFlow<String> = _airQuality.asStateFlow()

    private val _powerData = MutableStateFlow(PowerData(solarInputWatts = 0.0, batteryPercent = 0, isCharging = false))
    val powerData: StateFlow<PowerData> = _powerData.asStateFlow()

    private val _lastUpdatedTime = MutableStateFlow(System.currentTimeMillis())
    val lastUpdatedTime: StateFlow<Long> = _lastUpdatedTime.asStateFlow()

    private val _coolingActive = MutableStateFlow(true)
    val coolingActive: StateFlow<Boolean> = _coolingActive.asStateFlow()

    private val _fansActive = MutableStateFlow(true)
    val fansActive: StateFlow<Boolean> = _fansActive.asStateFlow()

    private val _sensorNodes = MutableStateFlow(
        listOf(
            SensorNodeStatus("DHT11", "Chamber 1", true),
            SensorNodeStatus("DHT11", "Chamber 2", true),
            SensorNodeStatus("DHT11", "Outside", true),
            SensorNodeStatus("MQ-2", "Gas 1", true),
            SensorNodeStatus("MQ-2", "Gas 2", true),
            SensorNodeStatus("TFT Display", "Cabinet", true)
        )
    )
    val sensorNodes: StateFlow<List<SensorNodeStatus>> = _sensorNodes.asStateFlow()

    // Data streams from local Room DB
    val allCrates: Flow<List<Crate>> = crateDao.getAllCrates()
    val allAlerts: Flow<List<AlertItem>> = alertDao.getAllAlerts()
    val unreadAlertsCount: Flow<Int> = alertDao.getUnreadCount()
    val recentReadings: Flow<List<SensorReading>> = sensorReadingDao.getRecentReadings(100)

    private val agriMarketService = AgriMarketPriceService(alertDao, coroutineScope)
    val marketProduces: StateFlow<List<IndianAgriProduce>> = agriMarketService.produces

    private var raspberryPiApi: RaspberryPiApi? = null
    private var simulationStep = 0

    init {
        initRetrofit()
        coroutineScope.launch(Dispatchers.IO) {
            seedInitialDatabaseIfEmpty()
            startTelemetryLoop()
        }
    }

    private fun initRetrofit() {
        try {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(_raspberryPiHost.value)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            raspberryPiApi = retrofit.create(RaspberryPiApi::class.java)
        } catch (_: Exception) {
            raspberryPiApi = null
        }
    }

    fun updateRaspberryPiHost(host: String) {
        var clean = host.trim()
        if (clean.isBlank()) return

        if (!clean.startsWith("http://", ignoreCase = true) && !clean.startsWith("https://", ignoreCase = true)) {
            clean = "http://$clean"
        }

        val isTunnel = clean.contains("ngrok", ignoreCase = true) || clean.contains("loca.lt", ignoreCase = true)
        val authority = clean.substringAfter("://").substringBefore("/")
        if (!authority.contains(":") && !isTunnel) {
            // Append default port 5000 if not specified on local IP
            clean = clean.replace(authority, "$authority:5000")
        }

        if (!clean.endsWith("/")) {
            clean = "$clean/"
        }

        _raspberryPiHost.value = clean
        _connectionMode.value = ConnectionMode.RASPBERRY_PI
        initRetrofit()
        coroutineScope.launch(Dispatchers.IO) {
            pollRaspberryPi()
        }
    }

    fun setConnectionMode(mode: ConnectionMode) {
        _connectionMode.value = mode
        if (mode == ConnectionMode.RASPBERRY_PI) {
            _deviceConnected.value = false
            _chamber1.value = _chamber1.value.copy(currentTemp = 0.0, humidity = 0.0, status = "Disconnected", coolingActive = false, fanActive = false)
            _chamber2.value = _chamber2.value.copy(currentTemp = 0.0, humidity = 0.0, status = "Disconnected", coolingActive = false, fanActive = false)
            _outsideTemp.value = 0.0
            _outsideHumidity.value = 0.0
            _gasStatus.value = "Unknown"
            _coolingActive.value = false
            _fansActive.value = false
        }
    }

    fun setOnline(online: Boolean) {
        _isOnline.value = online
        _syncStatus.value = if (online) "Synced" else "Offline"
    }

    suspend fun triggerManualSync() {
        if (!_isOnline.value) return
        _syncStatus.value = "Syncing..."
        delay(1200)
        _syncStatus.value = "Synced"
    }

    fun toggleCooling(enabled: Boolean) {
        if (_connectionMode.value == ConnectionMode.RASPBERRY_PI) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val response = raspberryPiApi?.toggleCooling(enabled)
                    if (response != null && response.isSuccessful) {
                        _coolingActive.value = enabled
                        _chamber1.value = _chamber1.value.copy(coolingActive = enabled)
                        _chamber2.value = _chamber2.value.copy(coolingActive = enabled)
                    }
                } catch (_: Exception) {}
            }
        } else {
            _coolingActive.value = enabled
            _chamber1.value = _chamber1.value.copy(coolingActive = enabled)
            _chamber2.value = _chamber2.value.copy(coolingActive = enabled)
        }
    }

    fun toggleFans(enabled: Boolean) {
        if (_connectionMode.value == ConnectionMode.RASPBERRY_PI) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val response = raspberryPiApi?.toggleFans(enabled)
                    if (response != null && response.isSuccessful) {
                        _fansActive.value = enabled
                        _chamber1.value = _chamber1.value.copy(fanActive = enabled)
                        _chamber2.value = _chamber2.value.copy(fanActive = enabled)
                    }
                } catch (_: Exception) {}
            }
        } else {
            _fansActive.value = enabled
            _chamber1.value = _chamber1.value.copy(fanActive = enabled)
            _chamber2.value = _chamber2.value.copy(fanActive = enabled)
        }
    }

    /**
     * Continuous background loop that updates live telemetry every 3 seconds.
     */
    private suspend fun startTelemetryLoop() {
        while (coroutineScope.isActive) {
            try {
                when (_connectionMode.value) {
                    ConnectionMode.DEMO -> {
                        _deviceConnected.value = true
                        simulateTelemetryTick()
                    }
                    ConnectionMode.RASPBERRY_PI -> {
                        pollRaspberryPi()
                    }
                    ConnectionMode.CLOUD_SYNC -> {
                        simulateTelemetryTick()
                    }
                }
            } catch (_: Exception) {
                _deviceConnected.value = false
            }
            delay(3000)
        }
    }

    private suspend fun pollRaspberryPi() {
        val api = raspberryPiApi
        if (api == null) {
            _deviceConnected.value = false
            _chamber1.value = _chamber1.value.copy(status = "Disconnected", currentTemp = 0.0, humidity = 0.0)
            _chamber2.value = _chamber2.value.copy(status = "Disconnected", currentTemp = 0.0, humidity = 0.0)
            return
        }

        try {
            // Verify health endpoint and fetch live telemetry
            val response = api.getLiveTelemetry()

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                _deviceConnected.value = true
                _lastUpdatedTime.value = System.currentTimeMillis()

                _chamber1.value = _chamber1.value.copy(
                    currentTemp = data.chamber1.temperature,
                    humidity = data.chamber1.humidity,
                    status = if (data.chamber1.temperature == 0.0) "Unavailable" else if (abs(data.chamber1.temperature - 12.0) <= 2.0) "Normal" else "Warning",
                    coolingActive = data.cooling,
                    fanActive = data.fans,
                    doorOpen = data.doorOpen
                )

                _chamber2.value = _chamber2.value.copy(
                    currentTemp = data.chamber2.temperature,
                    humidity = data.chamber2.humidity,
                    status = if (data.chamber2.temperature == 0.0) "Unavailable" else if (abs(data.chamber2.temperature - 4.0) <= 2.0) "Normal" else "Warning",
                    coolingActive = data.cooling,
                    fanActive = data.fans,
                    doorOpen = data.doorOpen
                )

                _outsideTemp.value = data.outside.temperature
                _outsideHumidity.value = data.outside.humidity
                _gasStatus.value = if (data.gasStatus.equals("normal", true)) "Normal" else "Elevated"
                _coolingActive.value = data.cooling
                _fansActive.value = data.fans

                _powerData.value = _powerData.value.copy(
                    solarInputWatts = data.solarPower,
                    batteryPercent = data.battery,
                    isCharging = data.solarPower > 100
                )

                recordSensorSnapshot()
            } else {
                _deviceConnected.value = false
                _chamber1.value = _chamber1.value.copy(status = "Disconnected", currentTemp = 0.0, humidity = 0.0)
                _chamber2.value = _chamber2.value.copy(status = "Disconnected", currentTemp = 0.0, humidity = 0.0)
            }
        } catch (_: Exception) {
            _deviceConnected.value = false
            _chamber1.value = _chamber1.value.copy(status = "Disconnected", currentTemp = 0.0, humidity = 0.0)
            _chamber2.value = _chamber2.value.copy(status = "Disconnected", currentTemp = 0.0, humidity = 0.0)
        }
    }

    private suspend fun simulateTelemetryTick() {
        simulationStep++
        val now = System.currentTimeMillis()
        _lastUpdatedTime.value = now

        // Micro-fluctuations around target temps (Chamber 1 = 12°C, Chamber 2 = 4°C, Outside = 28°C)
        val c1Noise = sin(simulationStep * 0.15) * 0.4 + (Random.nextDouble(-0.15, 0.15))
        val c2Noise = cos(simulationStep * 0.12) * 0.3 + (Random.nextDouble(-0.1, 0.1))
        val outNoise = sin(simulationStep * 0.08) * 0.8 + (Random.nextDouble(-0.2, 0.2))

        val c1Temp = (12.0 + c1Noise).coerceIn(10.5, 14.5)
        val c2Temp = (4.0 + c2Noise).coerceIn(2.5, 6.0)
        val outTemp = (28.0 + outNoise).coerceIn(25.0, 32.0)

        val hum1 = (62.0 + sin(simulationStep * 0.2) * 2.0).coerceIn(58.0, 70.0)
        val hum2 = (60.0 + cos(simulationStep * 0.2) * 1.5).coerceIn(55.0, 68.0)
        val outHum = (62.0 + sin(simulationStep * 0.1) * 3.0).coerceIn(50.0, 75.0)

        // Solar & Battery simulation: Solar fluctuates slightly around 620W, Battery around 78%
        val solarW = (620.0 + sin(simulationStep * 0.1) * 25.0).coerceIn(580.0, 660.0)
        val battPct = 78
        val battVolt = 12.4 + (sin(simulationStep * 0.05) * 0.1)

        _chamber1.value = _chamber1.value.copy(
            currentTemp = (c1Temp * 10).toInt() / 10.0,
            humidity = (hum1 * 10).toInt() / 10.0,
            status = if (c1Temp > 13.5) "Warning" else "Normal"
        )

        _chamber2.value = _chamber2.value.copy(
            currentTemp = (c2Temp * 10).toInt() / 10.0,
            humidity = (hum2 * 10).toInt() / 10.0,
            status = if (c2Temp > 5.5) "Warning" else "Normal"
        )

        _outsideTemp.value = (outTemp * 10).toInt() / 10.0
        _outsideHumidity.value = (outHum * 10).toInt() / 10.0

        _powerData.value = _powerData.value.copy(
            solarInputWatts = (solarW * 10).toInt() / 10.0,
            batteryPercent = battPct,
            batteryVoltage = (battVolt * 100).toInt() / 100.0,
            batteryCurrent = 8.2,
            isCharging = true
        )

        // Save a reading snapshot to DB every 5 steps
        if (simulationStep % 5 == 0) {
            recordSensorSnapshot()
        }

        // Automatic Market Price Surge Detection: Checks crops and generates price spike notification
        if (simulationStep % 8 == 0) {
            checkAndGeneratePriceHikeAlert()
        }
    }

    private suspend fun checkAndGeneratePriceHikeAlert() {
        try {
            val highDemandCrops = com.example.data.model.IndianAgriMarketCatalog.getPriceHikeItems()
            if (highDemandCrops.isNotEmpty()) {
                val sampleCrop = highDemandCrops[simulationStep % highDemandCrops.size]
                val alertId = "PRICE-${sampleCrop.id}-${System.currentTimeMillis() / 60000}"
                val existing = alertDao.getAlertById(alertId)
                if (existing == null) {
                    val alert = AlertItem(
                        id = alertId,
                        severity = AlertSeverity.INFO,
                        title = "मंडी भाव में उछाल: ${sampleCrop.nameHindi} (+${sampleCrop.priceTrendPercent}%)",
                        message = "${sampleCrop.emoji} ${sampleCrop.nameHindi} (${sampleCrop.nameEnglish}) का भाव ₹${sampleCrop.previousPrice} से बढ़कर ₹${sampleCrop.currentPricePerKgOrUnit}/${sampleCrop.unit} हो गया है (${sampleCrop.majorState} मंडी)। कोल्ड स्टोरेज से बिक्री का उत्तम समय!",
                        target = "${sampleCrop.nameEnglish} • ₹${sampleCrop.currentPricePerKgOrUnit}/${sampleCrop.unit}",
                        timestamp = System.currentTimeMillis(),
                        isRead = false
                    )
                    alertDao.insertAlert(alert)
                }
            }
        } catch (_: Exception) {
        }
    }

    private suspend fun recordSensorSnapshot() {
        val reading = SensorReading(
            timestamp = System.currentTimeMillis(),
            chamber1Temp = _chamber1.value.currentTemp,
            chamber1Humidity = _chamber1.value.humidity,
            chamber2Temp = _chamber2.value.currentTemp,
            chamber2Humidity = _chamber2.value.humidity,
            outsideTemp = _outsideTemp.value,
            outsideHumidity = _outsideHumidity.value,
            gasStatus = _gasStatus.value,
            airQuality = _airQuality.value,
            batteryPercent = _powerData.value.batteryPercent,
            batteryVoltage = _powerData.value.batteryVoltage,
            batteryCurrent = _powerData.value.batteryCurrent,
            batteryTemp = _powerData.value.batteryTemperature,
            solarInputWatts = _powerData.value.solarInputWatts,
            coolingSystemOn = _coolingActive.value,
            fansOn = _fansActive.value,
            door1Open = _chamber1.value.doorOpen,
            door2Open = _chamber2.value.doorOpen,
            isSynced = _isOnline.value
        )
        sensorReadingDao.insertReading(reading)
        sensorReadingDao.trimOldReadings()
    }

    /**
     * Seeds initial crates, alerts, and historical readings if table is empty.
     */
    private suspend fun seedInitialDatabaseIfEmpty() {
        if (crateDao.getActiveCrateCount() == 0) {
            val now = System.currentTimeMillis()
            val dayMs = 86_400_000L
            val hourMs = 3_600_000L

            val initialCrates = listOf(
                Crate(
                    id = "CR001",
                    product = "Mango",
                    chamber = "Chamber 1",
                    quantity = 250.0,
                    unit = "kg",
                    storageTimestamp = now - (2 * hourMs),
                    expectedShelfLifeDays = 21,
                    targetTemp = 12.0,
                    targetHumidity = 85.0,
                    freshnessPercent = 85,
                    status = "Healthy",
                    notes = "Alphonso harvest from Ratnagiri lot #4A"
                ),
                Crate(
                    id = "CR002",
                    product = "Tomato",
                    chamber = "Chamber 2",
                    quantity = 180.0,
                    unit = "kg",
                    storageTimestamp = now - (4 * hourMs),
                    expectedShelfLifeDays = 14,
                    targetTemp = 4.0,
                    targetHumidity = 72.0,
                    freshnessPercent = 72,
                    status = "Healthy",
                    notes = "Vine-ripened organic batch"
                ),
                Crate(
                    id = "CR003",
                    product = "Banana",
                    chamber = "Chamber 1",
                    quantity = 320.0,
                    unit = "kg",
                    storageTimestamp = now - (1 * dayMs + 5 * hourMs),
                    expectedShelfLifeDays = 10,
                    targetTemp = 12.0,
                    targetHumidity = 75.0,
                    freshnessPercent = 45,
                    status = "Watch",
                    notes = "Ethylene sensitive. Monitor ventilation."
                ),
                Crate(
                    id = "CR004",
                    product = "Flower",
                    chamber = "Chamber 2",
                    quantity = 50.0,
                    unit = "boxes",
                    storageTimestamp = now - (2 * dayMs + 3 * hourMs),
                    expectedShelfLifeDays = 7,
                    targetTemp = 4.0,
                    targetHumidity = 60.0,
                    freshnessPercent = 28,
                    status = "Spoiled Risk",
                    notes = "Cut carnations and roses; low humidity risk."
                )
            )
            crateDao.insertCrates(initialCrates)
        }

        // Seed Alerts
        val existingAlerts = alertDao.getAllAlerts().firstOrNull() ?: emptyList()
        if (existingAlerts.isEmpty()) {
            val now = System.currentTimeMillis()
            val hourMs = 3_600_000L

            val initialAlerts = listOf(
                AlertItem(
                    id = "ALT-001",
                    severity = AlertSeverity.CRITICAL,
                    title = "Temperature High",
                    message = "Chamber 1 exceeded upper safe band by +2.0°C",
                    target = "Chamber 1 – 14°C (Target 12°C)",
                    timestamp = now - (2 * hourMs),
                    isRead = false
                ),
                AlertItem(
                    id = "ALT-002",
                    severity = AlertSeverity.WARNING,
                    title = "Gas Level Rise",
                    message = "MQ-2 sensor detected elevated ethylene and VOC levels",
                    target = "Chamber 2 – 75%",
                    timestamp = now - (4 * hourMs),
                    isRead = false
                ),
                AlertItem(
                    id = "ALT-003",
                    severity = AlertSeverity.WARNING,
                    title = "Door Open",
                    message = "Chamber 1 magnetic sensor open for more than 4 minutes",
                    target = "Chamber 1",
                    timestamp = now - (5 * hourMs),
                    isRead = false
                ),
                AlertItem(
                    id = "ALT-004",
                    severity = AlertSeverity.CRITICAL,
                    title = "Battery Low",
                    message = "Storage backup battery dropped below 20% threshold",
                    target = "Battery Level Low (20%)",
                    timestamp = now - (6 * hourMs),
                    isRead = true
                ),
                AlertItem(
                    id = "ALT-005",
                    severity = AlertSeverity.WARNING,
                    title = "Product Freshness Low",
                    message = "Crate freshness dropped to critical 28%",
                    target = "CR004 – Flower",
                    timestamp = now - (8 * hourMs),
                    isRead = true
                ),
                AlertItem(
                    id = "ALT-PRICE-001",
                    severity = AlertSeverity.INFO,
                    title = "मंडी भाव में उछाल: आम (Alphonso Mango) +25%",
                    message = "🥭 आम का थोक भाव बढ़कर ₹95/kg हो गया है (रत्नागिरी/वाशी मंडी)। कमरा 1 में रखे आम को बेचने का यह सबसे सही समय है!",
                    target = "Chamber 1 • Mango • ₹95/kg",
                    timestamp = now - (30 * 60 * 1000L),
                    isRead = false
                ),
                AlertItem(
                    id = "ALT-PRICE-002",
                    severity = AlertSeverity.INFO,
                    title = "मंडी भाव में उछाल: टमाटर (Tomato) +18%",
                    message = "🍅 टमाटर की कीमतें ₹32 से बढ़कर ₹38/kg पहुंच गई हैं (नासिक मंडी)। मुनाफा बुक करने के लिए स्टॉक चेक करें।",
                    target = "Chamber 2 • Tomato • ₹38/kg",
                    timestamp = now - (10 * 60 * 1000L),
                    isRead = false
                )
            )
            alertDao.insertAlerts(initialAlerts)
        }

        // Seed historical readings (24 hours history at 2 hour intervals)
        if (sensorReadingDao.getReadingCount() < 12) {
            val now = System.currentTimeMillis()
            val hourMs = 3_600_000L
            val historical = mutableListOf<SensorReading>()

            for (i in 12 downTo 0) {
                val t = now - (i * 2 * hourMs)
                val c1 = 12.0 + sin(i * 0.5) * 0.4
                val c2 = 4.0 + cos(i * 0.5) * 0.3
                val out = 27.5 + sin(i * 0.3) * 1.5

                historical.add(
                    SensorReading(
                        timestamp = t,
                        chamber1Temp = (c1 * 10).toInt() / 10.0,
                        chamber1Humidity = 62.0 + sin(i * 0.4) * 2.0,
                        chamber2Temp = (c2 * 10).toInt() / 10.0,
                        chamber2Humidity = 60.0 + cos(i * 0.4) * 2.0,
                        outsideTemp = (out * 10).toInt() / 10.0,
                        outsideHumidity = 62.0 + cos(i * 0.3) * 3.0,
                        solarInputWatts = max(0.0, 620.0 * sin((12 - i) / 12.0 * Math.PI)),
                        batteryPercent = (75 + i % 5),
                        isSynced = true
                    )
                )
            }
            sensorReadingDao.insertReadings(historical)
        }
    }

    // Crate actions
    suspend fun addCrate(crate: Crate) {
        crateDao.insertCrate(crate)
    }

    suspend fun updateCrate(crate: Crate) {
        crateDao.updateCrate(crate)
    }

    suspend fun archiveCrate(crateId: String) {
        crateDao.archiveCrate(crateId)
    }

    suspend fun getCrateById(crateId: String): Crate? {
        return crateDao.getCrateById(crateId)
    }

    // Alert actions
    suspend fun markAlertAsRead(alertId: String) {
        alertDao.markAsRead(alertId)
    }

    suspend fun markAllAlertsAsRead() {
        alertDao.markAllAsRead()
    }

    suspend fun deleteAlert(alertId: String) {
        alertDao.deleteAlert(alertId)
    }

    /**
     * Calculates scientific shelf-life prediction based on produce biology,
     * storage duration, temperature delta (Q10 food degradation model), and humidity.
     * Integrates full Indian agricultural catalog with market pricing.
     */
    fun calculateShelfLifePrediction(
        product: String,
        harvestDate: String,
        chamber: String,
        currentTemp: Double,
        currentHumidity: Double
    ): ShelfLifePrediction {
        // Find matching crop profile from Indian Agri Catalog
        val matchedCrop = com.example.data.model.IndianAgriMarketCatalog.findByName(product)
            ?: com.example.data.model.IndianAgriMarketCatalog.allProduces.first()

        val baseDays = matchedCrop.baseShelfLifeDays
        val optimalTemp = matchedCrop.optimalTemp
        val optimalHumidity = matchedCrop.optimalHumidity
        val tempTolerance = matchedCrop.tempTolerance

        // Q10 acceleration calculation: For every 10°C above optimal, degradation rate multiplies by ~2.2x
        val tempDelta = max(0.0, currentTemp - optimalTemp)
        val q10Factor = 2.2.pow(tempDelta / 10.0)

        // Humidity deficit penalty
        val humidityDeficit = max(0.0, optimalHumidity - currentHumidity)
        val humidityPenaltyFactor = 1.0 + (humidityDeficit / 100.0) * 0.8

        val totalDegradationRate = q10Factor * humidityPenaltyFactor

        // Execute 15-Feature Edge Neural Network (auto-generated by train.py)
        val neuralInputs = floatArrayOf(
            currentTemp.toFloat(),
            (currentTemp - optimalTemp).toFloat(),
            0.02f,
            (currentHumidity / 100.0).toFloat(),
            28.5f,
            0.12f,
            totalDegradationRate.toFloat(),
            0.0f,
            0.42f,
            0.85f,
            4.0f,
            0.30f,
            0.88f,
            0.82f,
            0.05f
        )
        val edgeInference = com.example.data.ai.VisionXEdgeNeuralModel.runInference(neuralInputs, product)

        val estimatedDaysRemaining = max(1, min(edgeInference.predictedShelfLifeDays, (baseDays / totalDegradationRate).toInt()))
        val confidence = edgeInference.confidencePercent

        val recommendation = when {
            tempDelta > 2.0 -> "Temperature is ${String.format("%.1f", tempDelta)}°C above optimal ($optimalTemp°C). Cooling adjustment can recover up to ${(baseDays * 0.3).toInt()} days of shelf life."
            humidityDeficit > 15.0 -> "Relative humidity is below target ($optimalHumidity%). Humidification recommended to prevent premature transpirational wilting."
            else -> "Storage chamber conditions are optimal. Respiration rate is stabilized within target range."
        }

        return ShelfLifePrediction(
            product = matchedCrop.nameEnglish,
            estimatedDays = estimatedDaysRemaining,
            confidencePercent = confidence,
            currentTemp = currentTemp,
            currentHumidity = currentHumidity,
            harvestDate = harvestDate,
            optimalTempRange = "$optimalTemp°C ± $tempTolerance°C",
            optimalHumidityRange = "${(optimalHumidity - 5).toInt()}% – ${(optimalHumidity + 5).toInt()}%",
            accelerationFactor = (totalDegradationRate * 100).toInt() / 100.0,
            recommendation = recommendation,
            currentMarketPrice = matchedCrop.currentPricePerKgOrUnit,
            priceUnit = matchedCrop.unit,
            majorState = matchedCrop.majorState,
            priceTrendPercent = matchedCrop.priceTrendPercent,
            cropCategory = matchedCrop.category.labelHindi,
            produceEmoji = matchedCrop.emoji
        )
    }

    private data class Quad(val first: Int, val second: Double, val third: Double, val fourth: Double)
}

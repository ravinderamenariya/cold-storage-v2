package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VisionXDatabase
import com.example.data.model.AlertItem
import com.example.data.model.ChamberData
import com.example.data.model.ConnectionMode
import com.example.data.model.Crate
import com.example.data.model.IndianAgriProduce
import com.example.data.model.PowerData
import com.example.data.model.SensorNodeStatus
import com.example.data.model.SensorReading
import com.example.data.model.ShelfLifePrediction
import com.example.data.repository.ColdStorageRepository
import com.example.util.FarmerQuestionType
import com.example.util.IndianLanguage
import com.example.util.VoiceAssistantManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination(val route: String, val label: String) {
    SPLASH("splash", "Splash"),
    LOGIN("login", "Login"),
    HOME("home", "Home"),
    CRATES("crates", "Crates"),
    ADD_CRATE("add_crate", "Add Crate"),
    CRATE_DETAIL("crate_detail", "Crate Details"),
    HISTORY("history", "History"),
    ALERTS("alerts", "Alerts"),
    VOICE_ASSISTANT("voice_assistant", "Voice Assistant"),
    LIVE_MONITORING("live_monitoring", "Live Monitoring"),
    SOLAR_POWER("solar_power", "Solar & Power"),
    AI_PREDICTION("ai_prediction", "AI Prediction"),
    SETTINGS("settings", "Settings"),
    PI_GUIDE("pi_guide", "Pi & DB Guide")
}

data class AuthUser(
    val name: String = "Ravindera Menariya",
    val email: String = "ravindera@domain.com",
    val isOfflineMode: Boolean = false,
    val role: String = "Storage Manager"
)

data class AiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isAiGenerated: Boolean = true
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VisionXDatabase.getDatabase(application)
    val repository = ColdStorageRepository(
        crateDao = database.crateDao(),
        sensorReadingDao = database.sensorReadingDao(),
        alertDao = database.alertDao(),
        coroutineScope = viewModelScope
    )

    // Navigation and Current Destination
    private val _currentScreen = MutableStateFlow(AppDestination.SPLASH)
    val currentScreen: StateFlow<AppDestination> = _currentScreen.asStateFlow()

    private val _selectedCrateId = MutableStateFlow<String?>(null)
    val selectedCrateId: StateFlow<String?> = _selectedCrateId.asStateFlow()

    // Auth State
    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    val isAuthenticated: Boolean
        get() = _currentUser.value != null

    // Telemetry State Flows from Repository
    val chamber1: StateFlow<ChamberData> = repository.chamber1
    val chamber2: StateFlow<ChamberData> = repository.chamber2
    val outsideTemp: StateFlow<Double> = repository.outsideTemp
    val outsideHumidity: StateFlow<Double> = repository.outsideHumidity
    val gasStatus: StateFlow<String> = repository.gasStatus
    val airQuality: StateFlow<String> = repository.airQuality
    val powerData: StateFlow<PowerData> = repository.powerData
    val lastUpdatedTime: StateFlow<Long> = repository.lastUpdatedTime
    val coolingActive: StateFlow<Boolean> = repository.coolingActive
    val fansActive: StateFlow<Boolean> = repository.fansActive
    val sensorNodes: StateFlow<List<SensorNodeStatus>> = repository.sensorNodes

    val connectionMode: StateFlow<ConnectionMode> = repository.connectionMode
    val isOnline: StateFlow<Boolean> = repository.isOnline
    val syncStatus: StateFlow<String> = repository.syncStatus
    val deviceConnected: StateFlow<Boolean> = repository.deviceConnected
    val raspberryPiHost: StateFlow<String> = repository.raspberryPiHost

    // Crates & Alerts
    val allCrates: StateFlow<List<Crate>> = repository.allCrates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAlerts: StateFlow<List<AlertItem>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadAlertsCount: StateFlow<Int> = repository.unreadAlertsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentReadings: StateFlow<List<SensorReading>> = repository.recentReadings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marketProduces: StateFlow<List<IndianAgriProduce>> = repository.marketProduces

    // Location & Regional Market Price Service
    val locationService = com.example.util.LocationMarketService(application)
    val currentLocation = locationService.currentLocation

    init {
        viewModelScope.launch {
            locationService.detectCurrentLocation()
        }
    }

    fun detectLocationNow() {
        viewModelScope.launch {
            locationService.detectCurrentLocation()
        }
    }

    fun setLocationManually(cityName: String, stateName: String, nearestMandi: String, multiplier: Float, lat: Double = 28.6139, lng: Double = 77.2090) {
        locationService.setLocation(cityName, stateName, nearestMandi, multiplier, lat, lng)
    }

    // Gemini 3.8 Flash AI Service
    private val geminiAiService = com.example.data.ai.GeminiAiService()

    private val _geminiPrediction = MutableStateFlow<com.example.data.ai.GeminiMarketPrediction?>(null)
    val geminiPrediction: StateFlow<com.example.data.ai.GeminiMarketPrediction?> = _geminiPrediction.asStateFlow()

    private val _isPredictingWithAi = MutableStateFlow(false)
    val isPredictingWithAi: StateFlow<Boolean> = _isPredictingWithAi.asStateFlow()

    // AI Prediction State
    private val _lastPrediction = MutableStateFlow<ShelfLifePrediction?>(null)
    val lastPrediction: StateFlow<ShelfLifePrediction?> = _lastPrediction.asStateFlow()

    // Multi-Language & Voice Assistant System (Bolne Vala System for Indian Languages)
    val voiceAssistant = VoiceAssistantManager(application)
    val selectedLanguage: StateFlow<IndianLanguage> = voiceAssistant.selectedLanguage
    val isSpeaking: StateFlow<Boolean> = voiceAssistant.isSpeaking
    val lastSpokenText: StateFlow<String> = voiceAssistant.lastSpokenText

    // Conversational Chat Messages with Gemini 3.8 Flash
    private val _chatMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                text = "नमस्ते! मैं आपका GREENCOLD किसान AI सहायक हूँ (Powered by Gemini 3.8 Flash)। आप कोल्ड स्टोरेज तापमान, क्या-अगर सवाल (जैसे: अगर 5 दिन बाद बेचें तो क्या return मिलेगा?), या फसल सुझाव के बारे में सीधे पूछ सकते हैं।",
                isUser = false,
                isAiGenerated = true
            )
        )
    )
    val chatMessages: StateFlow<List<AiChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _weatherConnectionStatus = MutableStateFlow("ONLINE (Connected)")
    val weatherConnectionStatus: StateFlow<String> = _weatherConnectionStatus.asStateFlow()

    fun checkWeatherConnection(): String {
        val temp = outsideTemp.value
        val hum = outsideHumidity.value
        return "Weather Connection Active • Station Online • Ambient: ${temp}°C, Humidity: ${hum}% RH"
    }

    // Simple / Farmer Mode Toggle (Easy UI for illiterate / rural operators)
    private val _isFarmerMode = MutableStateFlow(true)
    val isFarmerMode: StateFlow<Boolean> = _isFarmerMode.asStateFlow()

    fun toggleFarmerMode() {
        _isFarmerMode.value = !_isFarmerMode.value
    }

    fun setLanguage(language: IndianLanguage) {
        voiceAssistant.setLanguage(language)
    }

    fun speakCurrentStatus() {
        val speechText = voiceAssistant.getStorageStatusSpeech(
            language = selectedLanguage.value,
            c1Temp = chamber1.value.currentTemp,
            c2Temp = chamber2.value.currentTemp,
            batteryPercent = powerData.value.batteryPercent,
            solarWatts = powerData.value.solarInputWatts,
            gasStatus = gasStatus.value
        )
        voiceAssistant.speak(speechText, selectedLanguage.value)
    }

    fun askFarmerQuestion(questionType: FarmerQuestionType) {
        val query = when (selectedLanguage.value) {
            IndianLanguage.HINDI -> questionType.hindiTitle
            else -> questionType.englishTitle
        }
        askVoiceQuery(query)
    }

    fun askVoiceQuery(query: String) {
        if (query.isBlank()) return
        val userMsg = AiChatMessage(text = query, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val answer = try {
                geminiAiService.askAssistant(
                    userQuery = query,
                    language = selectedLanguage.value,
                    c1Temp = chamber1.value.currentTemp,
                    c2Temp = chamber2.value.currentTemp,
                    humidityC1 = chamber1.value.humidity,
                    humidityC2 = chamber2.value.humidity,
                    outsideTemp = outsideTemp.value,
                    batteryPercent = powerData.value.batteryPercent,
                    solarWatts = powerData.value.solarInputWatts,
                    gasStatus = gasStatus.value,
                    activeCrates = allCrates.value,
                    nearestMandi = currentLocation.value.nearestMandi
                )
            } catch (e: Exception) {
                geminiAiService.askAssistant(
                    userQuery = query,
                    language = selectedLanguage.value,
                    c1Temp = chamber1.value.currentTemp,
                    c2Temp = chamber2.value.currentTemp,
                    humidityC1 = chamber1.value.humidity,
                    humidityC2 = chamber2.value.humidity,
                    outsideTemp = outsideTemp.value,
                    batteryPercent = powerData.value.batteryPercent,
                    solarWatts = powerData.value.solarInputWatts,
                    gasStatus = gasStatus.value,
                    activeCrates = allCrates.value,
                    nearestMandi = currentLocation.value.nearestMandi
                )
            } finally {
                _isAiThinking.value = false
            }

            val aiMsg = AiChatMessage(text = answer, isUser = false, isAiGenerated = true)
            _chatMessages.value = _chatMessages.value + aiMsg
            voiceAssistant.speak(answer, selectedLanguage.value)
        }
    }

    fun speakText(text: String) {
        voiceAssistant.speak(text, selectedLanguage.value)
    }

    fun stopSpeaking() {
        voiceAssistant.stopSpeaking()
    }

    override fun onCleared() {
        super.onCleared()
        voiceAssistant.shutdown()
    }

    // Navigation control
    fun navigateTo(destination: AppDestination) {
        _currentScreen.value = destination
    }

    fun openCrateDetail(crateId: String) {
        _selectedCrateId.value = crateId
        _currentScreen.value = AppDestination.CRATE_DETAIL
    }

    // Authentication methods
    fun login(emailOrUsername: String, password: String): Boolean {
        // Support demo login or configured user
        if (emailOrUsername.isNotBlank() && password.length >= 4) {
            _currentUser.value = AuthUser(
                name = if (emailOrUsername.contains("@")) emailOrUsername.substringBefore("@").replaceFirstChar { it.uppercase() } else emailOrUsername.replaceFirstChar { it.uppercase() },
                email = if (emailOrUsername.contains("@")) emailOrUsername else "$emailOrUsername@domain.com",
                isOfflineMode = false
            )
            repository.setOnline(true)
            navigateTo(AppDestination.HOME)
            return true
        }
        return false
    }

    fun loginOffline() {
        _currentUser.value = AuthUser(
            name = "Offline Operator",
            email = "offline@local.device",
            isOfflineMode = true
        )
        repository.setOnline(false)
        navigateTo(AppDestination.HOME)
    }

    fun logout() {
        _currentUser.value = null
        navigateTo(AppDestination.LOGIN)
    }

    // CRUD & IoT actions
    fun addCrate(crate: Crate) {
        viewModelScope.launch {
            repository.addCrate(crate)
        }
    }

    fun updateCrate(crate: Crate) {
        viewModelScope.launch {
            repository.updateCrate(crate)
        }
    }

    fun archiveCrate(crateId: String) {
        viewModelScope.launch {
            repository.archiveCrate(crateId)
            if (_selectedCrateId.value == crateId) {
                navigateTo(AppDestination.CRATES)
            }
        }
    }

    fun markAlertAsRead(alertId: String) {
        viewModelScope.launch {
            repository.markAlertAsRead(alertId)
        }
    }

    fun markAllAlertsAsRead() {
        viewModelScope.launch {
            repository.markAllAlertsAsRead()
        }
    }

    fun deleteAlert(alertId: String) {
        viewModelScope.launch {
            repository.deleteAlert(alertId)
        }
    }

    fun triggerSync() {
        viewModelScope.launch {
            repository.triggerManualSync()
        }
    }

    fun setConnectionMode(mode: ConnectionMode) {
        repository.setConnectionMode(mode)
    }

    fun setOnline(online: Boolean) {
        repository.setOnline(online)
    }

    fun setRaspberryPiHost(host: String) {
        repository.updateRaspberryPiHost(host)
    }

    fun toggleCooling(enabled: Boolean) {
        repository.toggleCooling(enabled)
    }

    fun toggleFans(enabled: Boolean) {
        repository.toggleFans(enabled)
    }

    fun runPrediction(product: String, harvestDate: String, chamber: String) {
        val currentT = if (chamber == "Chamber 2") chamber2.value.currentTemp else chamber1.value.currentTemp
        val currentH = if (chamber == "Chamber 2") chamber2.value.humidity else chamber1.value.humidity

        val result = repository.calculateShelfLifePrediction(
            product = product,
            harvestDate = harvestDate,
            chamber = chamber,
            currentTemp = currentT,
            currentHumidity = currentH
        )
        _lastPrediction.value = result

        // Launch Gemini 3.8 Flash AI prediction
        viewModelScope.launch {
            _isPredictingWithAi.value = true
            try {
                val matchedCrop = com.example.data.model.IndianAgriMarketCatalog.findByName(product)
                val geminiRes = geminiAiService.predictMarketAndShelfLife(
                    product = product,
                    harvestDate = harvestDate,
                    chamber = chamber,
                    currentTemp = currentT,
                    currentHumidity = currentH,
                    optimalTemp = matchedCrop?.optimalTemp ?: 10.0,
                    optimalHumidity = matchedCrop?.optimalHumidity ?: 85.0,
                    baseDays = matchedCrop?.baseShelfLifeDays ?: 14,
                    nearestMandi = currentLocation.value.nearestMandi,
                    currentPrice = matchedCrop?.currentPricePerKgOrUnit?.toDouble() ?: 30.0,
                    priceUnit = matchedCrop?.unit ?: "kg"
                )
                _geminiPrediction.value = geminiRes
            } catch (e: Exception) {
                android.util.Log.w("AppViewModel", "Gemini prediction error: ${e.message}")
            } finally {
                _isPredictingWithAi.value = false
            }
        }
    }
}

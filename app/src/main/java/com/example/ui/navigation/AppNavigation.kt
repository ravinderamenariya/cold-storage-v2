package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppDestination
import com.example.ui.AppViewModel
import com.example.ui.components.VisionXAdaptiveSidebar
import com.example.ui.components.VisionXBottomNavBar
import com.example.ui.screens.AddCrateScreen
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.CrateDetailScreen
import com.example.ui.screens.CratesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LiveMonitoringScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PiDatabaseGuideScreen
import com.example.ui.screens.PredictionScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SolarPowerScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VoiceAssistantScreen
import com.example.util.AppTranslations
import com.example.ui.theme.VxEmerald
import com.example.ui.theme.VxNavy
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxPurple
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxSurfaceGreen
import com.example.ui.theme.VxSurfacePurple
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxSurfaceWarning
import com.example.ui.theme.VxTheme
import com.example.ui.theme.VxWarningAmber
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val unreadAlertCount by viewModel.unreadAlertsCount.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val crates by viewModel.allCrates.collectAsState()
    val alerts by viewModel.allAlerts.collectAsState()
    val selectedCrateId by viewModel.selectedCrateId.collectAsState()

    val chamber1 by viewModel.chamber1.collectAsState()
    val chamber2 by viewModel.chamber2.collectAsState()
    val outsideTemp by viewModel.outsideTemp.collectAsState()
    val outsideHumidity by viewModel.outsideHumidity.collectAsState()
    val gasStatus by viewModel.gasStatus.collectAsState()
    val airQuality by viewModel.airQuality.collectAsState()
    val powerData by viewModel.powerData.collectAsState()
    val lastUpdatedTime by viewModel.lastUpdatedTime.collectAsState()
    val coolingActive by viewModel.coolingActive.collectAsState()
    val fansActive by viewModel.fansActive.collectAsState()
    val deviceConnected by viewModel.deviceConnected.collectAsState()
    val sensorNodes by viewModel.sensorNodes.collectAsState()
    val connectionMode by viewModel.connectionMode.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val lastPrediction by viewModel.lastPrediction.collectAsState()
    val recentReadings by viewModel.recentReadings.collectAsState()
    val marketProduces by viewModel.marketProduces.collectAsState()
    val rpiHost by viewModel.raspberryPiHost.collectAsState()

    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val lastSpokenText by viewModel.lastSpokenText.collectAsState()
    val isFarmerMode by viewModel.isFarmerMode.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()

    var showMoreSheet by remember { mutableStateOf(false) }
    var showLocationSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    if (showLocationSheet) {
        com.example.ui.components.LocationSelectionBottomSheet(
            currentLocation = currentLocation,
            onSelectLocation = { city, state, mandi, mult, lat, lng ->
                viewModel.setLocationManually(city, state, mandi, mult, lat, lng)
                showLocationSheet = false
            },
            onDetectGpsLocation = {
                viewModel.detectLocationNow()
                showLocationSheet = false
            },
            onDismiss = { showLocationSheet = false }
        )
    }

    // Handle system back navigation
    BackHandler(enabled = currentScreen !in listOf(AppDestination.SPLASH, AppDestination.LOGIN, AppDestination.HOME)) {
        when (currentScreen) {
            AppDestination.ADD_CRATE, AppDestination.CRATE_DETAIL -> viewModel.navigateTo(AppDestination.CRATES)
            AppDestination.VOICE_ASSISTANT -> viewModel.navigateTo(AppDestination.HOME)
            else -> viewModel.navigateTo(AppDestination.HOME)
        }
    }

    val showBottomBar = currentScreen !in listOf(AppDestination.SPLASH, AppDestination.LOGIN)

    @Composable
    fun RenderActiveScreen(contentModifier: Modifier) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                (slideInHorizontally { width -> width / 4 } + fadeIn())
                    .togetherWith(slideOutHorizontally { width -> -width / 4 } + fadeOut())
            },
            label = "screenTransition",
            modifier = contentModifier
        ) { targetScreen ->
            when (targetScreen) {
                AppDestination.SPLASH -> {
                    SplashScreen(
                        onSplashFinished = {
                            viewModel.navigateTo(AppDestination.LOGIN)
                        }
                    )
                }

                AppDestination.LOGIN -> {
                    LoginScreen(
                        onLoginSuccess = { username, isOffline ->
                            if (isOffline) {
                                viewModel.loginOffline()
                            } else {
                                viewModel.login(username, "password")
                            }
                        }
                    )
                }

                AppDestination.HOME -> {
                    DashboardScreen(
                        chamber1 = chamber1,
                        chamber2 = chamber2,
                        outsideTemp = outsideTemp,
                        outsideHumidity = outsideHumidity,
                        gasStatus = gasStatus,
                        airQuality = airQuality,
                        powerData = powerData,
                        coolingActive = coolingActive,
                        fansActive = fansActive,
                        onToggleCooling = { viewModel.toggleCooling(it) },
                        onToggleFans = { viewModel.toggleFans(it) },
                        crates = crates,
                        alerts = alerts,
                        isOnline = isOnline,
                        connectionMode = connectionMode,
                        onNavigate = { dest -> viewModel.navigateTo(dest) },
                        onCrateClick = { id -> viewModel.openCrateDetail(id) },
                        onAlertClick = { id -> viewModel.markAlertAsRead(id) },
                        onDismissAlert = { id -> viewModel.deleteAlert(id) },
                        selectedLanguage = selectedLanguage,
                        isFarmerMode = isFarmerMode,
                        onToggleFarmerMode = { viewModel.toggleFarmerMode() },
                        onSelectLanguage = { viewModel.setLanguage(it) },
                        onSpeakStatus = { viewModel.speakCurrentStatus() },
                        onSpeakText = { viewModel.speakText(it) },
                        onOpenVoiceAssistant = { viewModel.navigateTo(AppDestination.VOICE_ASSISTANT) },
                        currentLocation = currentLocation,
                        onOpenLocationSheet = { showLocationSheet = true },
                        onCheckWeatherConnection = { viewModel.checkWeatherConnection() }
                    )
                }

                AppDestination.VOICE_ASSISTANT -> {
                    VoiceAssistantScreen(
                        selectedLanguage = selectedLanguage,
                        isSpeaking = isSpeaking,
                        lastSpokenText = lastSpokenText,
                        chatMessages = chatMessages,
                        isAiThinking = isAiThinking,
                        onSelectLanguage = { viewModel.setLanguage(it) },
                        onSpeakCurrentStatus = { viewModel.speakCurrentStatus() },
                        onAskFarmerQuestion = { viewModel.askFarmerQuestion(it) },
                        onAskVoiceQuery = { viewModel.askVoiceQuery(it) },
                        onStopSpeaking = { viewModel.stopSpeaking() },
                        onSpeakText = { viewModel.speakText(it) },
                        onBack = { viewModel.navigateTo(AppDestination.HOME) }
                    )
                }

                AppDestination.LIVE_MONITORING -> {
                    LiveMonitoringScreen(
                        chamber1 = chamber1,
                        chamber2 = chamber2,
                        outsideTemp = outsideTemp,
                        outsideHumidity = outsideHumidity,
                        gasStatus = gasStatus,
                        airQuality = airQuality,
                        powerData = powerData,
                        coolingActive = coolingActive,
                        fansActive = fansActive,
                        deviceConnected = deviceConnected,
                        lastUpdatedTime = lastUpdatedTime,
                        sensorNodes = sensorNodes,
                        connectionMode = connectionMode,
                        isOnline = isOnline,
                        onToggleCooling = { viewModel.toggleCooling(it) },
                        onToggleFans = { viewModel.toggleFans(it) },
                        onBack = { viewModel.navigateTo(AppDestination.HOME) }
                    )
                }

                AppDestination.CRATES -> {
                    CratesScreen(
                        crates = crates,
                        marketProduces = marketProduces,
                        isOnline = isOnline,
                        onCrateClick = { id -> viewModel.openCrateDetail(id) },
                        onAddCrateClick = { viewModel.navigateTo(AppDestination.ADD_CRATE) },
                        onBack = { viewModel.navigateTo(AppDestination.HOME) }
                    )
                }

                AppDestination.ADD_CRATE -> {
                    AddCrateScreen(
                        nextCrateNumber = crates.size + 1,
                        isOnline = isOnline,
                        onSaveCrate = { crate ->
                            viewModel.addCrate(crate)
                            viewModel.navigateTo(AppDestination.CRATES)
                        },
                        onCancel = { viewModel.navigateTo(AppDestination.CRATES) }
                    )
                }

                AppDestination.CRATE_DETAIL -> {
                    val activeCrate = crates.find { it.id == selectedCrateId }
                    val chamberData = if (activeCrate?.chamber == "Chamber 2") chamber2 else chamber1
                    val relatedAlerts = alerts.filter { it.target.contains(activeCrate?.id ?: "", ignoreCase = true) }

                    CrateDetailScreen(
                        crate = activeCrate,
                        currentChamberData = chamberData,
                        relatedAlerts = relatedAlerts,
                        isOnline = isOnline,
                        onArchiveCrate = { id -> viewModel.archiveCrate(id) },
                        onBack = { viewModel.navigateTo(AppDestination.CRATES) }
                    )
                }

                AppDestination.HISTORY -> {
                    HistoryScreen(
                        recentReadings = recentReadings,
                        isOnline = isOnline,
                        onBack = { viewModel.navigateTo(AppDestination.HOME) }
                    )
                }

                AppDestination.ALERTS -> {
                    AlertsScreen(
                        alerts = alerts,
                        isOnline = isOnline,
                        onMarkAsRead = { id -> viewModel.markAlertAsRead(id) },
                        onDismissAlert = { id -> viewModel.deleteAlert(id) },
                        onMarkAllAsRead = { viewModel.markAllAlertsAsRead() },
                        onBack = { viewModel.navigateTo(AppDestination.HOME) }
                    )
                }

                AppDestination.SOLAR_POWER -> {
                    SolarPowerScreen(
                        powerData = powerData,
                        lastUpdatedTime = lastUpdatedTime,
                        isOnline = isOnline,
                        onBack = { viewModel.navigateTo(AppDestination.HOME) }
                    )
                }

                AppDestination.AI_PREDICTION -> {
                    val currentLocation by viewModel.currentLocation.collectAsState()
                    val geminiPrediction by viewModel.geminiPrediction.collectAsState()
                    val isPredictingWithAi by viewModel.isPredictingWithAi.collectAsState()
                    PredictionScreen(
                        chamber1 = chamber1,
                        chamber2 = chamber2,
                        lastPrediction = lastPrediction,
                        geminiPrediction = geminiPrediction,
                        isAiPredicting = isPredictingWithAi,
                        isOnline = isOnline,
                        marketProduces = marketProduces,
                        currentLocationInfo = currentLocation,
                        onDetectLocation = { viewModel.detectLocationNow() },
                        onRunPrediction = { prod, date, chamber ->
                            viewModel.runPrediction(prod, date, chamber)
                        },
                        onBack = { viewModel.navigateTo(AppDestination.HOME) }
                    )
                }

                AppDestination.SETTINGS -> {
                    SettingsScreen(
                        user = user,
                        connectionMode = connectionMode,
                        raspberryPiHost = rpiHost,
                        deviceConnected = deviceConnected,
                        isOnline = isOnline,
                        syncStatus = syncStatus,
                        onSetConnectionMode = { viewModel.setConnectionMode(it) },
                        onSetRaspberryPiHost = { viewModel.setRaspberryPiHost(it) },
                        onTriggerSync = { viewModel.triggerSync() },
                        onToggleOnline = { viewModel.setOnline(it) },
                        onOpenPiGuide = { viewModel.navigateTo(AppDestination.PI_GUIDE) },
                        onLogout = { viewModel.logout() },
                        onBack = { viewModel.navigateTo(AppDestination.HOME) }
                    )
                }

                AppDestination.PI_GUIDE -> {
                    PiDatabaseGuideScreen(
                        currentHost = rpiHost,
                        deviceConnected = deviceConnected,
                        isOnline = isOnline,
                        onSaveHost = { viewModel.setRaspberryPiHost(it) },
                        onBack = { viewModel.navigateTo(AppDestination.SETTINGS) }
                    )
                }
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp
        val isExpanded = maxWidth >= 960.dp

        if (isWideScreen && showBottomBar) {
            // Adaptive Desktop, MacBook, iMac, and Tablet Responsive Layout
            Row(modifier = Modifier.fillMaxSize()) {
                VisionXAdaptiveSidebar(
                    currentScreen = currentScreen,
                    unreadAlertCount = unreadAlertCount,
                    isOnline = isOnline,
                    connectionMode = connectionMode,
                    isExpanded = isExpanded,
                    onNavigate = { dest -> viewModel.navigateTo(dest) }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 1200.dp)
                    ) {
                        RenderActiveScreen(contentModifier = Modifier.fillMaxSize())
                    }
                }
            }
        } else {
            // Mobile (iPhone, Android) Layout with Bottom Navigation Bar
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        VisionXBottomNavBar(
                            currentScreen = currentScreen,
                            unreadAlertCount = unreadAlertCount,
                            onNavigate = { dest -> viewModel.navigateTo(dest) },
                            onOpenMoreMenu = { showMoreSheet = true },
                            language = selectedLanguage
                        )
                    }
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier.fillMaxSize()
            ) { scaffoldPadding ->
                RenderActiveScreen(
                    contentModifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (showBottomBar) scaffoldPadding.calculateBottomPadding() else 0.dp)
                )
            }
        }
    }

    // "More" Modal Bottom Sheet
    if (showMoreSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Control Centre Modules",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Quick navigation to advanced cold-chain features",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                MoreMenuItem(
                    icon = Icons.Default.Mic,
                    title = "Kisan Voice Assistant",
                    subtitle = "बोलने वाला साथी - Indian languages speech Q&A",
                    iconColor = VxTheme.primaryBlue,
                    bgColor = VxTheme.surfaceBlue,
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        showMoreSheet = false
                        viewModel.navigateTo(AppDestination.VOICE_ASSISTANT)
                    }
                )

                MoreMenuItem(
                    icon = Icons.Default.Sensors,
                    title = "Live Monitoring",
                    subtitle = "Real-time dual chamber & sensor telemetry",
                    iconColor = VxTheme.primaryBlue,
                    bgColor = VxTheme.surfaceBlue,
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        showMoreSheet = false
                        viewModel.navigateTo(AppDestination.LIVE_MONITORING)
                    }
                )

                MoreMenuItem(
                    icon = Icons.Default.SolarPower,
                    title = "Solar & Power",
                    subtitle = "Solar PV, Battery 78%, and MPPT Power Flow",
                    iconColor = VxTheme.warningDark,
                    bgColor = VxTheme.surfaceWarning,
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        showMoreSheet = false
                        viewModel.navigateTo(AppDestination.SOLAR_POWER)
                    }
                )

                MoreMenuItem(
                    icon = Icons.Default.AutoAwesome,
                    title = "AI Prediction",
                    subtitle = "Produce shelf-life modeling & degradation forecast",
                    iconColor = VxTheme.purple,
                    bgColor = VxTheme.surfacePurple,
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        showMoreSheet = false
                        viewModel.navigateTo(AppDestination.AI_PREDICTION)
                    }
                )

                MoreMenuItem(
                    icon = Icons.Default.DeveloperBoard,
                    title = "Device Connection",
                    subtitle = "Configure Raspberry Pi 5 IP & sync mode",
                    iconColor = VxTheme.emeraldDark,
                    bgColor = VxTheme.surfaceGreen,
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        showMoreSheet = false
                        viewModel.navigateTo(AppDestination.SETTINGS)
                    }
                )

                MoreMenuItem(
                    icon = Icons.Default.Info,
                    title = "Pi & Database Guide",
                    subtitle = "Wiring diagrams, SQLite schema & Python server",
                    iconColor = VxTheme.primaryBlue,
                    bgColor = VxTheme.surfaceBlue,
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        showMoreSheet = false
                        viewModel.navigateTo(AppDestination.PI_GUIDE)
                    }
                )

                MoreMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    subtitle = "Preferences, notifications, and profile",
                    iconColor = VxTheme.primaryBlue,
                    bgColor = VxTheme.surfaceSubtle,
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        showMoreSheet = false
                        viewModel.navigateTo(AppDestination.SETTINGS)
                    }
                )
            }
        }
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("more_menu_${title.replace(" ", "_").lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

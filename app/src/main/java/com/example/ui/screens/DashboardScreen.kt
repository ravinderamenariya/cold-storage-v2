package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertItem
import com.example.data.model.ChamberData
import com.example.data.model.ConnectionMode
import com.example.data.model.Crate
import com.example.data.model.PowerData
import com.example.ui.AppDestination
import com.example.ui.components.AlertCard
import com.example.ui.components.CrateCard
import com.example.ui.components.FarmerDashboardCard
import com.example.ui.components.LanguagePickerDialog
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxTextPrimary
import com.example.ui.theme.VxTextSecondary
import com.example.util.AppTranslations
import com.example.util.IndianLanguage
import com.example.util.UserLocationInfo

/**
 * Clean, minimal, and uncluttered Dashboard Screen.
 * Shows only the core essentials: Status & Readings, Direct Controls,
 * Voice AI Assistant, and Recent Crates & Alerts.
 */
@Composable
fun DashboardScreen(
    chamber1: ChamberData,
    chamber2: ChamberData,
    outsideTemp: Double,
    outsideHumidity: Double,
    gasStatus: String,
    airQuality: String,
    powerData: PowerData,
    coolingActive: Boolean = true,
    fansActive: Boolean = true,
    onToggleCooling: (Boolean) -> Unit = {},
    onToggleFans: (Boolean) -> Unit = {},
    crates: List<Crate>,
    alerts: List<AlertItem>,
    isOnline: Boolean,
    connectionMode: ConnectionMode,
    onNavigate: (AppDestination) -> Unit,
    onCrateClick: (String) -> Unit,
    onAlertClick: (String) -> Unit,
    onDismissAlert: (String) -> Unit,
    selectedLanguage: IndianLanguage = IndianLanguage.HINDI,
    isFarmerMode: Boolean = true,
    onToggleFarmerMode: () -> Unit = {},
    onSelectLanguage: (IndianLanguage) -> Unit = {},
    onSpeakStatus: () -> Unit = {},
    onSpeakText: (String) -> Unit = {},
    onOpenVoiceAssistant: () -> Unit = {},
    currentLocation: UserLocationInfo = UserLocationInfo("Delhi", "Delhi NCR", "Azadpur Mandi, Delhi", 1.08f),
    onOpenLocationSheet: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLanguageDialog by remember { mutableStateOf(false) }

    val isAllSafe = chamber1.status == "Normal" && chamber2.status == "Normal"

    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentLanguage = selectedLanguage,
            onSelectLanguage = onSelectLanguage,
            onDismiss = { showLanguageDialog = false }
        )
    }

    Scaffold(
        topBar = {
            VisionXHeader(
                title = AppTranslations.get("app_title", selectedLanguage),
                subtitle = AppTranslations.get("app_subtitle", selectedLanguage),
                isOnline = isOnline,
                showBack = false,
                language = selectedLanguage,
                onLanguageClick = { showLanguageDialog = true },
                onSpeakStatusClick = onSpeakStatus,
                onProfileClick = { onNavigate(AppDestination.SETTINGS) }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val isWide = maxWidth >= 720.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (isWide) 24.dp else 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Location & Regional Market Price Banner
                item {
                    Surface(
                        onClick = onOpenLocationSheet,
                        shape = RoundedCornerShape(14.dp),
                        color = VxSurfaceBlue,
                        border = BorderStroke(1.dp, VxPrimaryBlue.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_location_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = VxPrimaryBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "📍 ${currentLocation.cityName}, ${currentLocation.stateName}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VxTextPrimary
                                    )
                                    Text(
                                        text = "मंडी: ${currentLocation.nearestMandi}",
                                        fontSize = 11.sp,
                                        color = VxTextSecondary
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = VxPrimaryBlue,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "बदलें (Change)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // 1. Primary Clean Status & Control Hub (All-in-one minimal hero card)
                item {
                    FarmerDashboardCard(
                        c1Temp = chamber1.currentTemp,
                        c2Temp = chamber2.currentTemp,
                        batteryPercent = powerData.batteryPercent,
                        solarWatts = powerData.solarInputWatts,
                        cratesCount = crates.size,
                        coolingActive = coolingActive,
                        fansActive = fansActive,
                        isAllSafe = isAllSafe,
                        selectedLanguage = selectedLanguage,
                        isFarmerMode = isFarmerMode,
                        onToggleFarmerMode = onToggleFarmerMode,
                        onToggleCooling = onToggleCooling,
                        onToggleFans = onToggleFans,
                        onSpeakStatus = onSpeakStatus,
                        onSpeakCard = onSpeakText,
                        onOpenVoiceAssistant = onOpenVoiceAssistant
                    )
                }

                // 2. Chamber Stored Produce Overview (Chamber 1 & Chamber 2 Crops Breakdown)
                item {
                    val c1Crates = crates.filter { it.chamber.contains("1", ignoreCase = true) }
                    val c2Crates = crates.filter { it.chamber.contains("2", ignoreCase = true) }
                    com.example.ui.components.ChamberProduceStockCard(
                        chamber1Crates = c1Crates,
                        chamber2Crates = c2Crates,
                        selectedLanguage = selectedLanguage,
                        onSpeakStock = onSpeakText
                    )
                }

                // 3. Active Crates Section (Clean preview of 2 items)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppTranslations.get("nav_crates", selectedLanguage),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = VxTextPrimary
                        )
                        TextButton(
                            onClick = { onNavigate(AppDestination.CRATES) },
                            modifier = Modifier.testTag("view_all_crates_button")
                        ) {
                            Text(
                                text = "सभी देखें (View All)",
                                color = VxPrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                val recentCrates = crates.take(2)
                items(recentCrates, key = { it.id }) { crate ->
                    CrateCard(
                        crate = crate,
                        onClick = { onCrateClick(crate.id) }
                    )
                }

                // 3. Recent Alerts Section (Clean preview of 1-2 items)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppTranslations.get("nav_alerts", selectedLanguage),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = VxTextPrimary
                        )
                        TextButton(
                            onClick = { onNavigate(AppDestination.ALERTS) },
                            modifier = Modifier.testTag("view_all_alerts_button")
                        ) {
                            Text(
                                text = "सभी देखें (View All)",
                                color = VxPrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                val topAlerts = alerts.take(2)
                items(topAlerts, key = { it.id }) { alert ->
                    AlertCard(
                        alert = alert,
                        onMarkAsRead = { onAlertClick(alert.id) },
                        onDismiss = { onDismissAlert(alert.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

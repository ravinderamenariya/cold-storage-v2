package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChamberData
import com.example.data.model.ConnectionMode
import com.example.data.model.PowerData
import com.example.data.model.SensorNodeStatus
import com.example.ui.components.ChamberSensorCard
import com.example.ui.components.GasLevelSensorCard
import com.example.ui.components.OutsideSensorCard
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LiveMonitoringScreen(
    chamber1: ChamberData,
    chamber2: ChamberData,
    outsideTemp: Double,
    outsideHumidity: Double,
    gasStatus: String,
    airQuality: String,
    powerData: PowerData,
    coolingActive: Boolean,
    fansActive: Boolean,
    deviceConnected: Boolean,
    lastUpdatedTime: Long,
    sensorNodes: List<SensorNodeStatus>,
    connectionMode: ConnectionMode,
    isOnline: Boolean,
    onToggleCooling: (Boolean) -> Unit,
    onToggleFans: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedChamberTab by remember { mutableStateOf("All") }
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val lastUpdateStr = timeFormat.format(Date(lastUpdatedTime))

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Scaffold(
        topBar = {
            VisionXHeader(
                title = "Live Monitoring",
                subtitle = "Chambers & IoT Telemetry",
                isOnline = isOnline,
                showBack = true,
                onBackClick = onBack
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Status Header Pill with Last Updated
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (deviceConnected) VxTheme.emerald.copy(alpha = dotAlpha) else VxTheme.warningAmber
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (deviceConnected) "Raspberry Pi 5 Connected" else "Device Data Delayed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (deviceConnected) VxTheme.emeraldDark else VxTheme.warningAmber
                            )
                        }

                        Text(
                            text = "Updated: $lastUpdateStr",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Chamber Selection Tabs: All, Chamber 1, Chamber 2, Outside
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VxTheme.surfaceSubtle)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("All", "Chamber 1", "Chamber 2", "Outside").forEach { tab ->
                        val isSelected = tab == selectedChamberTab
                        Surface(
                            onClick = { selectedChamberTab = tab },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) VxTheme.primaryBlue else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_live_$tab")
                        ) {
                            Text(
                                text = tab,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Chamber Cards
            if (selectedChamberTab in listOf("All", "Chamber 1", "Chamber 2")) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (selectedChamberTab in listOf("All", "Chamber 1")) {
                            ChamberSensorCard(
                                data = chamber1,
                                isChamber1 = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (selectedChamberTab in listOf("All", "Chamber 2")) {
                            ChamberSensorCard(
                                data = chamber2,
                                isChamber1 = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Outside & Gas Cards
            if (selectedChamberTab in listOf("All", "Outside")) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutsideSensorCard(
                            temperature = outsideTemp,
                            humidity = outsideHumidity,
                            modifier = Modifier.weight(1f)
                        )
                        GasLevelSensorCard(
                            gasLevel = gasStatus,
                            airQuality = airQuality,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // System Actuators & Controls (Cooling System ON, Fans ON, Battery 78%, Solar 620W)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "System Status & Actuators",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Cooling System Switch Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Cooling System",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (coolingActive) "Compressors active" else "System idle",
                                    fontSize = 11.sp,
                                    color = if (coolingActive) VxTheme.emeraldDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = coolingActive,
                                onCheckedChange = { onToggleCooling(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VxTheme.emerald,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.testTag("toggle_cooling_system")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Fans Switch Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Circulation Fans",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (fansActive) "Chamber ventilation running" else "Fans stopped",
                                    fontSize = 11.sp,
                                    color = if (fansActive) VxTheme.emeraldDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = fansActive,
                                onCheckedChange = { onToggleFans(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VxTheme.emerald,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.testTag("toggle_fans_system")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Battery & Solar Quick Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(VxTheme.surfaceSubtle)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Battery",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${powerData.batteryPercent}%",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Column {
                                Text(
                                    text = "Solar Input",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${powerData.solarInputWatts.toInt()} W",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxTheme.emeraldDark
                                )
                            }

                            Column {
                                Text(
                                    text = "Controller",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "MPPT Active",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxTheme.primaryBlue
                                )
                            }
                        }
                    }
                }
            }

            // Sensor Status Grid: DHT11 Chamber 1, DHT11 Chamber 2, DHT11 Outside, MQ-2 Gas 1, MQ-2 Gas 2, TFT Display
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sensor Hardware Status",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = VxTheme.primaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "6/6 OK",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxTheme.emeraldDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Grid of Sensor items
                        sensorNodes.chunked(2).forEach { rowPair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowPair.forEach { sensor ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(VxTheme.surfaceSubtle)
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = sensor.name,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = sensor.location,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(VxTheme.surfaceGreen)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "OK",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = VxTheme.emeraldDark
                                                )
                                            }
                                        }
                                    }
                                }
                                if (rowPair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

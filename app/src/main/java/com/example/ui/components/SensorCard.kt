package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChamberData
import com.example.data.model.PowerData
import com.example.ui.theme.VxTheme
import kotlin.math.abs

/**
 * Top Overall Status Card: "Cold Storage" / "All Systems Normal"
 */
@Composable
fun SystemStatusHeroCard(
    statusText: String = "All Systems Normal",
    chamberCount: Int = 2,
    activeCrates: Int = 4,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("system_status_hero_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(VxTheme.emerald)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cold Storage Status",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = statusText,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$chamberCount Chambers Monitored",
                        fontSize = 12.sp,
                        color = VxTheme.primaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = " • ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$activeCrates Stored Crates",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tech Cold Storage Chamber Graphic
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VxTheme.surfaceBlue)
                    .border(1.dp, VxTheme.borderBlue, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = null,
                        tint = VxTheme.primaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "ACTIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = VxTheme.primaryBlue
                    )
                }
            }
        }
    }
}

/**
 * Enhanced Material 3 Chamber Sensor Card with target comparison,
 * humidity level, actuator status, and delta temperature.
 */
@Composable
fun ChamberSensorCard(
    data: ChamberData,
    isChamber1: Boolean,
    outsideTemp: Double = 28.0,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isNormal = data.status.equals("Normal", ignoreCase = true)
    val accentColor = if (isChamber1) VxTheme.primaryBlue else VxTheme.emerald
    val bgColor = if (isChamber1) VxTheme.surfaceBlue.copy(alpha = 0.5f) else VxTheme.surfaceGreen.copy(alpha = 0.5f)
    val borderColor = if (isChamber1) VxTheme.borderBlue else VxTheme.borderGreen
    val deltaFromOutside = String.format("%.1f", outsideTemp - data.currentTemp)

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("chamber_card_${if (isChamber1) "1" else "2"}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Chamber name, icon, and status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = data.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Checkmark / Warning pill
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isNormal) VxTheme.surfaceGreen else VxTheme.surfaceWarning),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isNormal) VxTheme.emeraldDark else VxTheme.warningDark,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Circular Arc Temperature Gauge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                TemperatureGauge(
                    temperature = data.currentTemp,
                    targetTemp = data.targetTemp,
                    minTemp = -5.0,
                    maxTemp = 35.0,
                    size = 105.dp,
                    strokeWidth = 8.5.dp,
                    showTarget = true,
                    compact = true
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Humidity Level & Mini Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = VxTheme.primaryBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Humidity",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${data.humidity.toInt()}% RH",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VxTheme.primaryBlue
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (data.humidity / 100f).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = accentColor,
                trackColor = VxTheme.surfaceSubtle
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Actuator Badges Row (Cooling, Fans, Delta)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (data.coolingActive) VxTheme.emerald else VxTheme.textMuted)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (data.coolingActive) "Cooling ON" else "Idle",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (data.coolingActive) VxTheme.emeraldDark else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "ΔT -$deltaFromOutside°C",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Outside Environment Sensor Card (28°C, Humidity 62%).
 */
@Composable
fun OutsideSensorCard(
    temperature: Double,
    humidity: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("outside_sensor_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VxTheme.surfaceSubtle),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = VxTheme.warningAmber,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ambient Outside",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Circular Arc Temperature Gauge for Outside Ambient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                TemperatureGauge(
                    temperature = temperature,
                    minTemp = 0.0,
                    maxTemp = 50.0,
                    size = 100.dp,
                    strokeWidth = 8.5.dp,
                    showTarget = false,
                    compact = true,
                    accentColor = VxTheme.warningAmber
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Humidity: ${humidity.toInt()}%",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Heat Load: Moderate",
                    fontSize = 10.sp,
                    color = VxTheme.warningDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Gas Level & Air Quality Card (Normal, Air Quality Good).
 */
@Composable
fun GasLevelSensorCard(
    gasLevel: String = "Normal",
    airQuality: String = "Good",
    modifier: Modifier = Modifier
) {
    val isNormal = gasLevel.equals("Normal", ignoreCase = true)

    Card(
        modifier = modifier
            .testTag("gas_sensor_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isNormal) VxTheme.borderPurple else VxTheme.borderWarning),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VxTheme.surfacePurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = VxTheme.purple,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gas & Air Quality",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = gasLevel,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = if (isNormal) MaterialTheme.colorScheme.onSurface else VxTheme.warningDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MQ-2: Safe (18 ppm)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Status: $airQuality",
                    fontSize = 10.sp,
                    color = if (isNormal) VxTheme.emeraldDark else VxTheme.warningDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Dedicated Material 3 Real-Time Solar Power & Energy Status Card.
 * Displays Solar Generation (W), Battery SOC (%), Charging State,
 * MPPT telemetry (Voltage, Current, Temp), and autonomous grid status.
 */
@Composable
fun SolarPowerLiveCard(
    powerData: PowerData,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "solarPulse")
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunPulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("solar_power_live_card"),
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
            // Header Row: Title & Action pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(VxTheme.surfaceWarning),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = VxTheme.warningDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Solar Power & Battery",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "MPPT Charge Controller • Off-Grid Ready",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Power Flow →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VxTheme.primaryBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Metrics Row: Solar Input Watts and Battery Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Solar Generation Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VxTheme.surfaceWarning.copy(alpha = 0.5f))
                        .border(1.dp, VxTheme.borderWarning, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Solar Input",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VxTheme.warningDark
                            )
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = VxTheme.warningAmber,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${powerData.solarInputWatts.toInt()}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = " W",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VxTheme.warningDark,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Peak: 750 W • 82% Eff",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Battery SOC Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VxTheme.surfaceGreen.copy(alpha = 0.5f))
                        .border(1.dp, VxTheme.borderGreen, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Battery SOC",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VxTheme.emeraldDark
                            )
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = null,
                                tint = VxTheme.emeraldDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${powerData.batteryPercent}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VxTheme.emeraldDark,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "~14.5 hrs on backup",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = VxTheme.emeraldDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Battery Progress Track
            LinearProgressIndicator(
                progress = { (powerData.batteryPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = VxTheme.emerald,
                trackColor = VxTheme.surfaceSubtle
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Electrical Telemetry Specs Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(VxTheme.surfaceSubtle)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voltage
                Column {
                    Text(text = "Voltage", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${powerData.batteryVoltage} V", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                // Current
                Column {
                    Text(text = "Current", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${powerData.batteryCurrent} A", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                // Cell Temp
                Column {
                    Text(text = "Cell Temp", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${powerData.batteryTemperature.toInt()}°C", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                // Net Surplus
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Net Surplus", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "+140 W", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VxTheme.emeraldDark)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Sub-status: Charging State & Grid Connectivity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (powerData.isCharging) VxTheme.emerald else VxTheme.textMuted)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (powerData.isCharging) "Charging Active (MPPT Bulk)" else "Standby Mode",
                        fontSize = 11.sp,
                        color = if (powerData.isCharging) VxTheme.emeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (powerData.gridConnected) VxTheme.emerald else VxTheme.warningAmber)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (powerData.gridConnected) "Grid Online" else "Grid: Off-grid Autonomous",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Material 3 Quick Actuator Controls for the dashboard.
 * Allows instant toggling of Cooling Compressors and Circulation Fans.
 */
@Composable
fun ChamberQuickControlsCard(
    coolingActive: Boolean,
    fansActive: Boolean,
    onToggleCooling: (Boolean) -> Unit,
    onToggleFans: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chamber_quick_controls_card"),
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
                text = "Real-Time Chamber Actuators",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cooling Compressor Control
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (coolingActive) VxTheme.surfaceBlue else VxTheme.surfaceSubtle)
                        .border(1.dp, if (coolingActive) VxTheme.borderBlue else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = null,
                                tint = if (coolingActive) VxTheme.primaryBlue else VxTheme.textMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Switch(
                                checked = coolingActive,
                                onCheckedChange = onToggleCooling,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VxTheme.primaryBlue
                                ),
                                modifier = Modifier.testTag("dashboard_toggle_cooling")
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Cooling System",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (coolingActive) "Compressors ON" else "Standby",
                            fontSize = 11.sp,
                            color = if (coolingActive) VxTheme.primaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Circulation Fan Control
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (fansActive) VxTheme.surfaceGreen else VxTheme.surfaceSubtle)
                        .border(1.dp, if (fansActive) VxTheme.borderGreen else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = null,
                                tint = if (fansActive) VxTheme.emeraldDark else VxTheme.textMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Switch(
                                checked = fansActive,
                                onCheckedChange = onToggleFans,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VxTheme.emerald
                                ),
                                modifier = Modifier.testTag("dashboard_toggle_fans")
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Circulation Fans",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (fansActive) "Ventilation Active" else "Stopped",
                            fontSize = 11.sp,
                            color = if (fansActive) VxTheme.emeraldDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

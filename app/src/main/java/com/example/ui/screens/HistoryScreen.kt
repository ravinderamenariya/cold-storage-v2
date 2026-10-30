package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.data.model.SensorReading
import com.example.ui.components.RechartsTelemetryModule
import com.example.ui.components.TemperatureOverviewChart
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxBorder
import com.example.ui.theme.VxEmerald
import com.example.ui.theme.VxEmeraldDark
import com.example.ui.theme.VxNavy
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxSurfaceGreen
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxSurfaceWarning
import com.example.ui.theme.VxTheme
import com.example.ui.theme.VxWarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    recentReadings: List<SensorReading>,
    isOnline: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMetricTab by remember { mutableStateOf("Temperature") } // Temperature, Humidity, Gas
    var selectedChamberFilter by remember { mutableStateOf("All") } // All, Chamber 1, Chamber 2, Outside
    var selectedInterval by remember { mutableStateOf("1D") } // 1D, 7D, 30D

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Compute stats
    val c1Temps = recentReadings.map { it.chamber1Temp }.ifEmpty { listOf(12.0) }
    val minC1 = c1Temps.minOrNull() ?: 11.8
    val maxC1 = c1Temps.maxOrNull() ?: 12.4
    val avgC1 = (c1Temps.average() * 10).toInt() / 10.0
    val latestC1 = c1Temps.firstOrNull() ?: 12.0

    Scaffold(
        topBar = {
            VisionXHeader(
                title = "History",
                subtitle = "View Past Sensor Data & Trends",
                isOnline = isOnline,
                showBack = false
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
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Metric Tabs (Temperature, Humidity, Gas)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VxTheme.surfaceSubtle)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Temperature", "Humidity", "Gas").forEach { tab ->
                        val isSelected = tab == selectedMetricTab
                        Surface(
                            onClick = { selectedMetricTab = tab },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) VxTheme.primaryBlue else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_metric_$tab")
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

            // Chamber Selection Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Chamber 1", "Chamber 2", "Outside").forEach { chamber ->
                        val isSelected = chamber == selectedChamberFilter
                        Surface(
                            onClick = { selectedChamberFilter = chamber },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.testTag("filter_chamber_$chamber")
                        ) {
                            Text(
                                text = chamber,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Recharts 24-Hour Telemetry & Solar Efficiency Chart Module
            item {
                RechartsTelemetryModule()
            }

            // Extended Multi-Day Overview Chart
            item {
                TemperatureOverviewChart(
                    title = "Multi-Day $selectedMetricTab Historical Trends",
                    selectedInterval = selectedInterval,
                    onIntervalSelected = { selectedInterval = it },
                    showChamber1 = selectedChamberFilter in listOf("All", "Chamber 1"),
                    showChamber2 = selectedChamberFilter in listOf("All", "Chamber 2"),
                    showOutside = selectedChamberFilter in listOf("All", "Outside")
                )
            }

            // Statistical Cards (Min, Max, Avg, Latest)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Chamber 1 Summary Statistics",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox(label = "Min", value = "${String.format("%.1f", minC1)}°C", color = VxTheme.primaryBlue)
                            StatBox(label = "Max", value = "${String.format("%.1f", maxC1)}°C", color = VxTheme.warningDark)
                            StatBox(label = "Average", value = "${String.format("%.1f", avgC1)}°C", color = VxTheme.emeraldDark)
                            StatBox(label = "Latest", value = "${String.format("%.1f", latestC1)}°C", color = VxTheme.primaryBlue)
                        }
                    }
                }
            }

            // Timestamped Readings Table
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Recent Timestamped Readings",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(VxTheme.surfaceSubtle)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Time", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Text(text = "C1 (°C)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Text(text = "C2 (°C)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Text(text = "Outside (°C)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val displayReadings = recentReadings.take(8).ifEmpty {
                            listOf(
                                SensorReading(timestamp = System.currentTimeMillis() - 0, chamber1Temp = 12.1, chamber1Humidity = 62.0, chamber2Temp = 4.2, chamber2Humidity = 60.0, outsideTemp = 28.3, outsideHumidity = 62.0),
                                SensorReading(timestamp = System.currentTimeMillis() - 3600000, chamber1Temp = 12.0, chamber1Humidity = 62.0, chamber2Temp = 4.1, chamber2Humidity = 60.0, outsideTemp = 28.1, outsideHumidity = 62.0),
                                SensorReading(timestamp = System.currentTimeMillis() - 7200000, chamber1Temp = 12.2, chamber1Humidity = 62.0, chamber2Temp = 4.0, chamber2Humidity = 60.0, outsideTemp = 27.8, outsideHumidity = 62.0),
                                SensorReading(timestamp = System.currentTimeMillis() - 10800000, chamber1Temp = 12.3, chamber1Humidity = 62.0, chamber2Temp = 4.0, chamber2Humidity = 60.0, outsideTemp = 27.6, outsideHumidity = 62.0)
                            )
                        }

                        displayReadings.forEachIndexed { idx, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = timeFormat.format(Date(item.timestamp)),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${item.chamber1Temp}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxTheme.primaryBlue,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${item.chamber2Temp}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxTheme.emeraldDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${item.outsideTemp}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (idx < displayReadings.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertItem
import com.example.data.model.ChamberData
import com.example.data.model.Crate
import com.example.ui.components.AlertCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.TemperatureOverviewChart
import com.example.ui.components.VisionXHeader
import com.example.ui.components.getProduceEmoji
import com.example.ui.theme.VxBorder
import com.example.ui.theme.VxBorderBlue
import com.example.ui.theme.VxDangerDark
import com.example.ui.theme.VxDangerRed
import com.example.ui.theme.VxEmerald
import com.example.ui.theme.VxEmeraldDark
import com.example.ui.theme.VxNavy
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxSurfaceDanger
import com.example.ui.theme.VxSurfaceGreen
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxSurfaceWarning
import com.example.ui.theme.VxTheme
import com.example.ui.theme.VxWarningAmber
import com.example.ui.theme.VxWarningDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CrateDetailScreen(
    crate: Crate?,
    currentChamberData: ChamberData,
    relatedAlerts: List<AlertItem>,
    isOnline: Boolean,
    onArchiveCrate: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (crate == null) {
        Scaffold(
            topBar = {
                VisionXHeader(
                    title = "Crate Details",
                    showBack = true,
                    onBackClick = onBack
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Crate not found", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }

    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val storedDateStr = dateFormat.format(Date(crate.storageTimestamp))

    val diffHours = ((System.currentTimeMillis() - crate.storageTimestamp) / (1000 * 60 * 60)).coerceAtLeast(0)
    val durationStr = if (diffHours < 24) "$diffHours Hours" else "${diffHours / 24} Days ${diffHours % 24} Hours"

    val progressColor = when {
        crate.freshnessPercent >= 70 -> VxTheme.emeraldDark
        crate.freshnessPercent >= 40 -> VxTheme.warningDark
        else -> VxTheme.errorRed
    }

    Scaffold(
        topBar = {
            VisionXHeader(
                title = "${crate.id} – ${crate.product}",
                subtitle = "Batch Tracking & Health",
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
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Hero Produce Card with Freshness Radial Gauge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = getProduceEmoji(crate.product),
                                    fontSize = 28.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = crate.product,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = crate.id,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VxTheme.primaryBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Chamber: ${crate.chamber}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Stored: $storedDateStr ($durationStr elapsed)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            StatusBadge(status = crate.status)
                        }

                        // Freshness Gauge Circle
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(90.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { (crate.freshnessPercent / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.size(86.dp),
                                strokeWidth = 8.dp,
                                color = progressColor,
                                trackColor = VxTheme.surfaceSubtle
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${crate.freshnessPercent}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Fresh",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Current Conditions Grid (Temperature, Humidity, Gas)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Current Chamber Conditions",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Temperature Metric Box
                            ConditionMetricBox(
                                icon = Icons.Default.Thermostat,
                                label = "Temperature",
                                value = "${currentChamberData.currentTemp}°C",
                                target = "Target: ${crate.targetTemp.toInt()}°C",
                                color = VxTheme.primaryBlue,
                                modifier = Modifier.weight(1f)
                            )

                            // Humidity Metric Box
                            ConditionMetricBox(
                                icon = Icons.Default.WaterDrop,
                                label = "Humidity",
                                value = "${currentChamberData.humidity.toInt()}%",
                                target = "Target: ${crate.targetHumidity.toInt()}%",
                                color = VxTheme.emeraldDark,
                                modifier = Modifier.weight(1f)
                            )

                            // Gas Metric Box
                            ConditionMetricBox(
                                icon = Icons.Default.Co2,
                                label = "Gas / Air",
                                value = "Normal",
                                target = "MQ-2 OK",
                                color = VxTheme.purple,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Storage Details Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Batch Metadata",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow("Quantity", "${crate.quantity} ${crate.unit}")
                        DetailRow("Expected Shelf Life", "${crate.expectedShelfLifeDays} Days")
                        DetailRow("Storage Chamber", crate.chamber)
                        if (crate.notes.isNotBlank()) {
                            DetailRow("Notes", crate.notes)
                        }
                    }
                }
            }

            // Historical Temperature Chart for this Crate
            item {
                TemperatureOverviewChart(
                    title = "Temperature History (${crate.chamber})",
                    selectedInterval = "24H",
                    showChamber1 = crate.chamber == "Chamber 1",
                    showChamber2 = crate.chamber == "Chamber 2",
                    showOutside = false
                )
            }

            // Associated Alerts section
            if (relatedAlerts.isNotEmpty()) {
                item {
                    Text(
                        text = "Associated Alerts",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(relatedAlerts.size) { index ->
                    AlertCard(
                        alert = relatedAlerts[index],
                        onMarkAsRead = {},
                        onDismiss = {}
                    )
                }
            }

            // Actions: Archive Crate
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onArchiveCrate(crate.id) },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("archive_crate_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Archive / Complete Crate Batch",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ConditionMetricBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    target: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(VxTheme.surfaceSubtle)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = target,
                fontSize = 9.sp,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

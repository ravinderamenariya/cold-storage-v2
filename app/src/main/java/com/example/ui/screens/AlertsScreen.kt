package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertItem
import com.example.data.model.AlertSeverity
import com.example.ui.components.AlertCard
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxBorder
import com.example.ui.theme.VxDangerRed
import com.example.ui.theme.VxNavy
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxTheme
import com.example.ui.theme.VxWarningAmber

@Composable
fun AlertsScreen(
    alerts: List<AlertItem>,
    isOnline: Boolean,
    onMarkAsRead: (String) -> Unit,
    onDismissAlert: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") } // "All", "Critical", "Warning", "Mandi Price", "Info"

    val filteredAlerts = remember(alerts, selectedCategory) {
        when (selectedCategory) {
            "Critical" -> alerts.filter { it.severity == AlertSeverity.CRITICAL }
            "Warning" -> alerts.filter { it.severity == AlertSeverity.WARNING }
            "Mandi Price" -> alerts.filter { it.title.contains("मंडी") || it.id.contains("PRICE", ignoreCase = true) }
            "Info" -> alerts.filter { it.severity == AlertSeverity.INFO }
            else -> alerts
        }
    }

    Scaffold(
        topBar = {
            VisionXHeader(
                title = "Alerts",
                subtitle = "Security & Telemetry Hazards",
                isOnline = isOnline,
                showBack = false
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Category filter chips and "Mark all as read"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Mandi Price", "Critical", "Warning").forEach { cat ->
                        val isSelected = cat == selectedCategory
                        val chipColor = when (cat) {
                            "Critical" -> if (isSelected) VxTheme.errorRed else MaterialTheme.colorScheme.surface
                            "Warning" -> if (isSelected) VxTheme.warningDark else MaterialTheme.colorScheme.surface
                            "Mandi Price" -> if (isSelected) VxTheme.emeraldDark else MaterialTheme.colorScheme.surface
                            else -> if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.surface
                        }

                        Surface(
                            onClick = { selectedCategory = cat },
                            shape = RoundedCornerShape(16.dp),
                            color = chipColor,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) chipColor else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.testTag("filter_alert_$cat")
                        ) {
                            val label = if (cat == "Mandi Price") "📈 भाव अलर्ट" else cat
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Mark All Read button
                OutlinedButton(
                    onClick = onMarkAllAsRead,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VxTheme.primaryBlue),
                    modifier = Modifier.testTag("mark_all_alerts_read_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = "Read All", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredAlerts.isEmpty()) {
                EmptyStateCard(
                    icon = Icons.Default.NotificationsActive,
                    title = "No Alerts in $selectedCategory",
                    description = "Everything is running within normal parameters. Storage chambers and power are stable."
                )
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isWide = maxWidth >= 720.dp
                    if (isWide) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val chunks = filteredAlerts.chunked(2)
                            items(chunks) { rowAlerts ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    rowAlerts.forEach { alert ->
                                        AlertCard(
                                            alert = alert,
                                            onMarkAsRead = { onMarkAsRead(alert.id) },
                                            onDismiss = { onDismissAlert(alert.id) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowAlerts.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredAlerts, key = { it.id }) { alert ->
                                AlertCard(
                                    alert = alert,
                                    onMarkAsRead = { onMarkAsRead(alert.id) },
                                    onDismiss = { onDismissAlert(alert.id) }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

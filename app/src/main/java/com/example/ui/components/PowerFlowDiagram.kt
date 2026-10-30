package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.PowerData
import com.example.ui.theme.VxTheme

/**
 * Animated Power Flow Diagram:
 * Solar Panel -> Charge Controller -> Battery -> Inverter / Load
 */
@Composable
fun PowerFlowDiagram(
    powerData: PowerData,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "powerFlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("power_flow_diagram_card"),
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
                text = "Power Flow",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Flow Row: Solar -> Controller -> Battery -> Load
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Node 1: Solar Panel
                PowerNode(
                    icon = Icons.Default.SolarPower,
                    label = "Solar Panel",
                    subValue = "${powerData.solarInputWatts.toInt()} W",
                    color = VxTheme.warningAmber,
                    bgColor = VxTheme.surfaceWarning
                )

                // Connector 1
                FlowArrow(isActive = powerData.solarInputWatts > 50, pulse = pulse)

                // Node 2: Charge Controller
                PowerNode(
                    icon = Icons.Default.SettingsInputComponent,
                    label = "Controller",
                    subValue = "MPPT Active",
                    color = VxTheme.primaryBlue,
                    bgColor = VxTheme.surfaceBlue
                )

                // Connector 2
                FlowArrow(isActive = powerData.isCharging, pulse = pulse)

                // Node 3: Battery
                PowerNode(
                    icon = Icons.Default.BatteryChargingFull,
                    label = "Battery",
                    subValue = "${powerData.batteryPercent}%",
                    color = VxTheme.emerald,
                    bgColor = VxTheme.surfaceGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inverter / Load branch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "↓",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VxTheme.primaryBlue.copy(alpha = pulse)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Inverter / Load Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VxTheme.surfaceSubtle)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Power,
                                contentDescription = null,
                                tint = VxTheme.cyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cold Storage Load",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Compressors & Evaporators",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "480 W Active",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = VxTheme.emeraldDark
                        )
                        Text(
                            text = "Cooling System ON",
                            fontSize = 10.sp,
                            color = VxTheme.emeraldDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PowerNode(
    icon: ImageVector,
    label: String,
    subValue: String,
    color: Color,
    bgColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(82.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(1.5.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subValue,
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FlowArrow(
    isActive: Boolean,
    pulse: Float
) {
    Text(
        text = "→",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = if (isActive) VxTheme.primaryBlue.copy(alpha = pulse) else MaterialTheme.colorScheme.onSurfaceVariant
    )
}

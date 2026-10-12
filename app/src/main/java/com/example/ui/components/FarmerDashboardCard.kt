package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.VxBorder
import com.example.ui.theme.VxBorderGreen
import com.example.ui.theme.VxBorderWarning
import com.example.ui.theme.VxError
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxSuccess
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxSurfaceCard
import com.example.ui.theme.VxSurfaceGreen
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxSurfaceWarning
import com.example.ui.theme.VxTextPrimary
import com.example.ui.theme.VxTextSecondary
import com.example.ui.theme.VxWarningAmber
import com.example.util.AppTranslations
import com.example.util.IndianLanguage

/**
 * Clean, minimal, and non-complex cold storage dashboard card.
 * Simple visual status, 4 clear metrics, comfortable button sizes (50dp+),
 * and spoken voice audio narration.
 */
@Composable
fun FarmerDashboardCard(
    c1Temp: Double,
    c2Temp: Double,
    batteryPercent: Int,
    solarWatts: Double,
    cratesCount: Int,
    coolingActive: Boolean,
    fansActive: Boolean,
    isAllSafe: Boolean,
    selectedLanguage: IndianLanguage,
    isFarmerMode: Boolean,
    onToggleFarmerMode: () -> Unit,
    onToggleCooling: (Boolean) -> Unit,
    onToggleFans: (Boolean) -> Unit,
    onSpeakStatus: () -> Unit,
    onSpeakCard: (String) -> Unit,
    onOpenVoiceAssistant: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VxSurfaceCard),
        border = BorderStroke(1.dp, VxBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("farmer_dashboard_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // 1. Overall Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isAllSafe) VxSurfaceGreen else VxSurfaceWarning,
                    border = BorderStroke(1.dp, if (isAllSafe) VxBorderGreen else VxBorderWarning)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isAllSafe) VxSuccess else VxWarningAmber)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAllSafe) AppTranslations.get("everything_safe", selectedLanguage) else AppTranslations.get("attention_required", selectedLanguage),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAllSafe) VxSuccess else VxWarningAmber
                        )
                    }
                }

                // Audio Listen Button
                IconButton(
                    onClick = onSpeakStatus,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(VxSurfaceSubtle)
                        .testTag("farmer_listen_status_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Listen Aloud",
                        tint = VxPrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Four Clean Minimal Metric Tiles (2x2 Grid)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Chamber 1 Tile
                MetricTile(
                    label = AppTranslations.get("chamber_1", selectedLanguage),
                    value = "${c1Temp}°C",
                    subtext = "सब्जी कमरा",
                    isGood = c1Temp in 8.0..16.0,
                    onClick = {
                        onSpeakCard("कमरा 1 का तापमान $c1Temp डिग्री है। सब ठीक है।")
                    },
                    modifier = Modifier.weight(1f)
                )

                // Chamber 2 Tile
                MetricTile(
                    label = AppTranslations.get("chamber_2", selectedLanguage),
                    value = "${c2Temp}°C",
                    subtext = "फल व अन्य",
                    isGood = c2Temp in -2.0..8.0,
                    onClick = {
                        onSpeakCard("कमरा 2 का तापमान $c2Temp डिग्री है। ठंडक सही है।")
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Battery & Solar Tile
                MetricTile(
                    label = AppTranslations.get("battery_storage", selectedLanguage),
                    value = "$batteryPercent%",
                    subtext = "${solarWatts.toInt()}W सोलर धूप",
                    isGood = batteryPercent > 30,
                    onClick = {
                        onSpeakCard("बैटरी $batteryPercent प्रतिशत चार्ज है और सोलर से ${solarWatts.toInt()} वाट बिजली आ रही है।")
                    },
                    modifier = Modifier.weight(1f)
                )

                // Stock Crates Tile
                MetricTile(
                    label = AppTranslations.get("crates_stored", selectedLanguage),
                    value = "$cratesCount",
                    subtext = "कुल पेटियां",
                    isGood = true,
                    onClick = {
                        onSpeakCard("कोल्ड स्टोरेज में कुल $cratesCount पेटियां रखी हैं।")
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Equipment Controls (Two Large, Comfortable Buttons - 52dp height)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cooling Control Button
                Surface(
                    onClick = { onToggleCooling(!coolingActive) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (coolingActive) VxSurfaceGreen else VxSurfaceSubtle,
                    border = BorderStroke(1.dp, if (coolingActive) VxBorderGreen else VxBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("farmer_cooling_toggle")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "❄️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ठंडक (AC)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VxTextPrimary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (coolingActive) VxSuccess else VxSurfaceSubtle
                        ) {
                            Text(
                                text = if (coolingActive) "चालू ON" else "बंद OFF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (coolingActive) Color.White else VxTextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Fan Control Button
                Surface(
                    onClick = { onToggleFans(!fansActive) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (fansActive) VxSurfaceGreen else VxSurfaceSubtle,
                    border = BorderStroke(1.dp, if (fansActive) VxBorderGreen else VxBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("farmer_fans_toggle")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌀", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "पंखा (Fan)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VxTextPrimary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (fansActive) VxSuccess else VxSurfaceSubtle
                        ) {
                            Text(
                                text = if (fansActive) "चालू ON" else "बंद OFF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (fansActive) Color.White else VxTextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Primary Voice Assistant Action Button (Large 52dp button, comfortable, clear)
            Button(
                onClick = onOpenVoiceAssistant,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VxPrimaryBlue,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("farmer_voice_assistant_banner")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Assistant",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "बोलकर पूछें (Ask Voice Assistant)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    subtext: String,
    isGood: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = VxSurfaceSubtle,
        border = BorderStroke(1.dp, VxBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VxTextSecondary,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isGood) VxSuccess else VxWarningAmber)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = VxTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = VxTextSecondary,
                maxLines = 1
            )
        }
    }
}

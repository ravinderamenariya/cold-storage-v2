package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VxTheme
import com.example.util.UserLocationInfo

/**
 * Google Maps & OpenStreetMap Cold Storage Location Preview Card.
 * Displays "Where is my Cold Storage" with live GPS coordinates, interactive radar
 * map canvas, and one-tap deep-links to Google Maps and OpenStreetMap.
 */
@Composable
fun ColdStorageLocationMapCard(
    currentLocation: UserLocationInfo,
    storageName: String = "VisionX Solar Cold Storage Hub - Dabok / Savina, Udaipur, Rajasthan",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun openInGoogleMaps() {
        try {
            val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${currentLocation.latitude},${currentLocation.longitude}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallbackUri = Uri.parse("geo:${currentLocation.latitude},${currentLocation.longitude}?q=${currentLocation.latitude},${currentLocation.longitude}(${Uri.encode(storageName)})")
            val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUri)
            try {
                context.startActivity(fallbackIntent)
            } catch (err: Exception) {
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${currentLocation.latitude},${currentLocation.longitude}")
                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
            }
        }
    }

    fun openInOpenStreetMap() {
        try {
            val osmUri = Uri.parse("https://www.openstreetmap.org/?mlat=${currentLocation.latitude}&mlon=${currentLocation.longitude}#map=16/${currentLocation.latitude}/${currentLocation.longitude}")
            val intent = Intent(Intent.ACTION_VIEW, osmUri)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open OpenStreetMap: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cold_storage_location_map_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, VxTheme.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title and Live GPS Tracked badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VxTheme.surfaceBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Google Maps",
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Where is my Cold Storage?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "उदयपुर मेवाड़ कोल्ड स्टोरेज लोकेशन (Udaipur Hub)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = VxTheme.surfaceGreen,
                    border = BorderStroke(1.dp, VxTheme.borderGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(VxTheme.emerald)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "GPS Online",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VxTheme.emeraldDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Canvas Map Preview with Cold Storage Radar Pin
            val infiniteTransition = rememberInfiniteTransition(label = "mapRadar")
            val radarScale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400),
                    repeatMode = RepeatMode.Restart
                ),
                label = "radarScale"
            )
            val radarAlpha by infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 0.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400),
                    repeatMode = RepeatMode.Restart
                ),
                label = "radarAlpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (MaterialTheme.colorScheme.surface.hashCode() % 2 == 0) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                    .border(1.dp, VxTheme.border, RoundedCornerShape(12.dp))
            ) {
                val lineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                val primaryColor = VxTheme.primaryBlue
                val secondaryColor = VxTheme.emerald

                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height

                    // Mewar regional highway grid (NH-27 & NH-58 Udaipur bypass)
                    drawLine(lineColor, Offset(0f, h * 0.35f), Offset(w, h * 0.35f), strokeWidth = 2f)
                    drawLine(lineColor, Offset(0f, h * 0.65f), Offset(w, h * 0.65f), strokeWidth = 2f)
                    drawLine(lineColor, Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), strokeWidth = 2f)
                    drawLine(lineColor, Offset(w * 0.70f, 0f), Offset(w * 0.70f, h), strokeWidth = 2f)

                    val center = Offset(w * 0.45f, h * 0.5f)
                    val savinaPoint = Offset(w * 0.22f, h * 0.75f)
                    val dabokPoint = Offset(w * 0.75f, h * 0.4f)
                    val fatehnagarPoint = Offset(w * 0.88f, h * 0.18f)

                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)

                    // Corridor to Savina Mandi
                    drawLine(
                        color = primaryColor.copy(alpha = 0.8f),
                        start = center,
                        end = savinaPoint,
                        strokeWidth = 3.dp.toPx(),
                        pathEffect = dashEffect,
                        cap = StrokeCap.Round
                    )

                    // Corridor to Dabok Collection Hub
                    drawLine(
                        color = secondaryColor.copy(alpha = 0.8f),
                        start = center,
                        end = dabokPoint,
                        strokeWidth = 3.dp.toPx(),
                        pathEffect = dashEffect,
                        cap = StrokeCap.Round
                    )

                    // Corridor to Fatehnagar Mandi
                    drawLine(
                        color = Color(0xFFF59E0B).copy(alpha = 0.8f),
                        start = center,
                        end = fatehnagarPoint,
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = dashEffect,
                        cap = StrokeCap.Round
                    )
                }

                // Center Pin: Cold Storage Hub with Radar Wave
                Box(
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing Radar Ring
                    Box(
                        modifier = Modifier
                            .size((48 * radarScale).dp)
                            .clip(CircleShape)
                            .background(VxTheme.emerald.copy(alpha = radarAlpha))
                    )

                    // Pin Icon
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(VxTheme.emerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = "Cold Storage",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Top-Left Badge: Storage Tag & Coordinates
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, VxTheme.border)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(
                            text = "VisionX Solar Hub (Dabok/Savina)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${String.format("%.4f", currentLocation.latitude)}°N, ${String.format("%.4f", currentLocation.longitude)}°E (Udaipur)",
                            fontSize = 9.sp,
                            color = VxTheme.primaryBlue
                        )
                    }
                }

                // Bottom-Left Marker: Savina Mandi Pin
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, VxTheme.primaryBlue.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍 सविना मंडी (~6.8 km)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VxTheme.primaryBlue
                        )
                    }
                }

                // Bottom-Right Marker: Nearest Mandi Distance
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, VxTheme.border)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentLocation.nearestMandi,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mewar Regional Collection Centers & Mandi Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(VxTheme.surfaceSubtle)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🌱 सविना: 6.8km", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = VxTheme.primaryBlue)
                Text(text = "•", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "📦 डबोक हब: 18km", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = VxTheme.emeraldDark)
                Text(text = "•", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "🌾 फतहनगर: 48km", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD97706))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Address & Facility Info Row
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = VxTheme.surfaceSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = VxTheme.emerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Facility: ${currentLocation.cityName}, ${currentLocation.stateName} (Mewar) • Hub: ${currentLocation.nearestMandi}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Open in Google Maps & Open in OpenStreetMap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { openInGoogleMaps() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_cold_storage_google_maps"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VxTheme.primaryBlue)
                ) {
                    Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { openInOpenStreetMap() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_cold_storage_osm"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, VxTheme.emerald)
                ) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = VxTheme.emerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OpenStreetMap", fontSize = 12.sp, color = VxTheme.emerald, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Weather & Sensor Connection Status Card.
 * Allows checking live ambient weather link, station telemetry, and microclimate efficiency.
 */
@Composable
fun WeatherConnectionCard(
    outsideTemp: Double,
    outsideHumidity: Double,
    connectionStatus: String = "ONLINE (Connected)",
    onCheckWeatherConnection: () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var lastCheckedMessage by remember { mutableStateOf<String?>(null) }
    var isChecking by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weather_connection_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, VxTheme.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Weather & Environment Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VxTheme.surfaceBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = "Weather",
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Weather & Ambient Link",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "मौसम एवं सेंसर कनेक्शन (Live Sensor)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = VxTheme.surfaceGreen,
                    border = BorderStroke(1.dp, VxTheme.borderGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = VxTheme.emeraldDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = connectionStatus,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VxTheme.emeraldDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ambient Readings Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Outside Temperature Box
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = VxTheme.surfaceSubtle,
                    border = BorderStroke(1.dp, VxTheme.border)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VxTheme.surfaceWarning),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = VxTheme.warningAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Outside Temp", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$outsideTemp°C", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // Outside Humidity Box
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = VxTheme.surfaceSubtle,
                    border = BorderStroke(1.dp, VxTheme.border)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VxTheme.surfaceBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = VxTheme.primaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Outside Humidity", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$outsideHumidity%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Microclimate Impact Note
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = VxTheme.surfaceGreen,
                border = BorderStroke(1.dp, VxTheme.borderGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = VxTheme.emeraldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (outsideTemp > 35.0) {
                            "High ambient heat ($outsideTemp°C): Solar compression running at peak capacity."
                        } else {
                            "Ambient microclimate ($outsideTemp°C): Solar refrigeration operating at optimal COP efficiency."
                        },
                        fontSize = 11.sp,
                        color = VxTheme.emeraldDark
                    )
                }
            }

            if (lastCheckedMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lastCheckedMessage ?: "",
                    fontSize = 11.sp,
                    color = VxTheme.primaryBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button: Check Weather Connection
            Button(
                onClick = {
                    val result = onCheckWeatherConnection()
                    lastCheckedMessage = result
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_check_weather_connection"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VxTheme.primaryBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "मौसम कनेक्शन जांचें (Check Weather Connection)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

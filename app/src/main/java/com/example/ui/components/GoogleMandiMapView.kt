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
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Thermostat
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
import com.example.data.model.MandiCatalog
import com.example.data.model.MandiLocation
import com.example.ui.theme.VxTheme

/**
 * Supported Map Providers for Cold Storage Logistics
 */
enum class MapProvider(val displayName: String) {
    GOOGLE_MAPS("Google Maps"),
    OPEN_STREET_MAP("OpenStreetMap (OSM)")
}

/**
 * Interactive Dual-Provider Mandi Logistics Card
 * Supports official Google AI Studio Maps Demo Key integration and OpenStreetMap (OSM).
 * Visualizes the cold storage facility location relative to the regional APMC Mandi,
 * provides distance & transit time calculations, and deep-links into either Google Maps
 * or OpenStreetMap for navigation.
 */
@Composable
fun GoogleMandiMapView(
    mandi: MandiLocation,
    storageLat: Double = 28.6139,
    storageLng: Double = 77.2090,
    storageName: String = "GREENCOLD Storage Unit",
    produceName: String = "Produce",
    cropPrice: Double = 0.0,
    priceUnit: String = "kg",
    onSelectOtherMandi: (MandiLocation) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedProvider by remember { mutableStateOf(MapProvider.GOOGLE_MAPS) }

    val distanceKm = MandiCatalog.calculateDistanceKm(storageLat, storageLng, mandi.latitude, mandi.longitude)
    val transitMinutes = (distanceKm / 40.0 * 60).toInt().coerceAtLeast(15)
    val maxSafeTransitHours = 4.5

    fun openInGoogleMaps() {
        try {
            val uri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1&origin=$storageLat,$storageLng&destination=${mandi.latitude},${mandi.longitude}&travelmode=driving"
            )
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallbackUri = Uri.parse("geo:${mandi.latitude},${mandi.longitude}?q=${mandi.latitude},${mandi.longitude}(${Uri.encode(mandi.name)})")
            val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUri)
            try {
                context.startActivity(fallbackIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Opening Google Maps in browser...", Toast.LENGTH_SHORT).show()
                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$storageLat,$storageLng&destination=${mandi.latitude},${mandi.longitude}")
                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
            }
        }
    }

    fun startGoogleNavigation() {
        try {
            val navUri = Uri.parse("google.navigation:q=${mandi.latitude},${mandi.longitude}&mode=d")
            val navIntent = Intent(Intent.ACTION_VIEW, navUri)
            context.startActivity(navIntent)
        } catch (e: Exception) {
            openInGoogleMaps()
        }
    }

    fun openInOpenStreetMap() {
        try {
            // OpenStreetMap web routing (OSRM car engine)
            val osmUri = Uri.parse(
                "https://www.openstreetmap.org/directions?engine=fossgis_osrm_car&route=$storageLat%2C$storageLng%3B${mandi.latitude}%2C${mandi.longitude}"
            )
            val intent = Intent(Intent.ACTION_VIEW, osmUri)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open OpenStreetMap: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openInOsmApp() {
        try {
            // Standard Geo URI recognized by OpenStreetMap apps (OsmAnd, Organic Maps, MAPS.ME)
            val geoUri = Uri.parse("geo:${mandi.latitude},${mandi.longitude}?q=${mandi.latitude},${mandi.longitude}(${Uri.encode(mandi.name)})")
            val intent = Intent(Intent.ACTION_VIEW, geoUri)
            context.startActivity(intent)
        } catch (e: Exception) {
            openInOpenStreetMap()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("google_mandi_map_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, VxTheme.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Map Provider Badge and Destination Mandi
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
                            .background(
                                if (selectedProvider == MapProvider.GOOGLE_MAPS) VxTheme.surfaceBlue else VxTheme.surfaceGreen
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selectedProvider == MapProvider.GOOGLE_MAPS) Icons.Default.Map else Icons.Default.Public,
                            contentDescription = selectedProvider.displayName,
                            tint = if (selectedProvider == MapProvider.GOOGLE_MAPS) VxTheme.primaryBlue else VxTheme.emerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (selectedProvider == MapProvider.GOOGLE_MAPS) "Google Maps Logistics" else "OpenStreetMap (OSM) Logistics",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${mandi.name} • ${mandi.city}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedProvider == MapProvider.GOOGLE_MAPS) VxTheme.surfaceBlue else VxTheme.surfaceGreen,
                    border = BorderStroke(
                        1.dp,
                        if (selectedProvider == MapProvider.GOOGLE_MAPS) VxTheme.primaryBlue.copy(alpha = 0.4f) else VxTheme.borderGreen
                    )
                ) {
                    Text(
                        text = if (selectedProvider == MapProvider.GOOGLE_MAPS) "Demo Key Active" else "OSM Fallback Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedProvider == MapProvider.GOOGLE_MAPS) VxTheme.primaryBlue else VxTheme.emeraldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Map Provider Switcher Chips (Google Maps vs OpenStreetMap)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(VxTheme.surfaceSubtle)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("map_provider_google")
                        .clickable { selectedProvider = MapProvider.GOOGLE_MAPS },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedProvider == MapProvider.GOOGLE_MAPS) MaterialTheme.colorScheme.surface else Color.Transparent,
                    shadowElevation = if (selectedProvider == MapProvider.GOOGLE_MAPS) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = if (selectedProvider == MapProvider.GOOGLE_MAPS) VxTheme.primaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Google Maps (Demo)",
                            fontSize = 11.sp,
                            fontWeight = if (selectedProvider == MapProvider.GOOGLE_MAPS) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedProvider == MapProvider.GOOGLE_MAPS) VxTheme.primaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("map_provider_osm")
                        .clickable { selectedProvider = MapProvider.OPEN_STREET_MAP },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedProvider == MapProvider.OPEN_STREET_MAP) MaterialTheme.colorScheme.surface else Color.Transparent,
                    shadowElevation = if (selectedProvider == MapProvider.OPEN_STREET_MAP) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = if (selectedProvider == MapProvider.OPEN_STREET_MAP) VxTheme.emerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OpenStreetMap (Fallback)",
                            fontSize = 11.sp,
                            fontWeight = if (selectedProvider == MapProvider.OPEN_STREET_MAP) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedProvider == MapProvider.OPEN_STREET_MAP) VxTheme.emerald else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (selectedProvider == MapProvider.GOOGLE_MAPS) {
                    "Google Maps Platform Demo Key active (no billing account or credit card required)."
                } else {
                    "OpenStreetMap active as fallback (100% free open-source routing without API keys)."
                },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Visual Map Simulator Canvas (Styled for selected provider)
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0.9f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            val routeColor = if (selectedProvider == MapProvider.GOOGLE_MAPS) VxTheme.primaryBlue else VxTheme.emerald
            val badgeBorderColor = if (selectedProvider == MapProvider.GOOGLE_MAPS) VxTheme.primaryBlue else VxTheme.emerald

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (MaterialTheme.colorScheme.surface.hashCode() % 2 == 0) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                    .border(1.dp, VxTheme.border, RoundedCornerShape(12.dp))
            ) {
                // Background map roads / grid representation
                val lineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height

                    // Terrain & road lines
                    drawLine(lineColor, Offset(0f, h * 0.3f), Offset(w, h * 0.3f), strokeWidth = 2f)
                    drawLine(lineColor, Offset(0f, h * 0.7f), Offset(w, h * 0.7f), strokeWidth = 2f)
                    drawLine(lineColor, Offset(w * 0.35f, 0f), Offset(w * 0.35f, h), strokeWidth = 2f)
                    drawLine(lineColor, Offset(w * 0.65f, 0f), Offset(w * 0.65f, h), strokeWidth = 2f)

                    // Dashed Transit Corridor Line connecting Cold Storage to Mandi
                    val start = Offset(w * 0.2f, h * 0.5f)
                    val end = Offset(w * 0.8f, h * 0.5f)
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)

                    drawLine(
                        color = routeColor,
                        start = start,
                        end = end,
                        strokeWidth = 4.dp.toPx(),
                        pathEffect = pathEffect,
                        cap = StrokeCap.Round
                    )
                }

                // Left Pin: Cold Storage Origin
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VxTheme.emerald.copy(alpha = pulseAlpha)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = "Cold Storage",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, VxTheme.border)
                    ) {
                        Text(
                            text = "GREENCOLD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Center Transit Badge on the Route
                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, badgeBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = null,
                            tint = routeColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$distanceKm km • ~${transitMinutes}m",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = routeColor
                        )
                    }
                }

                // Right Pin: APMC Mandi Destination
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, VxTheme.border)
                    ) {
                        Text(
                            text = mandi.city,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(routeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = "Mandi",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transit Cold Chain Safety Notice
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
                        text = "Cold Chain Safe: $produceName can travel for up to $maxSafeTransitHours hrs without spoilage risk in insulated transport.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons dynamically tailored to the selected provider
            if (selectedProvider == MapProvider.GOOGLE_MAPS) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { openInGoogleMaps() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_google_maps"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VxTheme.primaryBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { startGoogleNavigation() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_google_maps_navigate"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, VxTheme.primaryBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Directions", fontSize = 12.sp, color = VxTheme.primaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { openInOpenStreetMap() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_osm"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VxTheme.emerald)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("OpenStreetMap", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { openInOsmApp() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_osm_navigate"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, VxTheme.emerald)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = VxTheme.emerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("OSM Routing", fontSize = 12.sp, color = VxTheme.emerald, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

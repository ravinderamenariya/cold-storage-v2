package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VxBorder
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxTextPrimary
import com.example.ui.theme.VxTextSecondary
import com.example.ui.theme.VxTheme
import com.example.util.UserLocationInfo

data class StateCityOption(
    val state: String,
    val city: String,
    val mandiName: String,
    val multiplier: Float,
    val flag: String,
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090
)

val popularIndianLocations = listOf(
    StateCityOption("Delhi NCR", "New Delhi", "Azadpur Mandi, Delhi", 1.08f, "🏛️", 28.7156, 77.1788),
    StateCityOption("Maharashtra", "Nashik", "Lasalgaon & Nashik APMC", 0.95f, "🍇", 20.1472, 74.2255),
    StateCityOption("Maharashtra", "Mumbai", "Vashi APMC Mandi", 1.10f, "🏙️", 19.0760, 73.0033),
    StateCityOption("Uttar Pradesh", "Agra", "Agra APMC Mandi", 0.92f, "🌾", 27.1767, 78.0081),
    StateCityOption("Uttar Pradesh", "Lucknow", "Dubagga APMC Mandi", 0.93f, "🍃", 26.8467, 80.9462),
    StateCityOption("Punjab", "Ludhiana", "Ludhiana Grain Mandi", 0.97f, "🌾", 30.9010, 75.8573),
    StateCityOption("Karnataka", "Bengaluru", "Yeshwantpur APMC", 1.05f, "🌿", 13.0280, 77.5408),
    StateCityOption("Tamil Nadu", "Chennai", "Koyambedu Market", 1.03f, "🌴", 13.0694, 80.1948),
    StateCityOption("Rajasthan", "Jaipur", "Muhana Mandi, Jaipur", 0.94f, "🏜️", 26.7865, 75.7622),
    StateCityOption("Gujarat", "Ahmedabad", "Jamalpur APMC", 0.98f, "🏭", 23.0125, 72.5800),
    StateCityOption("Madhya Pradesh", "Indore", "Chawani Mandi, Indore", 0.91f, "🥔", 22.6841, 75.8456),
    StateCityOption("West Bengal", "Kolkata", "Posta Bazar, Kolkata", 1.02f, "🌾", 22.5726, 88.3639)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionBottomSheet(
    currentLocation: UserLocationInfo,
    onSelectLocation: (String, String, String, Float, Double, Double) -> Unit,
    onDetectGpsLocation: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("location_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = VxTheme.primaryBlue,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "📍 राज्य और मंडी चुनें (Select Location)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "क्षेत्रीय बाजार मूल्य (Regional Market Prices)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GPS Auto-detect button
            Button(
                onClick = {
                    onDetectGpsLocation()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = VxTheme.primaryBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("gps_detect_button")
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "🛰️ ऑटो-डिटेक्ट जीपीएस (Auto-Detect GPS)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "लोकप्रिय कृषि मंडियां (Popular Mandis & Cities):",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                items(popularIndianLocations) { loc ->
                    val isSelected = loc.city.equals(currentLocation.cityName, ignoreCase = true)
                    Surface(
                        onClick = {
                            onSelectLocation(loc.city, loc.state, loc.mandiName, loc.multiplier, loc.latitude, loc.longitude)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) VxTheme.surfaceBlue else Color.Transparent,
                        border = BorderStroke(1.dp, if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("location_item_${loc.city}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = loc.flag, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${loc.city}, ${loc.state}",
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "📍 ${loc.mandiName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = VxTheme.primaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

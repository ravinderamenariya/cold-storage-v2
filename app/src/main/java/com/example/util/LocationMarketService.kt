package com.example.util

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.Locale

data class UserLocationInfo(
    val cityName: String,
    val stateName: String,
    val nearestMandi: String,
    val priceMultiplier: Float,
    val latitude: Double = 24.5854,
    val longitude: Double = 73.7125
)

class LocationMarketService(private val context: Context) {

    private val _currentLocation = MutableStateFlow(
        UserLocationInfo(
            cityName = "Udaipur",
            stateName = "Rajasthan",
            nearestMandi = "Krishi Upaj Mandi Samiti, Savina, Udaipur",
            priceMultiplier = 0.96f,
            latitude = 24.5854,
            longitude = 73.7125
        )
    )
    val currentLocation: StateFlow<UserLocationInfo> = _currentLocation.asStateFlow()

    fun setLocation(
        cityName: String,
        stateName: String,
        nearestMandi: String,
        priceMultiplier: Float,
        latitude: Double = 24.5854,
        longitude: Double = 73.7125
    ) {
        _currentLocation.value = UserLocationInfo(
            cityName = cityName,
            stateName = stateName,
            nearestMandi = nearestMandi,
            priceMultiplier = priceMultiplier,
            latitude = latitude,
            longitude = longitude
        )
    }

    suspend fun detectCurrentLocation(): UserLocationInfo = withContext(Dispatchers.IO) {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val gpsLocation = try {
                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } catch (e: SecurityException) {
                null
            }

            if (gpsLocation != null) {
                val geocoder = Geocoder(context, Locale("hi", "IN"))
                val addresses = geocoder.getFromLocation(gpsLocation.latitude, gpsLocation.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: "Udaipur"
                    val state = address.adminArea ?: "Rajasthan"
                    val mandiName = if (city.contains("Udaipur", ignoreCase = true)) {
                        "Krishi Upaj Mandi Samiti, Savina, Udaipur"
                    } else {
                        "$city APMC Mandi"
                    }
                    val multiplier = getMultiplierForState(state)

                    val info = UserLocationInfo(
                        cityName = city,
                        stateName = state,
                        nearestMandi = mandiName,
                        priceMultiplier = multiplier,
                        latitude = gpsLocation.latitude,
                        longitude = gpsLocation.longitude
                    )
                    _currentLocation.value = info
                    return@withContext info
                }
            }
        } catch (e: Exception) {
            Log.w("LocationMarketService", "GPS Geocoding fallback: ${e.message}")
        }

        // Default fallback to Udaipur, Rajasthan / Mewar Cold Storage Hub
        val defaultInfo = UserLocationInfo(
            cityName = "Udaipur",
            stateName = "Rajasthan",
            nearestMandi = "Krishi Upaj Mandi Samiti, Savina, Udaipur",
            priceMultiplier = 0.96f,
            latitude = 24.5854,
            longitude = 73.7125
        )
        _currentLocation.value = defaultInfo
        return@withContext defaultInfo
    }

    private fun getMultiplierForState(state: String): Float {
        return when {
            state.contains("Rajasthan", ignoreCase = true) -> 0.96f
            state.contains("Delhi", ignoreCase = true) -> 1.08f
            state.contains("Maharashtra", ignoreCase = true) -> 0.95f
            state.contains("Uttar Pradesh", ignoreCase = true) -> 0.92f
            state.contains("Jammu", ignoreCase = true) -> 1.12f
            state.contains("Punjab", ignoreCase = true) -> 0.97f
            state.contains("Karnataka", ignoreCase = true) -> 1.05f
            state.contains("Tamil", ignoreCase = true) -> 1.03f
            else -> 1.0f
        }
    }
}

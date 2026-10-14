package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChamberTelemetryDto(
    @Json(name = "temperature") val temperature: Double = 0.0,
    @Json(name = "humidity") val humidity: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class RaspberryPiPayload(
    @Json(name = "timestamp") val timestamp: String = "",
    @Json(name = "chamber1") val chamber1: ChamberTelemetryDto = ChamberTelemetryDto(0.0, 0.0),
    @Json(name = "chamber2") val chamber2: ChamberTelemetryDto = ChamberTelemetryDto(0.0, 0.0),
    @Json(name = "outside") val outside: ChamberTelemetryDto = ChamberTelemetryDto(0.0, 0.0),
    @Json(name = "gasStatus") val gasStatus: String = "normal",
    @Json(name = "solarPower") val solarPower: Double = 0.0,
    @Json(name = "battery") val battery: Int = 0,
    @Json(name = "cooling") val cooling: Boolean = false,
    @Json(name = "fans") val fans: Boolean = false,
    @Json(name = "doorOpen") val doorOpen: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SystemHealthDto(
    @Json(name = "status") val status: String = "online",
    @Json(name = "device") val device: String = "VISION X Raspberry Pi 5"
)

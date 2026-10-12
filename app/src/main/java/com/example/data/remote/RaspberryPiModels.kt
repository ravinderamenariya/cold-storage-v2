package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChamberTelemetryDto(
    @Json(name = "temperature") val temperature: Double = 12.0,
    @Json(name = "humidity") val humidity: Double = 62.0
)

@JsonClass(generateAdapter = true)
data class RaspberryPiPayload(
    @Json(name = "timestamp") val timestamp: String = "",
    @Json(name = "chamber1") val chamber1: ChamberTelemetryDto = ChamberTelemetryDto(12.0, 62.0),
    @Json(name = "chamber2") val chamber2: ChamberTelemetryDto = ChamberTelemetryDto(4.0, 60.0),
    @Json(name = "outside") val outside: ChamberTelemetryDto = ChamberTelemetryDto(28.0, 62.0),
    @Json(name = "gas1") val gas1: String = "normal",
    @Json(name = "gas2") val gas2: String = "normal",
    @Json(name = "battery") val battery: Int = 78,
    @Json(name = "solarPower") val solarPower: Int = 620,
    @Json(name = "cooling") val cooling: Boolean = true,
    @Json(name = "fans") val fans: Boolean = true,
    @Json(name = "door1") val door1: Boolean = false,
    @Json(name = "door2") val door2: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SystemHealthDto(
    @Json(name = "device") val device: String = "Raspberry Pi 5",
    @Json(name = "uptime_hours") val uptimeHours: Double = 128.5,
    @Json(name = "cpu_temp_c") val cpuTempC: Double = 42.1,
    @Json(name = "status") val status: String = "OK"
)

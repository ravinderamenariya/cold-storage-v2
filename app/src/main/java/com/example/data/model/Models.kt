package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Status of produce freshness in a cold-storage crate.
 */
enum class CrateStatus(val label: String) {
    HEALTHY("Healthy"),
    WATCH("Watch"),
    SPOILED_RISK("Spoiled Risk");

    companion object {
        fun fromFreshness(percent: Int): CrateStatus = when {
            percent >= 70 -> HEALTHY
            percent >= 40 -> WATCH
            else -> SPOILED_RISK
        }
    }
}

/**
 * Severity level for cold storage telemetry alerts.
 */
enum class AlertSeverity {
    CRITICAL,
    WARNING,
    INFO
}

/**
 * Operating mode for the IoT data source.
 */
enum class ConnectionMode(val title: String) {
    DEMO("Demo Simulation"),
    RASPBERRY_PI("Raspberry Pi 5 (Local)"),
    CLOUD_SYNC("Cloud Live (Firebase)")
}

/**
 * Crate model used throughout UI and business logic.
 */
@Entity(tableName = "crates")
data class Crate(
    @PrimaryKey val id: String, // e.g. "CR001"
    val product: String, // e.g. "Mango"
    val chamber: String, // "Chamber 1" or "Chamber 2"
    val quantity: Double,
    val unit: String = "kg",
    val storageTimestamp: Long,
    val expectedShelfLifeDays: Int,
    val targetTemp: Double,
    val targetHumidity: Double,
    val freshnessPercent: Int,
    val status: String, // "Healthy", "Watch", "Spoiled Risk"
    val notes: String = "",
    val isArchived: Boolean = false
)

/**
 * Historical sensor reading snapshot.
 */
@Entity(tableName = "sensor_readings")
data class SensorReading(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val chamber1Temp: Double,
    val chamber1Humidity: Double,
    val chamber2Temp: Double,
    val chamber2Humidity: Double,
    val outsideTemp: Double,
    val outsideHumidity: Double,
    val gasStatus: String = "Normal", // "Normal", "Elevated", "Warning"
    val airQuality: String = "Good",
    val batteryPercent: Int = 78,
    val batteryVoltage: Double = 12.4,
    val batteryCurrent: Double = 8.2,
    val batteryTemp: Double = 32.0,
    val solarInputWatts: Double = 620.0,
    val coolingSystemOn: Boolean = true,
    val fansOn: Boolean = true,
    val door1Open: Boolean = false,
    val door2Open: Boolean = false,
    val isSynced: Boolean = true
)

/**
 * Alert item.
 */
@Entity(tableName = "alerts")
data class AlertItem(
    @PrimaryKey val id: String,
    val severity: AlertSeverity,
    val title: String,
    val message: String,
    val target: String, // e.g. "Chamber 1 - 14°C (Target 12°C)" or "CR004 - Flower"
    val timestamp: Long,
    val isRead: Boolean = false
)

/**
 * Live cold storage chamber status.
 */
data class ChamberData(
    val name: String,
    val currentTemp: Double,
    val targetTemp: Double,
    val humidity: Double,
    val targetHumidity: Double,
    val status: String = "Normal",
    val coolingActive: Boolean = true,
    val fanActive: Boolean = true,
    val doorOpen: Boolean = false
)

/**
 * Live power status summary.
 */
data class PowerData(
    val solarInputWatts: Double = 620.0,
    val batteryPercent: Int = 78,
    val batteryVoltage: Double = 12.4,
    val batteryCurrent: Double = 8.2,
    val batteryTemperature: Double = 32.0,
    val isCharging: Boolean = true,
    val gridConnected: Boolean = false,
    val powerSource: String = "Solar Primary"
)

/**
 * Hardware sensor node health status for Raspberry Pi 5.
 */
data class SensorNodeStatus(
    val name: String,
    val location: String,
    val isOk: Boolean = true
)

/**
 * AI Shelf-Life Prediction Result with Market Price & Category.
 */
data class ShelfLifePrediction(
    val product: String,
    val estimatedDays: Int,
    val confidencePercent: Int,
    val currentTemp: Double,
    val currentHumidity: Double,
    val harvestDate: String,
    val optimalTempRange: String,
    val optimalHumidityRange: String,
    val accelerationFactor: Double, // Degradation rate multiplier vs optimal (Q10 rule)
    val recommendation: String,
    val currentMarketPrice: Int = 40,
    val priceUnit: String = "kg",
    val majorState: String = "All India",
    val priceTrendPercent: Int = 10,
    val cropCategory: String = "Vegetable",
    val produceEmoji: String = "📦"
)

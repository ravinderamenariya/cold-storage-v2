package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AlertSeverity

class Converters {
    @TypeConverter
    fun fromAlertSeverity(severity: AlertSeverity): String = severity.name

    @TypeConverter
    fun toAlertSeverity(value: String): AlertSeverity = try {
        AlertSeverity.valueOf(value)
    } catch (_: Exception) {
        AlertSeverity.INFO
    }
}

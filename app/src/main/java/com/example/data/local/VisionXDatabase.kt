package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.AlertItem
import com.example.data.model.Crate
import com.example.data.model.SensorReading

@Database(
    entities = [
        Crate::class,
        SensorReading::class,
        AlertItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VisionXDatabase : RoomDatabase() {
    abstract fun crateDao(): CrateDao
    abstract fun sensorReadingDao(): SensorReadingDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: VisionXDatabase? = null

        fun getDatabase(context: Context): VisionXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VisionXDatabase::class.java,
                    "vision_x_cold_storage.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

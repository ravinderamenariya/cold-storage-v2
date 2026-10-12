package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SensorReading
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorReadingDao {
    @Query("SELECT * FROM sensor_readings ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentReadings(limit: Int = 100): Flow<List<SensorReading>>

    @Query("SELECT * FROM sensor_readings WHERE timestamp >= :sinceTimestamp ORDER BY timestamp ASC")
    fun getReadingsSince(sinceTimestamp: Long): Flow<List<SensorReading>>

    @Query("SELECT * FROM sensor_readings ORDER BY timestamp DESC LIMIT 1")
    fun getLatestReading(): Flow<SensorReading?>

    @Query("SELECT * FROM sensor_readings ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestReadingDirect(): SensorReading?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: SensorReading)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(readings: List<SensorReading>)

    @Query("SELECT COUNT(*) FROM sensor_readings")
    suspend fun getReadingCount(): Int

    @Query("DELETE FROM sensor_readings WHERE id NOT IN (SELECT id FROM sensor_readings ORDER BY timestamp DESC LIMIT 500)")
    suspend fun trimOldReadings()
}

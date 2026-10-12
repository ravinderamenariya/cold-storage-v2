package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Crate
import kotlinx.coroutines.flow.Flow

@Dao
interface CrateDao {
    @Query("SELECT * FROM crates WHERE isArchived = 0 ORDER BY storageTimestamp DESC")
    fun getAllCrates(): Flow<List<Crate>>

    @Query("SELECT * FROM crates WHERE id = :id LIMIT 1")
    suspend fun getCrateById(id: String): Crate?

    @Query("SELECT * FROM crates WHERE chamber = :chamber AND isArchived = 0")
    fun getCratesByChamber(chamber: String): Flow<List<Crate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrate(crate: Crate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrates(crates: List<Crate>)

    @Update
    suspend fun updateCrate(crate: Crate)

    @Query("UPDATE crates SET isArchived = 1 WHERE id = :id")
    suspend fun archiveCrate(id: String)

    @Query("DELETE FROM crates WHERE id = :id")
    suspend fun deleteCrate(id: String)

    @Query("SELECT COUNT(*) FROM crates WHERE isArchived = 0")
    suspend fun getActiveCrateCount(): Int
}

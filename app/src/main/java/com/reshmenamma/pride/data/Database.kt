package com.reshmenamma.pride.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey val id: String,
    val name: String, // Explicitly renamed from breed to name
    val variety: String,
    val startStage: Int, // 1 to 6 (Cocoon)
    val startDate: Long,
    val isActive: Boolean = true,
    val harvested: Boolean = false
)

@Entity(tableName = "climate_logs")
data class ClimateLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val batchId: String,
    val timestamp: Long,
    val temperature: Float,
    val humidity: Float
)

@Dao
interface SericultureDao {
    @Query("SELECT * FROM batches ORDER BY startDate DESC")
    fun getAllBatches(): Flow<List<BatchEntity>>

    @Query("SELECT * FROM batches WHERE isActive = 1 ORDER BY startDate DESC")
    fun getActiveBatches(): Flow<List<BatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntity)

    @Update
    suspend fun updateBatch(batch: BatchEntity)

    @Insert
    suspend fun insertLog(log: ClimateLogEntity)

    @Query("SELECT * FROM climate_logs WHERE batchId = :batchId ORDER BY timestamp DESC")
    fun getLogsForBatch(batchId: String): Flow<List<ClimateLogEntity>>

    @Query("DELETE FROM batches WHERE id = :batchId")
    suspend fun deleteBatch(batchId: String)
}

@Database(entities = [BatchEntity::class, ClimateLogEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): SericultureDao
}

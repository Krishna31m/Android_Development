package com.aicareermentor.data.local.dao

import androidx.room.*
import com.aicareermentor.data.local.entity.AnalysisHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AnalysisHistoryEntity): Long

    @Query("SELECT * FROM analysis_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AnalysisHistoryEntity>>

    @Query("SELECT * FROM analysis_history WHERE id = :id")
    suspend fun getById(id: Long): AnalysisHistoryEntity?

    @Delete
    suspend fun delete(entity: AnalysisHistoryEntity)

    @Query("DELETE FROM analysis_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}

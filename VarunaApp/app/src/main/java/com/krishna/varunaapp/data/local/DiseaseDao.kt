package com.krishna.varunaapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DiseaseDao {
    @Query("SELECT * FROM diseases")
    suspend fun getAllDiseases(): List<DiseaseEntity>

    @Query("SELECT * FROM diseases WHERE id = :diseaseId")
    suspend fun getDiseaseById(diseaseId: String): DiseaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiseases(diseases: List<DiseaseEntity>)

    @Query("DELETE FROM diseases")
    suspend fun clearAll()
}
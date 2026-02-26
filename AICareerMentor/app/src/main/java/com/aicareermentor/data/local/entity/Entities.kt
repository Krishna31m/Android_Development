package com.aicareermentor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_history")
data class AnalysisHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val title: String,
    val summary: String,
    val fullResult: String,
    val score: Int?,
    val timestamp: Long = System.currentTimeMillis()
)

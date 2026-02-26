package com.aicareermentor.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aicareermentor.data.local.dao.AnalysisHistoryDao
import com.aicareermentor.data.local.entity.AnalysisHistoryEntity

@Database(entities = [AnalysisHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): AnalysisHistoryDao
}

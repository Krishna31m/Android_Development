package com.aicareermentor.data.repository

import com.aicareermentor.data.local.dao.AnalysisHistoryDao
import com.aicareermentor.data.local.entity.AnalysisHistoryEntity
import com.aicareermentor.domain.model.AnalysisHistory
import com.aicareermentor.domain.model.AnalysisType
import com.aicareermentor.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(private val dao: AnalysisHistoryDao) : HistoryRepository {

    override fun getAllHistory(): Flow<List<AnalysisHistory>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun saveAnalysis(type: AnalysisType, title: String, summary: String, fullResult: String, score: Int?): Long =
        dao.insert(AnalysisHistoryEntity(type = type.name, title = title, summary = summary, fullResult = fullResult, score = score))

    override suspend fun deleteAnalysis(id: Long) = dao.deleteById(id)

    override suspend fun getById(id: Long): AnalysisHistory? = dao.getById(id)?.toDomain()

    private fun AnalysisHistoryEntity.toDomain() = AnalysisHistory(
        id = id, type = AnalysisType.valueOf(type), title = title,
        summary = summary, fullResult = fullResult, score = score, timestamp = timestamp
    )
}

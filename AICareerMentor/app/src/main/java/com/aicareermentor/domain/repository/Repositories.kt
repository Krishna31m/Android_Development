package com.aicareermentor.domain.repository

import com.aicareermentor.domain.model.*
import kotlinx.coroutines.flow.Flow

interface CareerRepository {
    suspend fun analyzeResume(resumeText: String): Result<ResumeAnalysis>
    suspend fun analyzeSkillGap(resumeText: String, targetRole: String): Result<SkillGapAnalysis>
    suspend fun generateInterviewQuestions(role: String, resumeText: String = ""): Result<List<InterviewQuestion>>
    suspend fun evaluateAnswer(question: String, answer: String, role: String): Result<AnswerEvaluation>
    suspend fun generateCareerRoadmap(domain: String, currentLevel: String): Result<CareerRoadmap>
}

interface HistoryRepository {
    fun getAllHistory(): Flow<List<AnalysisHistory>>
    suspend fun saveAnalysis(type: AnalysisType, title: String, summary: String, fullResult: String, score: Int?): Long
    suspend fun deleteAnalysis(id: Long)
    suspend fun getById(id: Long): AnalysisHistory?
}

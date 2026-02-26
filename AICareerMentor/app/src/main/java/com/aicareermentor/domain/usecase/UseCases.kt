package com.aicareermentor.domain.usecase

import com.aicareermentor.domain.model.*
import com.aicareermentor.domain.repository.CareerRepository
import com.aicareermentor.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnalyzeResumeUseCase @Inject constructor(
    private val careerRepo: CareerRepository,
    private val historyRepo: HistoryRepository
) {
    suspend operator fun invoke(resumeText: String): Result<ResumeAnalysis> =
        careerRepo.analyzeResume(resumeText).also { result ->
            result.onSuccess { a ->
                historyRepo.saveAnalysis(AnalysisType.RESUME, "Resume Analysis", a.summary, a.toString(), a.overallScore)
            }
        }
}

class AnalyzeSkillGapUseCase @Inject constructor(
    private val careerRepo: CareerRepository,
    private val historyRepo: HistoryRepository
) {
    suspend operator fun invoke(resumeText: String, targetRole: String): Result<SkillGapAnalysis> =
        careerRepo.analyzeSkillGap(resumeText, targetRole).also { result ->
            result.onSuccess { a ->
                historyRepo.saveAnalysis(AnalysisType.SKILL_GAP, "Skill Gap: $targetRole",
                    "Match: ${a.matchPercentage}% | Missing ${a.missingSkills.size} skills", a.toString(), a.matchPercentage)
            }
        }
}

class GenerateInterviewQuestionsUseCase @Inject constructor(private val repo: CareerRepository) {
    suspend operator fun invoke(role: String, resumeText: String = ""): Result<List<InterviewQuestion>> =
        repo.generateInterviewQuestions(role, resumeText)
}

class EvaluateAnswerUseCase @Inject constructor(private val repo: CareerRepository) {
    suspend operator fun invoke(question: String, answer: String, role: String): Result<AnswerEvaluation> =
        repo.evaluateAnswer(question, answer, role)
}

class GenerateCareerRoadmapUseCase @Inject constructor(
    private val careerRepo: CareerRepository,
    private val historyRepo: HistoryRepository
) {
    suspend operator fun invoke(domain: String, currentLevel: String): Result<CareerRoadmap> =
        careerRepo.generateCareerRoadmap(domain, currentLevel).also { result ->
            result.onSuccess { r ->
                historyRepo.saveAnalysis(AnalysisType.ROADMAP, "Roadmap: $domain",
                    "Duration: ${r.totalDuration} | ${r.phases.size} phases", r.toString(), null)
            }
        }
}

class GetHistoryUseCase @Inject constructor(private val repo: HistoryRepository) {
    operator fun invoke(): Flow<List<AnalysisHistory>> = repo.getAllHistory()
}

class DeleteHistoryUseCase @Inject constructor(private val repo: HistoryRepository) {
    suspend operator fun invoke(id: Long) = repo.deleteAnalysis(id)
}

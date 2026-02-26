package com.aicareermentor.data.repository

import com.aicareermentor.BuildConfig
import com.aicareermentor.data.remote.api.GeminiApiService
import com.aicareermentor.data.remote.api.PromptTemplates
import com.aicareermentor.data.remote.dto.GeminiContent
import com.aicareermentor.data.remote.dto.GeminiPart
import com.aicareermentor.data.remote.dto.GeminiRequest
import com.aicareermentor.domain.model.*
import com.aicareermentor.domain.repository.CareerRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CareerRepositoryImpl @Inject constructor(
    private val api: GeminiApiService,
    private val gson: Gson
) : CareerRepository {

    private val key get() = BuildConfig.GEMINI_API_KEY

    override suspend fun analyzeResume(resumeText: String): Result<ResumeAnalysis> =
        call(PromptTemplates.resumeAnalysis(resumeText)) { j ->
            ResumeAnalysis(
                overallScore         = j.int("overallScore"),
                strengths            = j.strList("strengths"),
                skillGaps            = j.strList("skillGaps"),
                missingTechnologies  = j.strList("missingTechnologies"),
                improvements         = j.strList("improvements"),
                summary              = j.str("summary"),
                atsScore             = j.int("atsScore"),
                keywordOptimization  = j.strList("keywordOptimization")
            )
        }

    override suspend fun analyzeSkillGap(resumeText: String, targetRole: String): Result<SkillGapAnalysis> =
        call(PromptTemplates.skillGapAnalysis(resumeText, targetRole)) { j ->
            SkillGapAnalysis(
                currentSkills        = j.strList("currentSkills"),
                requiredSkills       = j.strList("requiredSkills"),
                missingSkills        = j.strList("missingSkills"),
                matchPercentage      = j.int("matchPercentage"),
                roadmap              = j.getAsJsonArray("roadmap")?.map { el ->
                    val p = el.asJsonObject
                    RoadmapPhase(
                        phase     = p.str("phase"),
                        duration  = p.str("duration"),
                        topics    = p.strList("topics"),
                        resources = p.strList("resources")
                    )
                } ?: emptyList(),
                recommendedProjects  = j.getAsJsonArray("recommendedProjects")?.map { el ->
                    val p = el.asJsonObject
                    RecommendedProject(p.str("title"), p.str("description"), p.strList("skills"))
                } ?: emptyList(),
                estimatedTimeToReady = j.str("estimatedTimeToReady")
            )
        }

    override suspend fun generateInterviewQuestions(role: String, resumeText: String): Result<List<InterviewQuestion>> =
        call(PromptTemplates.interviewQuestions(role, resumeText)) { j ->
            j.getAsJsonArray("questions")?.map { el ->
                val q = el.asJsonObject
                InterviewQuestion(q.int("id"), q.str("question"), q.str("category"), q.str("difficulty"), q.str("hint"))
            } ?: emptyList()
        }

    override suspend fun evaluateAnswer(question: String, answer: String, role: String): Result<AnswerEvaluation> =
        call(PromptTemplates.evaluateAnswer(question, answer, role)) { j ->
            AnswerEvaluation(j.int("score"), j.str("verdict"), j.strList("strengths"),
                j.strList("weaknesses"), j.str("idealAnswer"), j.strList("improvementTips"))
        }

    override suspend fun generateCareerRoadmap(domain: String, currentLevel: String): Result<CareerRoadmap> =
        call(PromptTemplates.careerRoadmap(domain, currentLevel)) { j ->
            CareerRoadmap(
                domain        = j.str("domain"),
                totalDuration = j.str("totalDuration"),
                phases        = j.getAsJsonArray("phases")?.map { el ->
                    val p = el.asJsonObject
                    val detRes = p.getAsJsonArray("resources")?.map { r ->
                        if (r.isJsonObject) {
                            val ro = r.asJsonObject
                            LearningResource(ro.str("type"), ro.str("name"), ro.str("url"))
                        } else LearningResource("", r.asString, "")
                    } ?: emptyList()
                    RoadmapPhase(
                        phase            = p.str("phase"),
                        level            = p.str("level"),
                        duration         = p.str("duration"),
                        objectives       = p.strList("objectives"),
                        topics           = p.strList("topics"),
                        projects         = p.strList("projects"),
                        milestones       = p.strList("milestones"),
                        resources        = detRes.map { it.name }.filter { it.isNotEmpty() },
                        detailedResources = detRes
                    )
                } ?: emptyList(),
                careerPaths   = j.strList("careerPaths"),
                salaryRange   = j.str("salaryRange"),
                topCompanies  = j.strList("topCompanies")
            )
        }

    // ── helpers ──────────────────────────────────────

    private suspend fun <T> call(prompt: String, parse: (JsonObject) -> T): Result<T> {
        return try {
            val req  = GeminiRequest(listOf(GeminiContent(listOf(GeminiPart(prompt)))))
            val resp = api.generateContent(key, req)
            if (!resp.isSuccessful) {
                val err = resp.errorBody()?.string()
                Timber.e("Gemini error ${resp.code()}: $err")
                return Result.failure(Exception("API error ${resp.code()}. Rate limit reached. Please wait 1 minute and try again."))
            }
            val body = resp.body()
            body?.error?.let { return Result.failure(Exception("Gemini: ${it.message}")) }
            val text = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return Result.failure(Exception("Empty response from Gemini"))
            val json = gson.fromJson(cleanJson(text), JsonObject::class.java)
            Result.success(parse(json))
        } catch (e: Exception) {
            Timber.e(e, "Gemini call failed")
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    private fun cleanJson(raw: String) = raw.trim()
        .removePrefix("```json").removePrefix("```").removeSuffix("```").trim()

    private fun JsonObject.str(key: String) = get(key)?.takeIf { !it.isJsonNull }?.asString ?: ""
    private fun JsonObject.int(key: String) = get(key)?.takeIf { !it.isJsonNull }?.asInt ?: 0
    private fun JsonObject.strList(key: String) = try {
        getAsJsonArray(key)?.map { it.asString } ?: emptyList()
    } catch (e: Exception) { emptyList() }
}

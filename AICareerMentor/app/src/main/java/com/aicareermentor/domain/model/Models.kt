package com.aicareermentor.domain.model

// ─── Resume ───────────────────────────────────────────
data class ResumeAnalysis(
    val overallScore: Int,
    val strengths: List<String>,
    val skillGaps: List<String>,
    val missingTechnologies: List<String>,
    val improvements: List<String>,
    val summary: String,
    val atsScore: Int,
    val keywordOptimization: List<String>
)

// ─── Skill Gap ────────────────────────────────────────
data class SkillGapAnalysis(
    val currentSkills: List<String>,
    val requiredSkills: List<String>,
    val missingSkills: List<String>,
    val matchPercentage: Int,
    val roadmap: List<RoadmapPhase>,
    val recommendedProjects: List<RecommendedProject>,
    val estimatedTimeToReady: String
)

data class RoadmapPhase(
    val phase: String,
    val level: String = "",
    val duration: String,
    val objectives: List<String> = emptyList(),
    val topics: List<String>,
    val projects: List<String> = emptyList(),
    val milestones: List<String> = emptyList(),
    val resources: List<String>,
    val detailedResources: List<LearningResource> = emptyList()
)

data class LearningResource(val type: String, val name: String, val url: String)

data class RecommendedProject(val title: String, val description: String, val skills: List<String>)

// ─── Interview ────────────────────────────────────────
data class InterviewQuestion(
    val id: Int,
    val question: String,
    val category: String,
    val difficulty: String,
    val hint: String
)

data class AnswerEvaluation(
    val score: Int,
    val verdict: String,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val idealAnswer: String,
    val improvementTips: List<String>
)

// ─── Roadmap ──────────────────────────────────────────
data class CareerRoadmap(
    val domain: String,
    val totalDuration: String,
    val phases: List<RoadmapPhase>,
    val careerPaths: List<String>,
    val salaryRange: String,
    val topCompanies: List<String>
)

// ─── History ──────────────────────────────────────────
data class AnalysisHistory(
    val id: Long,
    val type: AnalysisType,
    val title: String,
    val summary: String,
    val fullResult: String,
    val score: Int?,
    val timestamp: Long
)

enum class AnalysisType(val label: String) {
    RESUME("Resume Analysis"),
    SKILL_GAP("Skill Gap"),
    INTERVIEW("Mock Interview"),
    ROADMAP("Career Roadmap")
}

// ─── Role list ────────────────────────────────────────
data class CareerRole(val title: String, val icon: String, val category: String)

val popularRoles = listOf(
    CareerRole("Android Developer",         "📱", "Mobile"),
    CareerRole("iOS Developer",             "🍎", "Mobile"),
    CareerRole("Frontend Developer",        "🌐", "Web"),
    CareerRole("Backend Developer",         "⚙️", "Web"),
    CareerRole("Full Stack Developer",      "💻", "Web"),
    CareerRole("Data Scientist",            "📊", "Data"),
    CareerRole("Machine Learning Engineer", "🤖", "AI/ML"),
    CareerRole("DevOps Engineer",           "🔧", "Infrastructure"),
    CareerRole("Cloud Architect",           "☁️", "Infrastructure"),
    CareerRole("Cybersecurity Engineer",    "🔐", "Security"),
    CareerRole("Product Manager",           "📋", "Management"),
    CareerRole("UI/UX Designer",            "🎨", "Design")
)

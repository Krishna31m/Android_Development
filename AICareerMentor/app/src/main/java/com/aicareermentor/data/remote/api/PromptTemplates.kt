package com.aicareermentor.data.remote.api

object PromptTemplates {

    fun resumeAnalysis(resumeText: String) = """
You are an expert career counselor and technical recruiter with 15+ years of experience.
Analyze the following resume and return ONLY valid JSON with no markdown, no explanation, no code fences.

Resume:
---
$resumeText
---

Return exactly this JSON structure:
{
  "overallScore": <0-100>,
  "strengths": ["strength1","strength2","strength3"],
  "skillGaps": ["gap1","gap2"],
  "missingTechnologies": ["tech1","tech2"],
  "improvements": ["tip1","tip2","tip3","tip4"],
  "summary": "2-3 sentence overall assessment",
  "atsScore": <0-100>,
  "keywordOptimization": ["keyword1","keyword2","keyword3"]
}
""".trimIndent()

    fun skillGapAnalysis(resumeText: String, targetRole: String) = """
You are a senior technical career advisor. Analyze the skill gap between this resume and the target role.
Return ONLY valid JSON with no markdown, no explanation, no code fences.

Target Role: $targetRole

Resume:
---
$resumeText
---

Return exactly this JSON structure:
{
  "currentSkills": ["skill1","skill2"],
  "requiredSkills": ["skill1","skill2","skill3"],
  "missingSkills": ["missing1","missing2"],
  "matchPercentage": <0-100>,
  "roadmap": [
    {
      "phase": "Phase 1: Foundations",
      "duration": "4-6 weeks",
      "topics": ["topic1","topic2"],
      "resources": ["resource1","resource2"]
    }
  ],
  "recommendedProjects": [
    {
      "title": "Project Title",
      "description": "What to build and why",
      "skills": ["skill1","skill2"]
    }
  ],
  "estimatedTimeToReady": "3-4 months"
}
Include at least 3 roadmap phases and 3 recommended projects.
""".trimIndent()

    fun interviewQuestions(role: String, resumeText: String = "") = """
You are a senior technical interviewer at a top tech company.
Generate exactly 10 interview questions for a $role position.
${if (resumeText.isNotEmpty()) "Tailor to this candidate:\n---\n${resumeText.take(1500)}\n---" else ""}
Return ONLY valid JSON with no markdown, no explanation, no code fences.

Return exactly this JSON structure:
{
  "questions": [
    {
      "id": 1,
      "question": "The interview question",
      "category": "Technical|Behavioral|System Design|Problem Solving",
      "difficulty": "Easy|Medium|Hard",
      "hint": "What the interviewer is looking for"
    }
  ]
}
""".trimIndent()

    fun evaluateAnswer(question: String, answer: String, role: String) = """
You are an expert technical interviewer evaluating a candidate for a $role position.
Return ONLY valid JSON with no markdown, no explanation, no code fences.

Question: $question
Candidate's Answer: $answer

Return exactly this JSON structure:
{
  "score": <0-10>,
  "verdict": "Excellent|Good|Average|Needs Improvement",
  "strengths": ["what was good"],
  "weaknesses": ["what was missing or wrong"],
  "idealAnswer": "What a perfect answer would cover",
  "improvementTips": ["specific tip1","specific tip2"]
}
""".trimIndent()

    fun careerRoadmap(domain: String, currentLevel: String) = """
You are a senior engineering mentor creating a comprehensive career roadmap.
Return ONLY valid JSON with no markdown, no explanation, no code fences.

Domain: $domain
Current Level: $currentLevel

Return exactly this JSON structure:
{
  "domain": "$domain",
  "totalDuration": "12-18 months",
  "phases": [
    {
      "phase": "Phase 1: Fundamentals",
      "level": "Beginner",
      "duration": "2-3 months",
      "objectives": ["objective1","objective2"],
      "topics": ["topic1","topic2","topic3"],
      "projects": ["project idea 1","project idea 2"],
      "milestones": ["milestone1","milestone2"],
      "resources": ["Course: Resource Name on Platform","Book: Title by Author"]
    }
  ],
  "careerPaths": ["Job Title 1","Job Title 2","Job Title 3"],
  "salaryRange": "$80k - $150k depending on location",
  "topCompanies": ["Company1","Company2","Company3","Company4","Company5"]
}
Include exactly 4 phases: Beginner, Intermediate, Advanced, Expert.
""".trimIndent()
}

package com.aicareermentor.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerializedName("generationConfig") val generationConfig: GenerationConfig = GenerationConfig()
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

data class GeminiPart(val text: String)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    @SerializedName("maxOutputTokens") val maxOutputTokens: Int = 2048,
    @SerializedName("topK") val topK: Int = 40,
    @SerializedName("topP") val topP: Float = 0.95f
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?,
    val error: GeminiError?
)

data class GeminiCandidate(
    val content: GeminiContent?,
    @SerializedName("finishReason") val finishReason: String?
)

data class GeminiError(val code: Int, val message: String, val status: String)

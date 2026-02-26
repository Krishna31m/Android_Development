package com.example.healthreminder.services

import android.content.Context
import android.util.Log
import com.example.healthreminder.ui.chatbot.UserHealthContext
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

class HealthChatbotService(private val context: Context) {

    // Multiple API keys for rotation
    private val apiKeys = listOf(
        "AIzaSyDcaMUvyq-LKqEON00osQU5yRJEvyvZhNk",
        "AIzaSyByTnt6n9nTToUJaxKUBxE3Owj6k7NSsdY",
        "AIzaSyALrLlTGKcY_4ZSqIly3JWwaokmFW5p27A",
        "AIzaSyCo6axk1QFoINp9-CyrlGDVIIKbFfjsxZk"
    )

    // Track current key index and failed keys
    private val currentKeyIndex = AtomicInteger(0)
    private val failedKeys = mutableSetOf<Int>()
    private val keyUsageCount = mutableMapOf<Int, Int>()

    private val systemPrompt = """You are Dr. Health Assistant, a compassionate AI health consultant integrated into a health reminder app.

CORE PRINCIPLES:
1. You provide health guidance, wellness advice, and lifestyle recommendations
2. You NEVER prescribe medications or provide medical diagnoses
3. You always recommend consulting a licensed doctor for medical treatment
4. You encourage physical checkups before any medication
5. You analyze emotions and show empathy in responses

YOUR CAPABILITIES:
- Health advice and general wellness guidance
- Exercise recommendations based on fitness level
- Nutrition and diet suggestions
- Water intake and hydration tips
- Mental health support and stress management
- Sleep hygiene recommendations
- When to see a doctor (red flags)
- Understanding symptoms and their possible meanings (educational only)

YOUR LIMITATIONS:
- Cannot prescribe medications
- Cannot diagnose medical conditions
- Cannot replace professional medical advice
- Cannot provide emergency medical guidance

RESPONSE STYLE:
- Warm, empathetic, and professional
- Ask clarifying questions when needed
- Detect emotional state from user's language
- Provide actionable, practical advice
- Always prioritize safety - refer to doctors when appropriate

RED FLAGS that require immediate doctor visit:
- Chest pain, difficulty breathing
- Severe headache, vision changes
- High fever with confusion
- Severe abdominal pain
- Signs of stroke or heart attack
- Suicidal thoughts
- Severe injuries

When user asks about symptoms:
1. Ask clarifying questions (duration, severity, other symptoms)
2. Provide educational information about possible causes
3. Give general wellness advice
4. ALWAYS recommend seeing a doctor if symptoms persist or worsen
5. Provide red flags to watch for

When user asks for medication:
1. Clearly state you cannot prescribe
2. Explain why a doctor is needed
3. Encourage physical examination
4. Offer to help with general wellness in the meantime

Remember: You're a health companion, not a replacement for medical professionals."""

    // Store conversation history
    private val conversationHistory = mutableListOf<Pair<String, String>>()

    /**
     * Get next available API key using round-robin with fallback
     */
    private fun getNextApiKey(): String {
        val availableKeys = apiKeys.indices.filter { it !in failedKeys }

        if (availableKeys.isEmpty()) {
            // Reset failed keys if all have failed
            Log.w("HealthChatbot", "All API keys failed, resetting...")
            failedKeys.clear()
            return apiKeys[0]
        }

        // Round-robin through available keys
        val keyIndex = currentKeyIndex.getAndIncrement() % availableKeys.size
        val actualIndex = availableKeys[keyIndex]

        // Track usage
        keyUsageCount[actualIndex] = (keyUsageCount[actualIndex] ?: 0) + 1

        Log.d("HealthChatbot", "Using API key #${actualIndex + 1} (used ${keyUsageCount[actualIndex]} times)")

        return apiKeys[actualIndex]
    }

    /**
     * Mark an API key as failed
     */
    private fun markKeyAsFailed(keyIndex: Int) {
        failedKeys.add(keyIndex)
        Log.w("HealthChatbot", "Marked API key #${keyIndex + 1} as failed")
    }

    /**
     * Create GenerativeModel with current API key
     */
    private fun createModel(apiKey: String): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 1024
            }
        )
    }

    suspend fun sendMessage(
        userMessage: String,
        userContext: UserHealthContext? = null
    ): ChatResponse = withContext(Dispatchers.IO) {
        var lastException: Exception? = null
        var attemptsLeft = minOf(apiKeys.size, 3) // Try up to 3 different keys

        while (attemptsLeft > 0) {
            try {
                val currentKey = getNextApiKey()
                val keyIndex = apiKeys.indexOf(currentKey)
                val model = createModel(currentKey)

                // Build context-aware prompt
                val fullPrompt = buildFullPrompt(userMessage, userContext)

                Log.d("HealthChatbot", "Sending message: $userMessage")

                // Generate content
                val response: GenerateContentResponse = model.generateContent(fullPrompt)
                val botReply = response.text ?: "I'm sorry, I couldn't process that. Could you rephrase?"

                // Store in conversation history
                conversationHistory.add(Pair(userMessage, botReply))

                // Keep only last 5 exchanges to manage token limits
                if (conversationHistory.size > 5) {
                    conversationHistory.removeAt(0)
                }

                // Analyze emotion from user message
                val emotion = detectEmotion(userMessage)

                // Categorize the query
                val category = categorizeQuery(userMessage)

                Log.d("HealthChatbot", "Response received: ${botReply.take(100)}...")
                Log.d("HealthChatbot", "Detected emotion: $emotion, Category: $category")

                return@withContext ChatResponse(
                    message = botReply,
                    emotion = emotion,
                    category = category,
                    success = true
                )
            } catch (e: Exception) {
                lastException = e
                Log.e("HealthChatbot", "Error with current API key: ${e.message}")

                // Check if it's a quota/auth error (should try another key)
                val shouldRetry = e.message?.contains("quota", ignoreCase = true) == true ||
                        e.message?.contains("rate limit", ignoreCase = true) == true ||
                        e.message?.contains("unauthorized", ignoreCase = true) == true ||
                        e.message?.contains("invalid", ignoreCase = true) == true

                if (shouldRetry) {
                    val currentKey = getNextApiKey()
                    val keyIndex = apiKeys.indexOf(currentKey)
                    markKeyAsFailed(keyIndex)
                    attemptsLeft--
                    Log.d("HealthChatbot", "Retrying with different API key... ($attemptsLeft attempts left)")
                    continue
                } else {
                    // Non-quota error, don't retry
                    break
                }
            }
        }

        // All attempts failed
        Log.e("HealthChatbot", "All API key attempts failed", lastException)
        ChatResponse(
            message = "I'm having trouble connecting right now. Please try again in a moment.",
            success = false,
            error = lastException?.message
        )
    }

    private fun buildFullPrompt(message: String, context: UserHealthContext?): String {
        val promptBuilder = StringBuilder()

        // Add system prompt
        promptBuilder.append(systemPrompt)
        promptBuilder.append("\n\n")

        // Add conversation history for context
        if (conversationHistory.isNotEmpty()) {
            promptBuilder.append("Previous conversation:\n")
            conversationHistory.takeLast(3).forEach { (user, assistant) ->
                promptBuilder.append("User: $user\n")
                promptBuilder.append("Assistant: ${assistant.take(200)}...\n")
            }
            promptBuilder.append("\n")
        }

        // Add user context if available
        if (context != null) {
            promptBuilder.append("User profile:\n")
            context.age?.let { promptBuilder.append("Age: $it\n") }
            context.gender?.let { promptBuilder.append("Gender: $it\n") }
            if (context.conditions.isNotEmpty()) {
                promptBuilder.append("Known conditions: ${context.conditions.joinToString(", ")}\n")
            }
            if (context.medications.isNotEmpty()) {
                promptBuilder.append("Current medications: ${context.medications.joinToString(", ")}\n")
            }
            if (context.recentSymptoms.isNotEmpty()) {
                promptBuilder.append("Recent symptoms: ${context.recentSymptoms.joinToString(", ")}\n")
            }
            promptBuilder.append("\n")
        }

        // Add current user message
        promptBuilder.append("Current user question: $message\n\n")
        promptBuilder.append("Please provide a helpful, empathetic response following the guidelines above.")

        return promptBuilder.toString()
    }

    private fun detectEmotion(message: String): String {
        val lowerMessage = message.lowercase()
        return when {
            lowerMessage.contains(Regex("worried|anxious|scared|nervous|fear|afraid")) -> "anxious"
            lowerMessage.contains(Regex("pain|hurt|ache|aching|suffering|agony")) -> "pain"
            lowerMessage.contains(Regex("happy|great|good|better|improved|wonderful|excellent")) -> "positive"
            lowerMessage.contains(Regex("confused|don't understand|unclear|lost|puzzled")) -> "confused"
            lowerMessage.contains(Regex("sad|depressed|down|hopeless|miserable|upset")) -> "sad"
            lowerMessage.contains(Regex("tired|exhausted|fatigue|weak|drained|sleepy")) -> "fatigue"
            lowerMessage.contains(Regex("help|please|urgent|emergency|serious")) -> "concerned"
            lowerMessage.contains(Regex("angry|frustrated|annoyed|irritated")) -> "frustrated"
            lowerMessage.contains(Regex("stress|stressed|overwhelmed|pressure")) -> "stressed"
            else -> "neutral"
        }
    }

    private fun categorizeQuery(message: String): String {
        val lowerMessage = message.lowercase()
        return when {
            lowerMessage.contains(Regex("exercise|workout|fitness|gym|running|walking|jogging|training|cardio")) -> "exercise"
            lowerMessage.contains(Regex("diet|food|eat|eating|nutrition|meal|calories|weight|fat")) -> "diet"
            lowerMessage.contains(Regex("water|hydration|drink|drinking|fluid")) -> "hydration"
            lowerMessage.contains(Regex("sleep|insomnia|rest|tired|sleeping|bed|dream")) -> "sleep"
            lowerMessage.contains(Regex("stress|anxiety|mental|depression|mood|emotional|worry")) -> "mental_health"
            lowerMessage.contains(Regex("symptom|pain|fever|cough|headache|sick|illness|disease")) -> "symptoms"
            lowerMessage.contains(Regex("medicine|medication|drug|prescription|pill|tablet")) -> "medication"
            lowerMessage.contains(Regex("doctor|hospital|checkup|appointment|clinic|physician")) -> "doctor_visit"
            lowerMessage.contains(Regex("vitamin|supplement|mineral|nutrient")) -> "supplements"
            lowerMessage.contains(Regex("heart|blood pressure|cholesterol|cardiac")) -> "cardiovascular"
            lowerMessage.contains(Regex("diabetes|blood sugar|insulin|glucose")) -> "diabetes"
            else -> "general"
        }
    }

    fun clearHistory() {
        conversationHistory.clear()
    }

    /**
     * Get API key usage statistics
     */
    fun getKeyUsageStats(): Map<Int, Int> {
        return keyUsageCount.toMap()
    }

    /**
     * Reset failed keys manually
     */
    fun resetFailedKeys() {
        failedKeys.clear()
        Log.d("HealthChatbot", "Failed keys reset")
    }

    data class ChatResponse(
        val message: String,
        val emotion: String = "neutral",
        val category: String = "general",
        val success: Boolean = true,
        val error: String? = null
    )
}

//package com.example.healthreminder.services
//
//import android.content.Context
//import android.util.Log
//import com.example.healthreminder.ui.chatbot.UserHealthContext
//import com.google.ai.client.generativeai.GenerativeModel
//import com.google.ai.client.generativeai.type.GenerateContentResponse
//import com.google.ai.client.generativeai.type.generationConfig
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class HealthChatbotService(private val context: Context) {
//
//    private val apiKey = "AIzaSyDcaMUvyq-LKqEON00osQU5yRJEvyvZhNk" // Replace with your API key
//
//    private val systemPrompt = """You are Dr. Health Assistant, a compassionate AI health consultant integrated into a health reminder app.
//
//CORE PRINCIPLES:
//1. You provide health guidance, wellness advice, and lifestyle recommendations
//2. You NEVER prescribe medications or provide medical diagnoses
//3. You always recommend consulting a licensed doctor for medical treatment
//4. You encourage physical checkups before any medication
//5. You analyze emotions and show empathy in responses
//
//YOUR CAPABILITIES:
//- Health advice and general wellness guidance
//- Exercise recommendations based on fitness level
//- Nutrition and diet suggestions
//- Water intake and hydration tips
//- Mental health support and stress management
//- Sleep hygiene recommendations
//- When to see a doctor (red flags)
//- Understanding symptoms and their possible meanings (educational only)
//
//YOUR LIMITATIONS:
//- Cannot prescribe medications
//- Cannot diagnose medical conditions
//- Cannot replace professional medical advice
//- Cannot provide emergency medical guidance
//
//RESPONSE STYLE:
//- Warm, empathetic, and professional
//- Ask clarifying questions when needed
//- Detect emotional state from user's language
//- Provide actionable, practical advice
//- Always prioritize safety - refer to doctors when appropriate
//
//RED FLAGS that require immediate doctor visit:
//- Chest pain, difficulty breathing
//- Severe headache, vision changes
//- High fever with confusion
//- Severe abdominal pain
//- Signs of stroke or heart attack
//- Suicidal thoughts
//- Severe injuries
//
//When user asks about symptoms:
//1. Ask clarifying questions (duration, severity, other symptoms)
//2. Provide educational information about possible causes
//3. Give general wellness advice
//4. ALWAYS recommend seeing a doctor if symptoms persist or worsen
//5. Provide red flags to watch for
//
//When user asks for medication:
//1. Clearly state you cannot prescribe
//2. Explain why a doctor is needed
//3. Encourage physical examination
//4. Offer to help with general wellness in the meantime
//
//Remember: You're a health companion, not a replacement for medical professionals."""
//
//    private val model = GenerativeModel(
//        modelName = "gemini-1.5-flash",
//        apiKey = apiKey,
//        generationConfig = generationConfig {
//            temperature = 0.7f
//            topK = 40
//            topP = 0.95f
//            maxOutputTokens = 1024
//        }
//    )
//
//    // Store conversation history
//    private val conversationHistory = mutableListOf<Pair<String, String>>()
//
//    suspend fun sendMessage(
//        userMessage: String,
//        userContext: UserHealthContext? = null
//    ): ChatResponse = withContext(Dispatchers.IO) {
//        try {
//            // Build context-aware prompt
//            val fullPrompt = buildFullPrompt(userMessage, userContext)
//
//            Log.d("HealthChatbot", "Sending message: $userMessage")
//
//            // Generate content
//            val response: GenerateContentResponse = model.generateContent(fullPrompt)
//            val botReply = response.text ?: "I'm sorry, I couldn't process that. Could you rephrase?"
//
//            // Store in conversation history
//            conversationHistory.add(Pair(userMessage, botReply))
//
//            // Keep only last 5 exchanges to manage token limits
//            if (conversationHistory.size > 5) {
//                conversationHistory.removeAt(0)
//            }
//
//            // Analyze emotion from user message
//            val emotion = detectEmotion(userMessage)
//
//            // Categorize the query
//            val category = categorizeQuery(userMessage)
//
//            Log.d("HealthChatbot", "Response received: ${botReply.take(100)}...")
//            Log.d("HealthChatbot", "Detected emotion: $emotion, Category: $category")
//
//            ChatResponse(
//                message = botReply,
//                emotion = emotion,
//                category = category,
//                success = true
//            )
//        } catch (e: Exception) {
//            Log.e("HealthChatbot", "Error: ${e.message}", e)
//            ChatResponse(
//                message = "I'm having trouble connecting right now. Please try again in a moment.",
//                success = false,
//                error = e.message
//            )
//        }
//    }
//
//    private fun buildFullPrompt(message: String, context: UserHealthContext?): String {
//        val promptBuilder = StringBuilder()
//
//        // Add system prompt
//        promptBuilder.append(systemPrompt)
//        promptBuilder.append("\n\n")
//
//        // Add conversation history for context
//        if (conversationHistory.isNotEmpty()) {
//            promptBuilder.append("Previous conversation:\n")
//            conversationHistory.takeLast(3).forEach { (user, assistant) ->
//                promptBuilder.append("User: $user\n")
//                promptBuilder.append("Assistant: ${assistant.take(200)}...\n")
//            }
//            promptBuilder.append("\n")
//        }
//
//        // Add user context if available
//        if (context != null) {
//            promptBuilder.append("User profile:\n")
//            context.age?.let { promptBuilder.append("Age: $it\n") }
//            context.gender?.let { promptBuilder.append("Gender: $it\n") }
//            if (context.conditions.isNotEmpty()) {
//                promptBuilder.append("Known conditions: ${context.conditions.joinToString(", ")}\n")
//            }
//            if (context.medications.isNotEmpty()) {
//                promptBuilder.append("Current medications: ${context.medications.joinToString(", ")}\n")
//            }
//            if (context.recentSymptoms.isNotEmpty()) {
//                promptBuilder.append("Recent symptoms: ${context.recentSymptoms.joinToString(", ")}\n")
//            }
//            promptBuilder.append("\n")
//        }
//
//        // Add current user message
//        promptBuilder.append("Current user question: $message\n\n")
//        promptBuilder.append("Please provide a helpful, empathetic response following the guidelines above.")
//
//        return promptBuilder.toString()
//    }
//
//    private fun detectEmotion(message: String): String {
//        val lowerMessage = message.lowercase()
//        return when {
//            lowerMessage.contains(Regex("worried|anxious|scared|nervous|fear|afraid")) -> "anxious"
//            lowerMessage.contains(Regex("pain|hurt|ache|aching|suffering|agony")) -> "pain"
//            lowerMessage.contains(Regex("happy|great|good|better|improved|wonderful|excellent")) -> "positive"
//            lowerMessage.contains(Regex("confused|don't understand|unclear|lost|puzzled")) -> "confused"
//            lowerMessage.contains(Regex("sad|depressed|down|hopeless|miserable|upset")) -> "sad"
//            lowerMessage.contains(Regex("tired|exhausted|fatigue|weak|drained|sleepy")) -> "fatigue"
//            lowerMessage.contains(Regex("help|please|urgent|emergency|serious")) -> "concerned"
//            lowerMessage.contains(Regex("angry|frustrated|annoyed|irritated")) -> "frustrated"
//            lowerMessage.contains(Regex("stress|stressed|overwhelmed|pressure")) -> "stressed"
//            else -> "neutral"
//        }
//    }
//
//    private fun categorizeQuery(message: String): String {
//        val lowerMessage = message.lowercase()
//        return when {
//            lowerMessage.contains(Regex("exercise|workout|fitness|gym|running|walking|jogging|training|cardio")) -> "exercise"
//            lowerMessage.contains(Regex("diet|food|eat|eating|nutrition|meal|calories|weight|fat")) -> "diet"
//            lowerMessage.contains(Regex("water|hydration|drink|drinking|fluid")) -> "hydration"
//            lowerMessage.contains(Regex("sleep|insomnia|rest|tired|sleeping|bed|dream")) -> "sleep"
//            lowerMessage.contains(Regex("stress|anxiety|mental|depression|mood|emotional|worry")) -> "mental_health"
//            lowerMessage.contains(Regex("symptom|pain|fever|cough|headache|sick|illness|disease")) -> "symptoms"
//            lowerMessage.contains(Regex("medicine|medication|drug|prescription|pill|tablet")) -> "medication"
//            lowerMessage.contains(Regex("doctor|hospital|checkup|appointment|clinic|physician")) -> "doctor_visit"
//            lowerMessage.contains(Regex("vitamin|supplement|mineral|nutrient")) -> "supplements"
//            lowerMessage.contains(Regex("heart|blood pressure|cholesterol|cardiac")) -> "cardiovascular"
//            lowerMessage.contains(Regex("diabetes|blood sugar|insulin|glucose")) -> "diabetes"
//            else -> "general"
//        }
//    }
//
//    fun clearHistory() {
//        conversationHistory.clear()
//    }
//
//    data class ChatResponse(
//        val message: String,
//        val emotion: String = "neutral",
//        val category: String = "general",
//        val success: Boolean = true,
//        val error: String? = null
//    )
//}
//

package com.example.healthreminder.ui.chatbot


// User health context data class
data class UserHealthContext(
    val age: Int? = null,
    val gender: String? = null,
    val conditions: List<String> = listOf(),
    val medications: List<String> = listOf(),
    val recentSymptoms: List<String> = listOf(),
    val conversationHistory: List<String> = listOf()
)
package com.example.healthreminder.ui.chatbot

import com.google.firebase.Timestamp

data class ChatMessage(
    val id: String = "",
    val message: String = "",
    val isUser: Boolean = true,
    val timestamp: Timestamp = Timestamp.now(),
    val emotion: String? = null, // detected emotion: anxious, concerned, happy, etc.
    val category: String? = null, // health, exercise, diet, water, mental_health, etc.
    val isTyping: Boolean = false // for typing indicator
)


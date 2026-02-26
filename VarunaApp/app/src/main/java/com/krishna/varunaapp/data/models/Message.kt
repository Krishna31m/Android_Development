package com.krishna.varunaapp.data.models

data class Message(
    val id: String = "",
    val sender: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isBot: Boolean = false
)
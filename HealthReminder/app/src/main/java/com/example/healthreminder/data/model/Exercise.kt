package com.example.healthreminder.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId


data class Exercise(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "", // Gym, Yoga, Walk, Running
    val duration: Int = 0, // in minutes
    val time: String = "",
    val days: List<String> = listOf(),
    val isActive: Boolean = true,
    val reminderEnabled: Boolean = true,
    val createdAt: Timestamp = Timestamp.now()
)

package com.example.healthreminder.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class HealthChallenge(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val type: String = "", // Water, Walk, Sugar-free, etc.
    val name: String = "",
    val duration: Int = 0, // in days
    val startDate: Timestamp? = null,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isActive: Boolean = true,
    val completedDays: List<String> = listOf(),
    val createdAt: Timestamp = Timestamp.now()
)

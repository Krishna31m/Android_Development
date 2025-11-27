package com.example.healthreminder.data.model

import com.google.firebase.firestore.DocumentId

// Water Intake Model
data class WaterIntake(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val date: String = "", // Format: yyyy-MM-dd
    val goal: Int = 3000, // in ml
    val consumed: Int = 0,
    val logs: List<WaterLog> = listOf()
)
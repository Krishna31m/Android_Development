package com.example.healthreminder.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class HealthStats(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val date: String = "", // Format: yyyy-MM-dd
    val waterIntake: Int = 0,
    val waterGoal: Int = 3000,
    val medicinesTaken: Int = 0,
    val medicinesTotal: Int = 0,
    val exercisesCompleted: Int = 0,
    val exercisesTotal: Int = 0,
    val mealsCompleted: Int = 0,
    val mealsTotal: Int = 0,
    val steps: Int = 0,
    val sleepHours: Float = 0f,
    val mood: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
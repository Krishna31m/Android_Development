package com.example.healthreminder.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

// Diet/Meal Model
data class Meal(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val mealType: String = "", // Breakfast, Lunch, Dinner, Snack
    val time: String = "",
    val calories: Int = 0,
    val description: String = "",
    val reminderEnabled: Boolean = true,
    val createdAt: Timestamp = Timestamp.now()
)

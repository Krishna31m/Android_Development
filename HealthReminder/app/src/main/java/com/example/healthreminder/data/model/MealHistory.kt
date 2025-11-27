package com.example.healthreminder.data.model

import com.google.firebase.Timestamp

data class MealHistory(
    val mealId: String = "",
    val date: Timestamp = Timestamp.now(),
    val completed: Boolean = false,
    val actualCalories: Int = 0,
    val note: String = ""
)
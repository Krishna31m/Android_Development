package com.example.healthreminder.data.model

import com.google.firebase.Timestamp

data class ExerciseHistory(
    val exerciseId: String = "",
    val date: Timestamp = Timestamp.now(),
    val completed: Boolean = false,
    val duration: Int = 0,
    val note: String = ""
)
package com.example.healthreminder.data.model

import com.google.firebase.Timestamp

data class WaterLog(
    val amount: Int = 0, // in ml
    val timestamp: Timestamp = Timestamp.now()
)
package com.example.healthreminder.data.model

import com.google.firebase.Timestamp

data class MedicineHistory(
    val medicineId: String = "",
    val date: Timestamp = Timestamp.now(),
    val status: String = "", // Taken, Skipped, Missed
    val time: String = "",
    val note: String = ""
)
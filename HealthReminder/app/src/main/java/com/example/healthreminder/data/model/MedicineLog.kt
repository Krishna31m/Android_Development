package com.example.healthreminder.data.model

import com.google.firebase.Timestamp



data class MedicineLog(
    val id: String = "",
    val medicineId: String = "",
    val medicineName: String = "",
    val status: String = "", // "taken" or "skipped"
    val timestamp: Timestamp = Timestamp.now(),
    val userId: String = ""
)
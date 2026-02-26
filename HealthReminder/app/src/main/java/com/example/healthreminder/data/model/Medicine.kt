package com.example.healthreminder.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Medicine(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val dosage: String = "",
    val time: String = "",
    val frequency: String = "",
    val days: List<String> = emptyList(),
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val isActive: Boolean = true,  // Keep this as-is
    val reminderEnabled: Boolean = true,
    val createdAt: Timestamp = Timestamp.now()
)

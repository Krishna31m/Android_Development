package com.example.healthreminder.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

// Doctor Visit Model
data class DoctorVisit(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val date: Timestamp? = null,
    val time: String = "",
    val location: String = "",
    val reason: String = "",
    val prescriptionUrl: String = "",
    val reminderEnabled: Boolean = true,
    val completed: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)
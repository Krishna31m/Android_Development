package com.example.healthreminder.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class UserProfile(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val bloodGroup: String = "",
    val emergencyContact: String = "",
    val emergencyContactName: String = "",
    val allergies: String = "",
    val medicalConditions: String = "",
    val age: Int = 0,
    val weight: Float = 0f,
    val height: Float = 0f,
    val photoUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
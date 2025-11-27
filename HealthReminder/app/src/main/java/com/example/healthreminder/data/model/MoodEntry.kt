package com.example.healthreminder.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class MoodEntry(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val date: String = "", // Format: yyyy-MM-dd
    val mood: String = "", // Happy, Sad, Anxious, Calm, Energetic
    val emoji: String = "",
    val note: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
package com.krishna.varunaapp.data.models

data class ChatResponse(
    val message: String,
    val relatedDisease: Disease? = null,
    val showDoctorWarning: Boolean = false
)
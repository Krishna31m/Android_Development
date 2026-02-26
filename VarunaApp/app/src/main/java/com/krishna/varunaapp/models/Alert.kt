package com.krishna.varunaapp.models

data class Alert(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "info", // "info", "warning", "success", "urgent"
    val relatedPostId: String? = null, // Link to information post if applicable
    val createdAt: Long = 0,
    val createdBy: String = ""
)
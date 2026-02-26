package com.krishna.varunaapp.models

data class InformationPost(
    val id: String = "",
    val heading: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val createdAt: Long = 0,
    val createdBy: String = ""
)
package com.krishna.varunaapp.data.models

data class Disease(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val causes: List<String> = emptyList(),
    val symptoms: List<String> = emptyList(),
    val prevention: List<String> = emptyList(),
    val severity: String = "",
    val keywords: List<String> = emptyList()
)
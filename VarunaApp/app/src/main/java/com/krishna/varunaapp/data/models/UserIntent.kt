package com.krishna.varunaapp.data.models

enum class IntentType {
    DISEASE_QUERY,
    SYMPTOM_QUERY,
    PREVENTION_QUERY,
    WATER_SAFETY,
    HYGIENE,
    EMERGENCY,
    GREETING,
    UNKNOWN
}

data class UserIntent(
    val type: IntentType,
    val confidence: Float,
    val extractedSymptoms: List<String> = emptyList(),
    val extractedDiseases: List<String> = emptyList()
)
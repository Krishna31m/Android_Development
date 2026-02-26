package com.krishna.varunaapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diseases")
data class DiseaseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val causes: String,
    val symptoms: String,
    val prevention: String,
    val severity: String,
    val keywords: String
)
package com.example.events

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val profilePicture: String,  // URL or local path to profile picture
    val address: String,
    val bio: String
)

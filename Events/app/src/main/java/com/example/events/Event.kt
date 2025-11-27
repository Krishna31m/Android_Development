package com.example.events

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val artist: String,               // ← Added this line
    val dateTime: String,
    val venue: String,
    val price: String,
    val imageResId: Int
)
data class EventCategory(
    val categoryTitle: String,
    val events: List<Event>
)
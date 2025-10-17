package com.localify.android.data.models

data class Event(
    val id: String,
    val name: String,
    val imageUrl: String,
    val date: String,
    val venue: Venue,
    val artists: List<Artist>,
    val ticketUrl: String,
    val description: String
)

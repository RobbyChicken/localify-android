package com.localify.android.data.models

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String,
    val genres: List<String>,
    val bio: String,
    val spotifyId: String,
    val popularity: Int
)

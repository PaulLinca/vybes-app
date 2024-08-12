package com.example.vybes.post.model

import kotlinx.serialization.Serializable

@Serializable
data class VybeScreen(val id: Int)

data class Vybe(
    val id: Int,
    val songName: String,
    val spotifyTrackId: String,
    val spotifyArtistNames: List<String>,
    val spotifyArtistIds: List<String>,
    val spotifyAlbumId: String,
    val imageUrl: String,
    val postedDate: String,
    val vybesUser: String,
    val likes: List<Like>,
    val comments: List<Comment>
)

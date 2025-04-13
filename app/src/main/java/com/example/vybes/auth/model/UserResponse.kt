package com.example.vybes.auth.model

data class UserResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val favoriteArtists: Set<Artist>,
    val favoriteAlbums: Set<Artist>
)

data class Artist(
    val spotifyId: String,
    val name: String,
    val imageUrl: String
)

data class Album(
    val spotifyId: String,
    val name: String,
    val imageUrl: String,
    val artist: String
)
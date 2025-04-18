package com.example.vybes.auth.model

data class UserResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val favoriteArtists: List<Artist>,
    val favoriteAlbums: List<Artist>
)

data class Artist(
    val spotifyId: String,
    override val name: String,
    override val imageUrl: String,
) : MediaItem

data class Album(
    val spotifyId: String,
    val artist: String,
    override val name: String,
    override val imageUrl: String
) : MediaItem

interface MediaItem {
    val name: String
    val imageUrl: String
}
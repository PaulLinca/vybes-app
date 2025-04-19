package com.example.vybes.auth.model

data class UserResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val favoriteArtists: List<Artist>,
    val favoriteAlbums: List<Album>
)

data class Artist(
    override val spotifyId: String,
    override val name: String,
    override val imageUrl: String,
) : MediaItem

data class Album(
    override val spotifyId: String,
    val artist: String,
    override val name: String,
    override val imageUrl: String,
) : MediaItem

interface MediaItem {
    val spotifyId: String
    val name: String
    val imageUrl: String
    val isEmpty: Boolean
        get() = false
}
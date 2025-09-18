package com.example.vybes.network.response

import java.time.LocalDate

data class UserResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val favoriteArtists: List<Artist>,
    val favoriteAlbums: List<Album>,
    val profilePictureUrl: String? = null
)

data class Artist(
    override val spotifyId: String,
    override val name: String,
    override val imageUrl: String,
) : MediaItem

data class Album(
    override val spotifyId: String,
    val artists: List<Artist>,
    val tracks: List<Track>,
    override val name: String,
    override val imageUrl: String,
    val releaseDate: LocalDate?
) : MediaItem

data class Track(
    val spotifyId: String,
    val name: String,
    val imageUrl: String,
    val artists: List<Artist>,
)

interface MediaItem {
    val spotifyId: String?
    val name: String
    val imageUrl: String?
    val isEmpty: Boolean
        get() = false
}
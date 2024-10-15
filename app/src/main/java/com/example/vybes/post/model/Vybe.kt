package com.example.vybes.post.model

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class VybeScreen(val id: Long) {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<VybeScreen>()
    }
}

data class Vybe(
    val id: Long,
    val songName: String,
    val spotifyTrackId: String,
    val spotifyArtists: List<Artist>,
    val spotifyAlbumId: String,
    val imageUrl: String,
    val postedDate: ZonedDateTime,
    val user: User,
    val likes: List<Like> = mutableListOf(),
    val comments: List<Comment> = mutableListOf(),
)

data class Artist(
    val spotifyId: String,
    val name: String
)

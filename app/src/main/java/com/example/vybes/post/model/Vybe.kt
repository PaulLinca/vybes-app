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
    override val id: Long,
    val songName: String,
    override val spotifyId: String,
    val spotifyArtists: List<Artist>,
    val spotifyAlbumId: String,
    val imageUrl: String,
    val description: String?,
    override val user: User,
    override val postedDate: ZonedDateTime,
    override val likes: List<Like>? = mutableListOf(),
    override val comments: List<Comment>? = mutableListOf(),
    override val type: String
) : Post

data class Artist(
    val spotifyId: String,
    val name: String
)

package com.example.vybes.model

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.vybes.network.request.TrackRating
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.ZonedDateTime

@Serializable
data class AlbumReviewScreen(val id: Long) {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<AlbumReviewScreen>()
    }
}

data class AlbumReview(
    override val id: Long,
    val albumName: String,
    override val spotifyId: String,
    val score: Int?,
    val imageUrl: String?,
    val description: String?,
    val artists: List<Artist> = emptyList(),
    override val user: User,
    override val postedDate: ZonedDateTime,
    val releaseDate: LocalDate,
    val trackReviews: List<TrackReviewDTO> = emptyList(),
    override val likes: List<Like>? = emptyList(),
    override val comments: List<Comment>? = emptyList(),
    override val type: String
) : Post

data class TrackReviewDTO(
    val id: Long?,
    val name: String,
    val spotifyTrackId: String,
    val rating: TrackRating?,
    val isFavorite: Boolean
)
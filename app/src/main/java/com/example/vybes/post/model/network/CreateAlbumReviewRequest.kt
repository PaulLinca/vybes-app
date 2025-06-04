package com.example.vybes.post.model.network

import androidx.compose.ui.graphics.Color
import com.example.vybes.common.theme.TryoutBlue
import com.example.vybes.common.theme.TryoutGreen
import com.example.vybes.common.theme.TryoutOrange
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.TryoutYellow
import kotlinx.serialization.Serializable

@Serializable
data class CreateAlbumReviewRequest(
    val spotifyAlbumId: String,
    val score: Int,
    val description: String,
    val trackReviews: List<TrackReview>
)

@Serializable
data class TrackReview(
    val id: Long? = null,
    val name: String,
    val spotifyTrackId: String,
    val rating: TrackRating?,
    val isFavorite: Boolean
)

enum class TrackRating(val displayName: String, val color: Color) {
    AWFUL("Awful", TryoutRed),
    MEH("Meh", TryoutOrange),
    OKAY("Okay", TryoutYellow),
    GREAT("Great", TryoutBlue),
    AMAZING("Amazing", TryoutGreen)
}
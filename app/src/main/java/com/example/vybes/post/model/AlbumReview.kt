package com.example.vybes.post.model

import com.example.vybes.post.model.network.TrackRating
import java.time.LocalDate
import java.time.ZonedDateTime

data class AlbumReview(
    override val id: Long,
    val albumName: String,
    override val spotifyId: String,
    val spotifyAlbumId: String,
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
    val rating: TrackRating,
    val isFavorite: Boolean
)
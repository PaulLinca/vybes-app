package com.example.vybes.post.model

import com.example.vybes.post.model.network.TrackRating
import java.time.ZonedDateTime

data class AlbumReview(
    override val id: Long,
    val albumName: String,
    val spotifyAlbumId: String,
    val score: Int?,
    val imageUrl: String?,
    val description: String?,
    val artists: List<Artist>,
    override val user: User,
    override val postedDate: ZonedDateTime,
    val trackReviews: List<TrackReviewDTO>,
    override val likes: List<Like> = mutableListOf(),
    override val comments: List<Comment>? = mutableListOf(),
) : Post


data class TrackReviewDTO(
    val id: Long?,
    val name: String,
    val spotifyTrackId: String,
    val rating: TrackRating,
    val isFavorite: Boolean
)
package com.example.vybes.model

import java.time.ZonedDateTime

sealed interface Post {
    val id: Long
    val user: User
    val postedDate: ZonedDateTime
    val spotifyId: String
    val likes: List<Like>?
    val comments: List<Comment>?
    val type: String
}

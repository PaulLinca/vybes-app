package com.example.vybes.post.model

import java.time.ZonedDateTime

sealed interface Post {
    val id: Long
    val user: User
    val postedDate: ZonedDateTime
    val likes: List<Like>
    val comments: List<Comment>?
}

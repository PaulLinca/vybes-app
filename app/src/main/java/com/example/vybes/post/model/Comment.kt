package com.example.vybes.post.model

import java.time.ZonedDateTime

data class Comment(
    val id: Long,
    val text: String,
    val user: User,
    val likeIds: List<Long> = mutableListOf(),
    val timestamp: ZonedDateTime,
)
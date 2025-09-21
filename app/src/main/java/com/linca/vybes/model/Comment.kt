package com.linca.vybes.model

import java.time.ZonedDateTime

data class Comment(
    val id: Long,
    val text: String,
    val user: User,
    val likes: List<Like>? = mutableListOf(),
    val postedDate: ZonedDateTime,
)
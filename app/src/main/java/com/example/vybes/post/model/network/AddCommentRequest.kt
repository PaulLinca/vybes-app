package com.example.vybes.post.model.network

import java.time.ZonedDateTime

data class AddCommentRequest(
    val text: String,
    val timestamp: ZonedDateTime
)

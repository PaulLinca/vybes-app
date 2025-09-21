package com.linca.vybes.network.request

import java.time.ZonedDateTime

data class AddCommentRequest(
    val text: String,
    val timestamp: ZonedDateTime
)

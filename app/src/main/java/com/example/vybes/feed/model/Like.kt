package com.example.vybes.feed.model

import kotlinx.serialization.Serializable

@Serializable
data class Like(
    val user: User
)

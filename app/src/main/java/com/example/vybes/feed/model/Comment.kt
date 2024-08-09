package com.example.vybes.feed.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val text: String,
    val likes: List<Like>
)
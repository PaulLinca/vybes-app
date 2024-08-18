package com.example.vybes.post.model

data class Comment(
    val id: Long,
    val text: String,
    val user: User,
    val likes: List<Like> = mutableListOf(),
    val postedDate: String,
)
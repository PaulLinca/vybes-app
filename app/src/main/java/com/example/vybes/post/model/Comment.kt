package com.example.vybes.post.model

data class Comment(
    val id: Int,
    val text: String,
    val user: User,
    val likes: List<Like> = mutableListOf(),
    val postedDate: String,
)
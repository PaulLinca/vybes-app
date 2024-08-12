package com.example.vybes.post.model

data class Comment(
    val text: String,
    val user: User,
    val likes: List<Like>,
    val postedDate: String,
)
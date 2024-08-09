package com.example.vybes.feedback.model

import com.example.vybes.feed.model.User

data class FeedbackEntry(
    val text: String,
    val user: User
)
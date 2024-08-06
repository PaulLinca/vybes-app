package com.example.vybes.ui.feedback.model

import com.example.vybes.ui.feed.model.User

data class FeedbackEntry(
    val text: String,
    val user: User
)
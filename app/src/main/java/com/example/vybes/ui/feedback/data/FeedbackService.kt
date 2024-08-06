package com.example.vybes.ui.feedback.data

import com.example.vybes.ui.feedback.model.FeedbackEntry

interface FeedbackService {
    suspend fun submit(feedbackText: String): String
}
package com.example.vybes.feedback.service

interface FeedbackService {
    suspend fun submit(feedbackText: String)
}
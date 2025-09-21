package com.linca.vybes.feedback.service

import retrofit2.Response

interface FeedbackService {
    suspend fun submit(feedbackText: String): Response<Void>
}
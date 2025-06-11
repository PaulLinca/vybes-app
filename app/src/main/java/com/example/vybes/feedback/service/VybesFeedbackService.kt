package com.example.vybes.feedback.service

import com.example.vybes.feedback.model.FeedbackRequest
import com.example.vybes.network.VybesApiClient
import retrofit2.Response
import javax.inject.Inject

class VybesFeedbackService @Inject constructor(
    private val vybesApiClient: VybesApiClient
) : FeedbackService {
    override suspend fun submit(feedbackText: String): Response<Void> {
        return vybesApiClient.submitFeedback(FeedbackRequest(feedbackText))
    }
}
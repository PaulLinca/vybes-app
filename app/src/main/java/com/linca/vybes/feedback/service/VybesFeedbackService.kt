package com.linca.vybes.feedback.service

import com.linca.vybes.network.VybesApiClient
import com.linca.vybes.network.request.FeedbackRequest
import retrofit2.Response
import javax.inject.Inject

class VybesFeedbackService @Inject constructor(
    private val vybesApiClient: VybesApiClient
) : FeedbackService {
    override suspend fun submit(feedbackText: String): Response<Void> {
        return vybesApiClient.submitFeedback(FeedbackRequest(feedbackText))
    }
}
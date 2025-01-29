package com.example.vybes.feedback.service

import android.util.Log

class DummyFeedbackService : FeedbackService {
    override suspend fun submit(feedbackText: String) {
        Log.e("DDDDD", "DUMMY SERVICE CALLED $feedbackText")
    }
}
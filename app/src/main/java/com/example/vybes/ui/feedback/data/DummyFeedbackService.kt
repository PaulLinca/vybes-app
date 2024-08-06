package com.example.vybes.ui.feedback.data

import android.util.Log

class DummyFeedbackService : FeedbackService {
    override suspend fun submit(feedbackText: String){
        Log.e("DDDDD", "DUMMY SERVICE CALLED")
    }
}
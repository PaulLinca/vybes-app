package com.example.vybes.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.ui.feedback.data.FeedbackService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackService: FeedbackService
) : ViewModel() {

    fun submitFeedback(feedbackText: String) {
        viewModelScope.launch { feedbackService.submit(feedbackText) }
    }
}
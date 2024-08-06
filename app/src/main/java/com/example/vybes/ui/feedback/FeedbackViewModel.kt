package com.example.vybes.ui.feedback

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    private var _text: String by mutableStateOf("")
    val text: String
        get() = _text

    fun updateText(updatedText: String) {
        _text = updatedText
    }

    fun submitFeedback() {
        viewModelScope.launch { feedbackService.submit(text) }
    }
}
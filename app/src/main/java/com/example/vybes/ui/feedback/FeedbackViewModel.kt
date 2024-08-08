package com.example.vybes.ui.feedback

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.ui.feedback.data.FeedbackService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackService: FeedbackService
) : ViewModel() {

    private val minFeedbackLength = 10

    private var _text: String by mutableStateOf("")
    val text: String
        get() = _text

    fun updateText(updatedText: String) {
        _text = updatedText
    }

    private val _alertText = MutableStateFlow("")
    val alertText = _alertText.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted = _isSubmitted.asStateFlow()

    private val _isTextInvalid = MutableStateFlow(false)
    val isTextInvalid = _isTextInvalid.asStateFlow()

    fun submitFeedback() {
        if (isTextValid()) {
            viewModelScope.launch {
                feedbackService.submit(text)
            }
            setSuccess()
        } else {
            _isTextInvalid.value = true
            _alertText.value = "Input too short"
        }
    }

    private fun setSuccess() {
        _alertText.value = "Feedback successfully submitted!"
        _isSubmitted.value = true
    }

    fun resetTextValidity() {
        _isTextInvalid.value = false
    }

    private fun isTextValid(): Boolean {
        if (text.isBlank()) {
            return false
        }
        if (text.length < minFeedbackLength) {
            return false
        }
        return true
    }
}
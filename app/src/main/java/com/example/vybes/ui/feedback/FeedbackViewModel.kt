package com.example.vybes.ui.feedback

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val initialInfoText = "We value your input! Please use the text field below to share any feedback, suggest new features, or report bugs. Your comments help us improve the app and make it better for you. Thank you for your support!"
    private val minFeedbackLength = 10

    private var _text: String by mutableStateOf("")
    val text: String
        get() = _text

    fun updateText(updatedText: String) {
        _text = updatedText
    }

    private val _infoText = MutableStateFlow(initialInfoText)
    val infoText = _infoText.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted = _isSubmitted.asStateFlow()

    fun submitFeedback() {
        if(isTextValid()) {
            viewModelScope.launch {
                feedbackService.submit(text)
            }
            setSuccess()
        } else {

        }
    }

    fun setSuccess() {
        _infoText.value = "Feedback successfully submitted!"
        _isSubmitted.value = true
    }

    private fun isTextValid(): Boolean {
        if (text.isBlank()) {
            return false
        }
        if(text.length < minFeedbackLength) {
            return false
        }
        return true
    }
}
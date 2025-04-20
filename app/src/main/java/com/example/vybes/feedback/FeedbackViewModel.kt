package com.example.vybes.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.feedback.service.FeedbackService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackService: FeedbackService
) : ViewModel() {

    private val minFeedbackLength = 10
    private val maxFeedbackLength = 500

    data class FeedbackUiState(
        val text: String = "",
        val isLoading: Boolean = false,
        val isSubmitted: Boolean = false,
        val isTextInvalid: Boolean = false,
        val alertText: String = "",
        val charactersRemaining: Int = 500
    )

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState = _uiState.asStateFlow()

    fun updateText(updatedText: String) {
        if (updatedText.length <= maxFeedbackLength) {
            _uiState.update { currentState ->
                currentState.copy(
                    text = updatedText,
                    isTextInvalid = false,
                    charactersRemaining = maxFeedbackLength - updatedText.length
                )
            }
        }
    }

    fun submitFeedback() {
        val currentText = _uiState.value.text

        if (!isTextValid(currentText)) {
            _uiState.update {
                it.copy(
                    isTextInvalid = true,
                    alertText = "Please enter at least $minFeedbackLength characters"
                )
            }
            return
        }

        // Set loading state
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                feedbackService.submit(currentText)
                setSuccess()
            } catch (e: Exception) {
                setError("Failed to submit feedback. Please try again.")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resetForm() {
        _uiState.value = FeedbackUiState()
    }

    private fun setSuccess() {
        _uiState.update {
            it.copy(
                isSubmitted = true,
                alertText = "Feedback successfully submitted!"
            )
        }
    }

    private fun setError(message: String) {
        _uiState.update {
            it.copy(
                isTextInvalid = true,
                alertText = message
            )
        }
    }

    private fun isTextValid(text: String): Boolean {
        return text.isNotBlank() && text.length >= minFeedbackLength
    }
}
package com.linca.vybes.add.vybe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linca.vybes.post.service.PostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val postService: PostService
) : ViewModel() {
    private val maxDescriptionLength = 100

    private val _navigationEvent = MutableSharedFlow<Boolean>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private var _remainingCharacters: Int by mutableStateOf(maxDescriptionLength)
    val remainingCharacters: Int get() = _remainingCharacters

    fun onDescriptionChange(newDescription: String) {
        if (newDescription.length <= maxDescriptionLength) {
            _description.value = newDescription
            _remainingCharacters = maxDescriptionLength - newDescription.length
        }
    }

    fun submit(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                postService.postVybe(id, _description.value)
                _navigationEvent.emit(true)
            } catch (e: Exception) {
                // Handle error if needed
                // You might want to show an error message to the user
            } finally {
                _isLoading.value = false
            }
        }
    }
}
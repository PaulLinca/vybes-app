package com.example.vybes.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.service.PostService
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
    private val _navigationEvent = MutableSharedFlow<Boolean>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun submit(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                postService.postVybe(id)
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
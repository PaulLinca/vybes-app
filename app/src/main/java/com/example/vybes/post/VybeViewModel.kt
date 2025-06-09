package com.example.vybes.post

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.Like
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.post.service.PostService
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class VybeViewModel @Inject constructor(
    private val postService: PostService,
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {
    private val args = VybeScreen.from(savedStateHandle)
    private val currentUserId: Long get() = SharedPreferencesManager.getUserId()

    sealed class VybeUiState {
        object Loading : VybeUiState()
        data class LoadingCall(val vybe: Vybe, val isLikedByCurrentUser: Boolean) : VybeUiState()
        data class Success(val vybe: Vybe, val isLikedByCurrentUser: Boolean) : VybeUiState()
        data class Error(val message: String) : VybeUiState()
    }

    private val _vybe = MutableStateFlow<Vybe?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLikedByCurrentUser = MutableStateFlow(false)
    private var _commentText: String by mutableStateOf("")

    val commentText: String get() = _commentText
    val isLikedByCurrentUser = _isLikedByCurrentUser.asStateFlow()
    val vybe = _vybe.asStateFlow()

    val uiState = combine(
        _vybe,
        _isLikedByCurrentUser,
        _isLoading,
        _errorMessage
    ) { vybe, isLiked, isLoading, error ->
        when {
            vybe != null && isLoading -> VybeUiState.LoadingCall(vybe, isLiked)
            isLoading -> VybeUiState.Loading
            error != null -> VybeUiState.Error(error)
            vybe != null -> VybeUiState.Success(vybe, isLiked)
            else -> VybeUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VybeUiState.Loading)

    init {
        loadVybe()
    }

    fun refreshVybe() {
        loadVybe()
    }

    fun updateText(updatedText: String) {
        _commentText = updatedText
    }

    fun clearText() {
        _commentText = ""
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun likeVybe() {
        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.likePost(args.id)
            }.onSuccess { like ->
                _vybe.value?.let { v ->
                    _vybe.value = v.copy(likes = v.likes.orEmpty() + Like(like.userId))
                    _isLikedByCurrentUser.value = true
                }
            }.onFailure { error ->
                _errorMessage.value = "Failed to like: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    fun unlikeVybe() {
        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.unlikePost(args.id)
            }.onSuccess { removedLike ->
                _vybe.value?.let { v ->
                    _vybe.value =
                        v.copy(likes = v.likes.orEmpty().filterNot { it.userId == removedLike.userId })
                    _isLikedByCurrentUser.value = false
                }
            }.onFailure { error ->
                _errorMessage.value = "Failed to unlike: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    fun likeComment(commentId: Long) {
        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.likeComment(args.id, commentId)
            }.onSuccess { newLike ->
                _vybe.value?.let { v ->
                    val updatedComments = v.comments.orEmpty().map { comment ->
                        if (comment.id == commentId) {
                            comment.copy(
                                likes = comment.likes.orEmpty() + Like(newLike.userId)
                            )
                        } else {
                            comment
                        }
                    }
                    _vybe.value = v.copy(comments = updatedComments)
                }
            }.onFailure { error ->
                _errorMessage.value = "Failed to like comment: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    fun unlikeComment(commentId: Long) {
        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.unlikeComment(args.id, commentId)
            }.onSuccess { response ->
                _vybe.value?.let { v ->
                    val updatedComments = v.comments.orEmpty().map { comment ->
                        if (comment.id == commentId) {
                            comment.copy(
                                likes = comment.likes.orEmpty()
                                    .filterNot { it.userId == currentUserId }
                            )
                        } else {
                            comment
                        }
                    }
                    _vybe.value = v.copy(comments = updatedComments)
                }
            }.onFailure { error ->
                _errorMessage.value = "Failed to unlike comment: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    fun addComment() {
        val trimmedComment = commentText.trim()
        if (trimmedComment.isEmpty()) return

        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.addComment(args.id, trimmedComment)
            }.onSuccess { comment ->
                _vybe.value?.let { v ->
                    _vybe.value = v.copy(
                        comments = v.comments.orEmpty() + comment
                    )
                }
                clearText()
            }.onFailure { error ->
                _errorMessage.value = "Failed to add comment: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    private fun loadVybe() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            safeApiCall { postService.getVybe(args.id) }.onSuccess { v ->
                _isLikedByCurrentUser.value = v.likes.orEmpty().any { it.userId == currentUserId }
                _vybe.value = v
            }.onFailure { error ->
                _errorMessage.value = "Failed to load vybe: ${error.localizedMessage}"
            }

            _isLoading.value = false
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                Result.success(response.body()) as Result<T>
            } else {
                Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
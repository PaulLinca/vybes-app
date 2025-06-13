package com.example.vybes.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.AlbumReview
import com.example.vybes.post.model.Comment
import com.example.vybes.post.model.Like
import com.example.vybes.post.model.Post
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.service.PostService
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.math.max

abstract class PostViewModel<T : Post>(
    protected open val postService: PostService
) : ViewModel() {
    private val maxCommentLength = 500

    sealed class PostUiState<T : Post> {
        class Loading<T : Post> : PostUiState<T>()
        data class LoadingCall<T : Post>(val post: T, val isLikedByCurrentUser: Boolean) :
            PostUiState<T>()

        data class Success<T : Post>(val post: T, val isLikedByCurrentUser: Boolean) :
            PostUiState<T>()

        data class Error<T : Post>(val message: String) : PostUiState<T>()
    }

    val currentUserId: Long get() = SharedPreferencesManager.getUserId()

    val _post = MutableStateFlow<T?>(null)
    val _isLoading = MutableStateFlow(true)
    val _errorMessage = MutableStateFlow<String?>(null)
    val _isLikedByCurrentUser = MutableStateFlow(false)
    private var _commentText: String by mutableStateOf("")
    private var _remainingCharacters: Int by mutableStateOf(maxCommentLength)

    val commentText: String get() = _commentText
    val post = _post.asStateFlow()
    val remainingCharacters: Int get() = _remainingCharacters

    val uiState = combine(
        _post,
        _isLikedByCurrentUser,
        _isLoading,
        _errorMessage
    ) { post, isLiked, isLoading, error ->
        when {
            post != null && isLoading -> PostUiState.LoadingCall(post, isLiked)
            isLoading -> PostUiState.Loading()
            error != null -> PostUiState.Error(error)
            post != null -> PostUiState.Success(post, isLiked)
            else -> PostUiState.Loading()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PostUiState.Loading())

    fun updateText(updatedText: String) {
        if (updatedText.length <= maxCommentLength) {
            _commentText = updatedText
            _remainingCharacters = maxCommentLength - updatedText.length
        }
    }

    fun clearText() {
        _commentText = ""
        _remainingCharacters = 0
    }

    fun likePost(postId: Long) {
        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.likePost(postId)
            }.onSuccess { like ->
                _post.value?.let { p ->
                    @Suppress("UNCHECKED_CAST")
                    _post.value = updatePostLikes(p, p.likes.orEmpty() + Like(like.userId)) as T
                    _isLikedByCurrentUser.value = true
                }
            }.onFailure { error ->
                _errorMessage.value = "Failed to like: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    fun unlikePost(postId: Long) {
        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.unlikePost(postId)
            }.onSuccess { removedLike ->
                _post.value?.let { p ->
                    @Suppress("UNCHECKED_CAST")
                    _post.value = updatePostLikes(
                        p,
                        p.likes.orEmpty().filterNot { it.userId == removedLike.userId }) as T
                    _isLikedByCurrentUser.value = false
                }
            }.onFailure { error ->
                _errorMessage.value = "Failed to unlike: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    fun likeComment(postId: Long, commentId: Long) {
        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.likeComment(postId, commentId)
            }.onSuccess { newLike ->
                _post.value?.let { p ->
                    val updatedComments = p.comments.orEmpty().map { comment ->
                        if (comment.id == commentId) {
                            comment.copy(likes = comment.likes.orEmpty() + Like(newLike.userId))
                        } else {
                            comment
                        }
                    }
                    @Suppress("UNCHECKED_CAST")
                    _post.value = updatePostComments(p, updatedComments) as T
                }
            }.onFailure { error ->
                _errorMessage.value = "Failed to like comment: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    fun unlikeComment(postId: Long, commentId: Long) {
        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.unlikeComment(postId, commentId)
            }.onSuccess { response ->
                _post.value?.let { p ->
                    val updatedComments = p.comments.orEmpty().map { comment ->
                        if (comment.id == commentId) {
                            comment.copy(
                                likes = comment.likes.orEmpty()
                                    .filterNot { it.userId == currentUserId }
                            )
                        } else {
                            comment
                        }
                    }
                    @Suppress("UNCHECKED_CAST")
                    _post.value = updatePostComments(p, updatedComments) as T
                }
            }.onFailure { error ->
                _errorMessage.value = "Failed to unlike comment: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    fun addComment(postId: Long) {
        val trimmedComment = commentText.trim()
        if (trimmedComment.isEmpty()) return

        viewModelScope.launch {
            safeApiCall {
                _isLoading.value = true
                postService.addComment(postId, trimmedComment)
            }.onSuccess { comment ->
                _post.value?.let { p ->
                    @Suppress("UNCHECKED_CAST")
                    _post.value = updatePostComments(p, p.comments.orEmpty() + comment) as T
                }
                clearText()
            }.onFailure { error ->
                _errorMessage.value = "Failed to add comment: ${error.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    protected open fun loadPost(postId: Long) {}

    private fun updatePostLikes(post: Post, likes: List<Like>): Post {
        return when (post) {
            is Vybe -> post.copy(likes = likes)
            is AlbumReview -> post.copy(likes = likes)
        }
    }

    private fun updatePostComments(post: Post, comments: List<Comment>): Post {
        return when (post) {
            is Vybe -> post.copy(comments = comments)
            is AlbumReview -> post.copy(comments = comments)
        }
    }

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
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

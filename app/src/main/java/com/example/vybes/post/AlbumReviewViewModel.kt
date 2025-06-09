package com.example.vybes.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.AlbumReview
import com.example.vybes.post.model.AlbumReviewScreen
import com.example.vybes.post.service.PostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumReviewViewModel @Inject constructor(
    override val postService: PostService,
    savedStateHandle: SavedStateHandle
) : PostViewModel<AlbumReview>(postService) {

    private val args = AlbumReviewScreen.from(savedStateHandle)

    init {
        loadAlbumReview()
    }

    fun refreshAlbumReview() = loadAlbumReview()
    fun likeAlbumReview() = likePost(args.id)
    fun unlikeAlbumReview() = unlikePost(args.id)
    fun likeComment(commentId: Long) = likeComment(args.id, commentId)
    fun unlikeComment(commentId: Long) = unlikeComment(args.id, commentId)
    fun addComment() = addComment(args.id)

    private fun loadAlbumReview() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            safeApiCall { postService.getAlbumReview(args.id) }.onSuccess { review ->
                _isLikedByCurrentUser.value =
                    review.likes.orEmpty().any { it.userId == currentUserId }
                _post.value = review
            }.onFailure { error ->
                _errorMessage.value = "Failed to load album review: ${error.localizedMessage}"
            }

            _isLoading.value = false
        }
    }
}


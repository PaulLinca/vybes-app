package com.linca.vybes.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.linca.vybes.model.AlbumReview
import com.linca.vybes.model.AlbumReviewScreen
import com.linca.vybes.post.service.PostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumReviewViewModel @Inject constructor(
    override val postService: PostService,
    postsRepository: PostsRepository,
    savedStateHandle: SavedStateHandle
) : PostViewModel<AlbumReview>(postService, postsRepository) {

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

            val cachedPost = postsRepository.getPost(args.id) as? AlbumReview
            if (cachedPost != null) {
                _isLikedByCurrentUser.value =
                    cachedPost.likes.orEmpty().any { it.userId == currentUserId }
                _post.value = cachedPost
            }

            safeApiCall { postService.getAlbumReview(args.id) }.onSuccess { review ->
                _isLikedByCurrentUser.value =
                    review.likes.orEmpty().any { it.userId == currentUserId }
                _post.value = review

                postsRepository.updatePostInCache(review)
            }.onFailure { error ->
                if (cachedPost == null) {
                    _errorMessage.value = "Failed to load album review: ${error.localizedMessage}"
                }
            }

            _isLoading.value = false
        }
    }
}
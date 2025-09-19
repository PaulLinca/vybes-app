package com.example.vybes.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.vybes.model.Vybe
import com.example.vybes.model.VybeScreen
import com.example.vybes.post.service.PostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VybeViewModel @Inject constructor(
    override val postService: PostService,
    postsRepository: PostsRepository,
    savedStateHandle: SavedStateHandle
) : PostViewModel<Vybe>(postService, postsRepository) {

    private val args = VybeScreen.from(savedStateHandle)

    init {
        loadVybe()
    }

    fun refreshVybe() = loadVybe()
    fun likeVybe() = likePost(args.id)
    fun unlikeVybe() = unlikePost(args.id)
    fun likeComment(commentId: Long) = likeComment(args.id, commentId)
    fun unlikeComment(commentId: Long) = unlikeComment(args.id, commentId)
    fun addComment() = addComment(args.id)

    private fun loadVybe() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val cachedPost = postsRepository.getPost(args.id) as? Vybe
            if (cachedPost != null) {
                _isLikedByCurrentUser.value =
                    cachedPost.likes.orEmpty().any { it.userId == currentUserId }
                _post.value = cachedPost
            }

            safeApiCall { postService.getVybe(args.id) }.onSuccess { vybe ->
                _isLikedByCurrentUser.value =
                    vybe.likes.orEmpty().any { it.userId == currentUserId }
                _post.value = vybe

                postsRepository.updatePostInCache(vybe)
            }.onFailure { error ->
                if (cachedPost == null) {
                    _errorMessage.value = "Failed to load vybe: ${error.localizedMessage}"
                }
            }

            _isLoading.value = false
        }
    }
}
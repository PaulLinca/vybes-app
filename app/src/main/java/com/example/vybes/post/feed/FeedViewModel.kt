package com.example.vybes.post.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.AlbumReview
import com.example.vybes.post.model.Like
import com.example.vybes.post.model.Post
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.service.PostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postService: PostService
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private val _hasMoreContent = MutableStateFlow(true)
    val hasMoreContent = _hasMoreContent.asStateFlow()

    private var currentPage = 0
    private val pageSize = 10
    private var totalPages = Int.MAX_VALUE

    init {
        loadInitialPosts()
    }

    private fun loadInitialPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            currentPage = 0

            try {
                val response = postService.getPostsPaginated(page = 0, size = pageSize)
                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    _posts.value = pageResponse.content
                    totalPages = pageResponse.totalPages
                    _hasMoreContent.value = !pageResponse.last
                } else {
                    _errorState.value = "Failed to load posts: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorState.value = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorState.value = null
            currentPage = 0

            try {
                val response = postService.getPostsPaginated(page = 0, size = pageSize)
                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    _posts.value = pageResponse.content
                    totalPages = pageResponse.totalPages
                    _hasMoreContent.value = !pageResponse.last
                } else {
                    _errorState.value = "Failed to refresh: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorState.value = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadMorePosts() {
        if (_isLoadingMore.value || !_hasMoreContent.value || currentPage >= totalPages - 1) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                val nextPage = currentPage + 1
                val response = postService.getPostsPaginated(page = nextPage, size = pageSize)

                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    val newPosts = pageResponse.content

                    if (newPosts.isNotEmpty()) {
                        _posts.value += newPosts
                        currentPage = nextPage
                        _hasMoreContent.value = !pageResponse.last
                    } else {
                        _hasMoreContent.value = false
                    }
                } else {
                    _errorState.value = "Failed to load more posts"
                }
            } catch (e: Exception) {
                _errorState.value = "Network error while loading more posts"
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    fun clickLikeButton(postId: Long, isLikedByCurrentUser: Boolean) {
        if (isLikedByCurrentUser) {
            unlikePost(postId)
        } else {
            likePost(postId)
        }
    }

    private fun likePost(postId: Long) {
        viewModelScope.launch {
            try {
                val response = postService.likePost(postId)
                if (response.isSuccessful && response.body() != null) {
                    updatePostLikes(postId) { currentLikes ->
                        currentLikes + Like(response.body()!!.userId)
                    }
                } else {
                    _errorState.value = "Failed to like post"
                }
            } catch (e: Exception) {
                _errorState.value = "Network error while liking post"
            }
        }
    }

    private fun unlikePost(postId: Long) {
        viewModelScope.launch {
            try {
                val response = postService.unlikePost(postId)
                if (response.isSuccessful && response.body() != null) {
                    updatePostLikes(postId) { currentLikes ->
                        currentLikes.filter { it.userId != response.body()!!.userId }
                    }
                } else {
                    _errorState.value = "Failed to unlike post"
                }
            } catch (e: Exception) {
                _errorState.value = "Network error while unliking post"
            }
        }
    }

    private fun updatePostLikes(postId: Long, update: (List<Like>) -> List<Like>) {
        _posts.value = _posts.value.map { post ->
            if (post is Vybe && post.id == postId) {
                post.copy(likes = update(post.likes.orEmpty()))
            } else if (post is AlbumReview && post.id == postId) {
                post.copy(likes = update(post.likes.orEmpty()))
            } else {
                post
            }
        }
    }

    fun clearError() {
        _errorState.value = null
    }
}
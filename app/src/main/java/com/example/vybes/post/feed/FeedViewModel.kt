package com.example.vybes.post.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.common.posts.PostFilter
import com.example.vybes.common.posts.PostsManager
import com.example.vybes.post.model.Like
import com.example.vybes.post.service.PostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postService: PostService
) : ViewModel() {

    private val postsManager = PostsManager()

    val posts = postsManager.filteredPosts
    val selectedPostFilter = postsManager.selectedPostFilter
    val isLoading = postsManager.isLoadingPosts
    val isLoadingMore = postsManager.isLoadingMorePosts
    val hasMoreContent = postsManager.hasMorePosts
    val postsError = postsManager.postsError

    private val _likeLoadingStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val likeLoadingStates = _likeLoadingStates.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        loadInitialPosts()
    }

    private fun loadInitialPosts() {
        viewModelScope.launch {
            postsManager.setLoadingState(true)
            postsManager.setError(null)

            try {
                val response = postService.getPostsPaginated(
                    page = 0,
                    size = postsManager.getPageSize()
                )
                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    postsManager.setInitialPosts(
                        posts = pageResponse.content,
                        totalPages = pageResponse.totalPages,
                        isLastPage = pageResponse.last
                    )
                } else {
                    postsManager.setError("Failed to load posts: ${response.message()}")
                }
            } catch (e: Exception) {
                postsManager.setError("Network error: ${e.localizedMessage ?: "Unknown error"}")
            } finally {
                postsManager.setLoadingState(false)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            postsManager.setError(null)

            try {
                val response = postService.getPostsPaginated(
                    page = 0,
                    size = postsManager.getPageSize()
                )
                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    postsManager.setInitialPosts(
                        posts = pageResponse.content,
                        totalPages = pageResponse.totalPages,
                        isLastPage = pageResponse.last
                    )
                } else {
                    postsManager.setError("Failed to refresh: ${response.message()}")
                }
            } catch (e: Exception) {
                postsManager.setError("Network error: ${e.localizedMessage ?: "Unknown error"}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadMorePosts() {
        if (!postsManager.canLoadMore()) return

        viewModelScope.launch {
            postsManager.setLoadingMoreState(true)

            try {
                val nextPage = postsManager.getCurrentPage() + 1
                val response = postService.getPostsPaginated(
                    page = nextPage,
                    size = postsManager.getPageSize()
                )

                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    postsManager.addMorePosts(
                        newPosts = pageResponse.content,
                        isLastPage = pageResponse.last
                    )
                } else {
                    postsManager.setError("Failed to load more posts")
                }
            } catch (e: Exception) {
                postsManager.setError("Network error while loading more posts")
            } finally {
                postsManager.setLoadingMoreState(false)
            }
        }
    }

    fun setPostFilter(filter: PostFilter) {
        postsManager.setPostFilter(filter)
    }

    fun clickLikeButton(postId: Long, isLikedByCurrentUser: Boolean) {
        _likeLoadingStates.value += (postId to true)

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
                    postsManager.setError("Failed to like post")
                }
            } catch (e: Exception) {
                postsManager.setError("Network error while liking post")
            } finally {
                _likeLoadingStates.value -= postId
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
                    postsManager.setError("Failed to unlike post")
                }
            } catch (e: Exception) {
                postsManager.setError("Network error while unliking post")
            } finally {
                _likeLoadingStates.value -= postId
            }
        }
    }

    fun deletePost(postId: Long) {
        postsManager.setLoadingState(true)
        postsManager.setError(null)

        viewModelScope.launch {
            try {
                val response = postService.deletePost(postId)
                if (response.isSuccessful && response.body() != null) {
                    deletePost(postId)
                } else {
                    postsManager.setError("Failed to delete post")
                }
            } catch (e: Exception) {
                postsManager.setError("Network error while deleting post")
            } finally {
                postsManager.setLoadingState(false)
            }
        }
    }

    private fun updatePostLikes(postId: Long, update: (List<Like>) -> List<Like>) {
        postsManager.updatePostLikes(postId, update)
    }

    fun clearError() {
        postsManager.setError(null)
    }
}
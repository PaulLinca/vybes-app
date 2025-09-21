package com.linca.vybes.post.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linca.vybes.common.posts.PostFilter
import com.linca.vybes.common.posts.PostsManager
import com.linca.vybes.model.Challenge
import com.linca.vybes.model.Like
import com.linca.vybes.post.PostsRepository
import com.linca.vybes.post.service.PostService
import com.linca.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postService: PostService,
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val postsManager = PostsManager()

    // Combine posts from PostsManager with cached updates from PostsRepository
    val posts = combine(
        postsManager.filteredPosts,
        postsRepository.cachedPosts
    ) { managedPosts, cachedPosts ->
        managedPosts.map { post ->
            // Use cached version if available, otherwise use the original post
            cachedPosts[post.id] ?: post
        }
    }

    val selectedPostFilter = postsManager.selectedPostFilter
    val isLoading = postsManager.isLoadingPosts
    val isLoadingMore = postsManager.isLoadingMorePosts
    val hasMoreContent = postsManager.hasMorePosts
    val postsError = postsManager.postsError

    private val _likeLoadingStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val likeLoadingStates = _likeLoadingStates.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _featuredChallenge = MutableStateFlow<Challenge?>(null)
    val featuredChallenge = _featuredChallenge.asStateFlow()

    private val _isChallengeVoting = MutableStateFlow(false)
    val isChallengeVoting = _isChallengeVoting.asStateFlow()

    init {
        loadInitialPosts()
        loadFeaturedChallenge()
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

                    // Cache the posts in the shared repository
                    postsRepository.cachePosts(pageResponse.content)
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

    private fun loadFeaturedChallenge() {
        viewModelScope.launch {
            _featuredChallenge.value = postService.getFeaturedChallenge().body()
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

                    // Update cache with fresh data
                    postsRepository.cachePosts(pageResponse.content)
                } else {
                    postsManager.setError("Failed to refresh: ${response.message()}")
                }
                loadFeaturedChallenge()
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

                    // Cache the new posts
                    postsRepository.cachePosts(pageResponse.content)
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

        // Optimistically update the shared repository
        if (isLikedByCurrentUser) {
            val newLikes = postsRepository.getPost(postId)?.likes.orEmpty()
                .filter { it.userId != SharedPreferencesManager.getUserId() }
            postsRepository.updatePostLikes(postId, newLikes)
            unlikePost(postId, revertOnFailure = true)
        } else {
            val newLikes = postsRepository.getPost(postId)?.likes.orEmpty() +
                    Like(SharedPreferencesManager.getUserId())
            postsRepository.updatePostLikes(postId, newLikes)
            likePost(postId, revertOnFailure = true)
        }
    }

    private fun likePost(postId: Long, revertOnFailure: Boolean) {
        viewModelScope.launch {
            try {
                val response = postService.likePost(postId)
                if (!(response.isSuccessful && response.body() != null) && revertOnFailure) {
                    // Revert optimistic update in shared repository
                    val currentPost = postsRepository.getPost(postId)
                    val revertedLikes = currentPost?.likes.orEmpty()
                        .filter { it.userId != SharedPreferencesManager.getUserId() }
                    postsRepository.updatePostLikes(postId, revertedLikes)
                    postsManager.setError("Failed to like post")
                }
            } catch (e: Exception) {
                if (revertOnFailure) {
                    // Revert optimistic update in shared repository
                    val currentPost = postsRepository.getPost(postId)
                    val revertedLikes = currentPost?.likes.orEmpty()
                        .filter { it.userId != SharedPreferencesManager.getUserId() }
                    postsRepository.updatePostLikes(postId, revertedLikes)
                }
                postsManager.setError("Network error while liking post")
            } finally {
                _likeLoadingStates.value -= postId
            }
        }
    }

    private fun unlikePost(postId: Long, revertOnFailure: Boolean) {
        viewModelScope.launch {
            try {
                val response = postService.unlikePost(postId)
                if (!(response.isSuccessful && response.body() != null) && revertOnFailure) {
                    // Revert optimistic update in shared repository
                    val currentPost = postsRepository.getPost(postId)
                    val revertedLikes = currentPost?.likes.orEmpty() +
                            Like(SharedPreferencesManager.getUserId())
                    postsRepository.updatePostLikes(postId, revertedLikes)
                    postsManager.setError("Failed to unlike post")
                }
            } catch (e: Exception) {
                if (revertOnFailure) {
                    // Revert optimistic update in shared repository
                    val currentPost = postsRepository.getPost(postId)
                    val revertedLikes = currentPost?.likes.orEmpty() +
                            Like(SharedPreferencesManager.getUserId())
                    postsRepository.updatePostLikes(postId, revertedLikes)
                }
                postsManager.setError("Network error while unliking post")
            } finally {
                _likeLoadingStates.value -= postId
            }
        }
    }

    fun clearError() {
        postsManager.setError(null)
    }

    fun voteOnChallengeOption(optionId: Long) {
        val challenge = _featuredChallenge.value ?: return

        _isChallengeVoting.value = true

        viewModelScope.launch {
            try {
                val response = postService.voteChallengeOption(challenge.id, optionId)
                if (response.isSuccessful && response.body() != null) {
                    val updatedChallenge = response.body()!!
                    _featuredChallenge.value = updatedChallenge
                } else {
                    postsManager.setError("Failed to vote on challenge")
                }
            } catch (e: Exception) {
                postsManager.setError("Network error while voting on challenge")
            } finally {
                _isChallengeVoting.value = false
            }
        }
    }
}
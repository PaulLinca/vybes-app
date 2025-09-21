package com.example.vybes.common.posts

import com.example.vybes.model.AlbumReview
import com.example.vybes.model.Like
import com.example.vybes.model.Post
import com.example.vybes.model.Vybe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostsManager {
    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    private val _filteredPosts = MutableStateFlow<List<Post>>(emptyList())
    val filteredPosts = _filteredPosts.asStateFlow()

    private val _selectedPostFilter = MutableStateFlow(PostFilter.ALL)
    val selectedPostFilter = _selectedPostFilter.asStateFlow()

    private val _isLoadingPosts = MutableStateFlow(false)
    val isLoadingPosts = _isLoadingPosts.asStateFlow()

    private val _isLoadingMorePosts = MutableStateFlow(false)
    val isLoadingMorePosts = _isLoadingMorePosts.asStateFlow()

    private val _hasMorePosts = MutableStateFlow(true)
    val hasMorePosts = _hasMorePosts.asStateFlow()

    private val _postsError = MutableStateFlow<String?>(null)
    val postsError = _postsError.asStateFlow()

    private var currentPage = 0
    private var totalPages = Int.MAX_VALUE
    private val pageSize = 10

    fun setInitialPosts(posts: List<Post>, totalPages: Int, isLastPage: Boolean) {
        _allPosts.value = posts
        this.totalPages = totalPages
        _hasMorePosts.value = !isLastPage
        currentPage = 0
        applyFilter()
    }

    fun addMorePosts(newPosts: List<Post>, isLastPage: Boolean) {
        if (newPosts.isNotEmpty()) {
            _allPosts.value += newPosts
            currentPage++
            _hasMorePosts.value = !isLastPage
            applyFilter()
        } else {
            _hasMorePosts.value = false
        }
    }

    fun setPostFilter(filter: PostFilter) {
        _selectedPostFilter.value = filter
        applyFilter()
    }

    private fun applyFilter() {
        _filteredPosts.value = _selectedPostFilter.value.applyFilter(_allPosts.value)
    }

    fun setLoadingState(isLoading: Boolean) {
        _isLoadingPosts.value = isLoading
    }

    fun setLoadingMoreState(isLoadingMore: Boolean) {
        _isLoadingMorePosts.value = isLoadingMore
    }

    fun setError(error: String?) {
        _postsError.value = error
    }

    fun getCurrentPage() = currentPage

    fun canLoadMore() =
        !_isLoadingMorePosts.value && _hasMorePosts.value && currentPage < totalPages - 1

    fun getPageSize() = pageSize

    fun updatePostLikes(postId: Long, update: (List<Like>) -> List<Like>) {
        val currentPosts = _allPosts.value
        val updatedPosts = currentPosts.map { post ->
            when (post) {
                is Vybe -> if (post.id == postId) {
                    post.copy(likes = update(post.likes.orEmpty()))
                } else post

                is AlbumReview -> if (post.id == postId) {
                    post.copy(likes = update(post.likes.orEmpty()))
                } else post
            }
        }
        _allPosts.value = updatedPosts
        applyFilter()
    }

    fun deletePost(postId: Long) {
        _allPosts.value = _allPosts.value.filterNot { it.id == postId }
        applyFilter()
    }
}
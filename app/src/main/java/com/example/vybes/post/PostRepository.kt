package com.example.vybes.post

import com.example.vybes.model.AlbumReview
import com.example.vybes.model.Comment
import com.example.vybes.model.Like
import com.example.vybes.model.Post
import com.example.vybes.model.Vybe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsRepository @Inject constructor() {

    private val _cachedPosts = MutableStateFlow<Map<Long, Post>>(emptyMap())
    val cachedPosts = _cachedPosts.asStateFlow()

    fun updatePostInCache(post: Post) {
        _cachedPosts.value = _cachedPosts.value + (post.id to post)
    }

    fun updatePostLikes(postId: Long, likes: List<Like>) {
        val currentPost = _cachedPosts.value[postId] ?: return
        val updatedPost = when (currentPost) {
            is Vybe -> currentPost.copy(likes = likes)
            is AlbumReview -> currentPost.copy(likes = likes)
        }
        updatePostInCache(updatedPost)
    }

    fun updatePostComments(postId: Long, comments: List<Comment>) {
        val currentPost = _cachedPosts.value[postId] ?: return
        val updatedPost = when (currentPost) {
            is Vybe -> currentPost.copy(comments = comments)
            is AlbumReview -> currentPost.copy(comments = comments)
        }
        updatePostInCache(updatedPost)
    }

    fun getPost(postId: Long): Post? = _cachedPosts.value[postId]

    fun removePost(postId: Long) {
        _cachedPosts.value = _cachedPosts.value - postId
    }

    fun cachePosts(posts: List<Post>) {
        val postsMap = posts.associateBy { it.id }
        _cachedPosts.value = _cachedPosts.value + postsMap
    }
}
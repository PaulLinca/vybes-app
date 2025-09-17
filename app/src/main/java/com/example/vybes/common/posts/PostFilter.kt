package com.example.vybes.common.posts

import com.example.vybes.model.AlbumReview
import com.example.vybes.model.Post
import com.example.vybes.model.Vybe

enum class PostFilter {
    ALL, VYBES, ALBUM_REVIEWS;

    fun getDisplayName(): String = when (this) {
        ALL -> "All"
        VYBES -> "Vybes"
        ALBUM_REVIEWS -> "Reviews"
    }

    fun <T : Post> applyFilter(posts: List<T>): List<Post> = when (this) {
        ALL -> posts
        VYBES -> posts.filterIsInstance<Vybe>()
        ALBUM_REVIEWS -> posts.filterIsInstance<AlbumReview>()
    }
}
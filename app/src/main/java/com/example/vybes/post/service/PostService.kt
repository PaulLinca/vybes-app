package com.example.vybes.post.service

import com.example.vybes.post.model.Comment
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.network.LikeResponse
import retrofit2.Response

interface PostService {
    suspend fun getAllVybes(): Response<List<Vybe>>

    suspend fun getVybe(id: Long): Response<Vybe>

    suspend fun likeVybe(vybeId: Long): Response<LikeResponse>

    suspend fun unlikeVybe(vybeId: Long): Response<LikeResponse>

    suspend fun likeComment(vybeId: Long, commentId: Long): Response<LikeResponse>

    suspend fun unlikeComment(vybeId: Long, commentId: Long): Response<LikeResponse>

    suspend fun addComment(vybeId: Long, text: String): Response<Comment>
}
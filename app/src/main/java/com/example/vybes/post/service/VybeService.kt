package com.example.vybes.post.service

import com.example.vybes.post.model.Like
import com.example.vybes.post.model.Vybe

interface VybeService {
    suspend fun getAllVybes(): List<Vybe>

    suspend fun getVybe(id: Int): Vybe

    suspend fun likeVybe(id: Int): Like

    suspend fun unlikeVybe(id: Int): Like

    suspend fun likeComment(vybeId: Int, commentId: Int): Like

    suspend fun unlikeComment(vybeId: Int, commentId: Int): Like
}
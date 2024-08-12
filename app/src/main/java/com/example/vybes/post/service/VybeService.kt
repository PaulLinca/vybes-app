package com.example.vybes.post.service

import com.example.vybes.post.model.Vybe

interface VybeService {
    suspend fun getAllVybes(): List<Vybe>

    suspend fun getVybe(id: Int): Vybe
}
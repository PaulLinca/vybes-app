package com.example.vybes.auth.service

import com.example.vybes.network.response.LoginResponse
import com.example.vybes.network.response.RegisterResponse
import retrofit2.Response

interface AuthService {
    suspend fun register(email: String, password: String): Response<RegisterResponse>

    suspend fun login(email: String, password: String): Response<LoginResponse>

    suspend fun refresh(token: String): Response<LoginResponse>
}
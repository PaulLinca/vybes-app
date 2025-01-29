package com.example.vybes.auth.service

import com.example.vybes.auth.model.LoginResponse
import com.example.vybes.auth.model.RegisterResponse
import retrofit2.Response

interface AuthService {
    suspend fun register(username: String, password: String): Response<RegisterResponse>

    suspend fun login(username: String, password: String): Response<LoginResponse>

    suspend fun refresh(token: String): Response<LoginResponse>
}
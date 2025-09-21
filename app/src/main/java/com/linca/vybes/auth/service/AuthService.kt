package com.linca.vybes.auth.service

import com.linca.vybes.network.response.LoginResponse
import com.linca.vybes.network.response.RegisterResponse
import retrofit2.Response

interface AuthService {
    suspend fun register(email: String, password: String): Response<RegisterResponse>

    suspend fun login(email: String, password: String): Response<LoginResponse>

    suspend fun refresh(token: String): Response<LoginResponse>
}
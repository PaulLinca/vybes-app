package com.linca.vybes.auth.service

import com.linca.vybes.network.response.LoginResponse
import com.linca.vybes.network.response.RegisterResponse
import retrofit2.Response

interface AuthService {
    suspend fun authenticate(): Response<LoginResponse>

    suspend fun logout(): Response<Void>
}
package com.example.vybes.auth.service

import com.example.vybes.network.response.LoginResponse
import com.example.vybes.network.response.RegisterResponse
import retrofit2.Response

class DummyAuthService : AuthService {
    override suspend fun register(email: String, password: String): Response<RegisterResponse> {
        return Response.success(RegisterResponse(1, "currentuser"))
    }

    override suspend fun login(email: String, password: String): Response<LoginResponse> {
        return Response.success(LoginResponse(1, "email", "currentuser", "jwt", "refresh", true))
    }

    override suspend fun refresh(token: String): Response<LoginResponse> {
        return Response.success(LoginResponse(1, "email", "currentuser", "jwt", "refresh", true))
    }
}
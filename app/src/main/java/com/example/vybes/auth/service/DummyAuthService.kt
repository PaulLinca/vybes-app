package com.example.vybes.auth.service

import com.example.vybes.auth.model.LoginResponse
import com.example.vybes.auth.model.RegisterResponse
import retrofit2.Response

class DummyAuthService : AuthService {
    override suspend fun register(username: String, password: String): Response<RegisterResponse> {
        return Response.success(RegisterResponse(1, "currentuser"))
    }

    override suspend fun login(username: String, password: String): Response<LoginResponse> {
        return Response.success(LoginResponse(1, "currentuser", "jwt"))
    }
}
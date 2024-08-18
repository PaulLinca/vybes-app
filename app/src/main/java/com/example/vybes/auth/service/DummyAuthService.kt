package com.example.vybes.auth.service

import com.example.vybes.auth.model.LoginResponse

class DummyAuthService : AuthService {
    override suspend fun register(username: String, password: String) {}

    override suspend fun login(username: String, password: String): LoginResponse {
        return LoginResponse(1, "currentuser", "jwt")
    }
}
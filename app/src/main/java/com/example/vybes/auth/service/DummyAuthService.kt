package com.example.vybes.auth.service

class DummyAuthService : AuthService {
    override suspend fun register(username: String, password: String) {}

    override suspend fun login(username: String, password: String) {}
}
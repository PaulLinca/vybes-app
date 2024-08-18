package com.example.vybes.auth.service

import com.example.vybes.auth.model.LoginResponse

interface AuthService {
    suspend fun register(username: String, password: String)

    suspend fun login(username: String, password: String): LoginResponse
}
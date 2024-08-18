package com.example.vybes.auth.service

interface AuthService {
    suspend fun register(username: String, password: String)

    suspend fun login(username: String, password: String)
}
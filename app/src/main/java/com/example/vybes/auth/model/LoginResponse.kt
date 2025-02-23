package com.example.vybes.auth.model

data class LoginResponse(
    val userId: Long,
    val email: String,
    val username: String,
    val jwt: String,
    val refreshToken: String,
    val requiresUsernameSetup: Boolean
)
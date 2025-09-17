package com.example.vybes.network.response

data class LoginResponse(
    val userId: Long,
    val email: String,
    val username: String,
    val jwt: String,
    val refreshToken: String,
    val requiresUsernameSetup: Boolean
)
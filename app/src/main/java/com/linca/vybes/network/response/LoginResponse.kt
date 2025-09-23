package com.linca.vybes.network.response

data class LoginResponse(
    val userId: Long,
    val email: String,
    val username: String,
    val requiresUsernameSetup: Boolean
)
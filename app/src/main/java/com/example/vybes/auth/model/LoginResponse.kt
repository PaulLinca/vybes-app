package com.example.vybes.auth.model

data class LoginResponse(val userId: Long, val username: String, val jwt: String)
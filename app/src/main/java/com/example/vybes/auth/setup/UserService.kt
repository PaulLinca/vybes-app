package com.example.vybes.auth.setup

import com.example.vybes.auth.model.UserResponse
import retrofit2.Response

interface UserService {
    suspend fun setupUsername(username: String): Response<UserResponse>

}
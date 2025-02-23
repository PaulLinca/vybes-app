package com.example.vybes.auth.setup

import com.example.vybes.auth.model.UserResponse
import com.example.vybes.auth.model.UsernameSetupRequest
import com.example.vybes.network.VybesApiClient
import retrofit2.Response

class VybesUserService(private val vybesApiClient: VybesApiClient) : UserService {
    override suspend fun setupUsername(username: String): Response<UserResponse> {
        return vybesApiClient.setupUsername(
            UsernameSetupRequest(username)
        )
    }
}
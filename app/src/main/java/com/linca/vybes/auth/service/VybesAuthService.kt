package com.linca.vybes.auth.service

import com.linca.vybes.network.VybesApiClient
import com.linca.vybes.network.request.AuthRequest
import com.linca.vybes.network.response.LoginResponse
import com.linca.vybes.network.response.RegisterResponse
import retrofit2.Response

class VybesAuthService(private val vybesApiClient: VybesApiClient) : AuthService {
    override suspend fun authenticate(): Response<LoginResponse> {
        return vybesApiClient.authenticate()
    }

    override suspend fun logout(): Response<Void> {
        return vybesApiClient.logout()
    }
}
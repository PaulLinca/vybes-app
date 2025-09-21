package com.linca.vybes.auth.service

import com.linca.vybes.network.VybesApiClient
import com.linca.vybes.network.request.AuthRequest
import com.linca.vybes.network.response.LoginResponse
import com.linca.vybes.network.response.RegisterResponse
import retrofit2.Response

class VybesAuthService(private val vybesApiClient: VybesApiClient) : AuthService {

    override suspend fun register(email: String, password: String): Response<RegisterResponse> {
        return vybesApiClient.register(
            AuthRequest(email = email, password = password)
        )
    }

    override suspend fun login(email: String, password: String): Response<LoginResponse> {
        return vybesApiClient.login(
            AuthRequest(email = email, password = password)
        )
    }

    override suspend fun refresh(token: String): Response<LoginResponse> {
        return vybesApiClient.refresh(token)
    }
}
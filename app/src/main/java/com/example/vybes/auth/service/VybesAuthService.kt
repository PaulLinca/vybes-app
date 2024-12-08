package com.example.vybes.auth.service

import com.example.vybes.auth.model.AuthRequest
import com.example.vybes.auth.model.LoginResponse
import com.example.vybes.auth.model.RegisterResponse
import com.example.vybes.network.VybesApiClient
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class VybesAuthService(private val vybesApiClient: VybesApiClient) : AuthService {

    override suspend fun register(username: String, password: String): Response<RegisterResponse> {
        return vybesApiClient.register(
            AuthRequest(username = username, password = password)
        )
    }

    override suspend fun login(username: String, password: String): Response<LoginResponse> {
        return vybesApiClient.login(
            AuthRequest(username = username, password = password)
        )
    }

    override suspend fun refresh(token: String): Response<LoginResponse> {
        return vybesApiClient.refresh(token)
    }
}
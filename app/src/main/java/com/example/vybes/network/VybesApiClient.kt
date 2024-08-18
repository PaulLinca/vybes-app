package com.example.vybes.network

import com.example.vybes.auth.model.AuthRequest
import com.example.vybes.auth.model.LoginResponse
import com.example.vybes.auth.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface VybesApiClient {
    @Headers(
        value = [
            "Accept: application/json",
            "Content-type:application/json"]
    )
    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: AuthRequest): Response<RegisterResponse>

    @Headers(
        value = [
            "Accept: application/json",
            "Content-type:application/json"]
    )
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: AuthRequest): Response<LoginResponse>
}
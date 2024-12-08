package com.example.vybes.network

import com.example.vybes.sharedpreferences.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        if (url.contains("/api/auth/login") || url.contains("/api/auth/register")) {
            return chain.proceed(request)
        }

        val requestBuilder = request.newBuilder()
        val token = SharedPreferencesManager.getJwt()

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
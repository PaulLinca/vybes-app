package com.example.vybes.network

import com.example.vybes.sharedpreferences.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        if (url.contains("/api/auth/login") || url.contains("/api/auth/register")) {
            return try {
                chain.proceed(request)
            } catch (e: java.net.ConnectException) {
                noNetworkResponse(request)
            } catch (e: Exception) {
                otherErrorResponse(request, e)
            }
        }

        val requestBuilder = request.newBuilder()
        val token = SharedPreferencesManager.getJwt()

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return try {
            chain.proceed(requestBuilder.build())
        } catch (e: java.net.ConnectException) {
            noNetworkResponse(request)
        } catch (e: Exception) {
            otherErrorResponse(request, e)
        }
    }

    private fun otherErrorResponse(request: Request, e: Exception) = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(500)
        .message("Network error occurred")
        .body(
            "{\"error\": \"${e.message}\"}"
                .toResponseBody("application/json".toMediaType())
        )
        .build()

    private fun noNetworkResponse(request: Request) = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(503)
        .message("No network connection")
        .body(
            "{\"error\": \"No network connection available\"}"
                .toResponseBody("application/json".toMediaType())
        )
        .build()
}
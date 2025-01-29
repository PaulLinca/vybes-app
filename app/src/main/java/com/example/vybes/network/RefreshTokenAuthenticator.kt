package com.example.vybes.network

import android.util.Log
import com.example.vybes.auth.AuthEvent
import com.example.vybes.auth.AuthEventBus
import com.example.vybes.auth.model.LoginResponse
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TokenAuthenticator : Authenticator {
    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            mutex.withLock {
                Log.e("TokenAuthenticator", "getting refresh token")
                val refreshToken = SharedPreferencesManager.getRefreshToken()
                Log.e("TokenAuthenticator", "Refresh token" + refreshToken)
                if (refreshToken.isNullOrEmpty()) {
                    Log.e("TokenAuthenticator", "emitting event")
                    AuthEventBus.emit(AuthEvent.TokenExpired)
                    return@withLock null
                }
                try {
                    val currentToken = SharedPreferencesManager.getJwt()
                    if (currentToken != null && !isTokenInResponse(response, currentToken)) {
                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                    }

                    val newTokenResponse = getUpdatedToken(refreshToken)
                    Log.e("TokenAuthenticator", "response" + newTokenResponse)
                    if (newTokenResponse.isSuccessful) {
                        val newToken = newTokenResponse.body()?.jwt
                        val newRefreshToken = newTokenResponse.body()?.refreshToken

                        if (newToken != null && newRefreshToken != null) {
                            SharedPreferencesManager.saveTokens(newToken, newRefreshToken)

                            return@runBlocking response.request.newBuilder()
                                .header("Authorization", "Bearer $newToken")
                                .build()
                        }
                    }
                    Log.e("TokenAuthenticator", "refresh failed - emitting event")
                    // refresh failed
                    SharedPreferencesManager.clearTokens()
                    AuthEventBus.emit(AuthEvent.TokenExpired)
                    null
                } catch (e: Exception) {
                    Log.e("TokenAuthenticator", "exception - emitting event")
                    // exception happened
                    SharedPreferencesManager.clearTokens()
                    AuthEventBus.emit(AuthEvent.TokenExpired)
                    null
                }
            }
        }
    }

    private fun isTokenInResponse(response: Response, token: String): Boolean {
        val authHeader = response.request.header("Authorization") ?: return false
        return authHeader == "Bearer $token"
    }

    // all of this is needed because of a circular dependency...
    // https://stackoverflow.com/questions/35238894/android-retrofit-2-authenticator-result
    // https://stackoverflow.com/questions/22450036/refreshing-oauth-token-using-retrofit-without-modifying-all-calls
    private suspend fun getUpdatedToken(refreshToken: String): retrofit2.Response<LoginResponse> {
        val okHttpClient = OkHttpClient().newBuilder()
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        val service = retrofit.create(VybesApiClient::class.java)
        return service.refresh(refreshToken)
    }
}
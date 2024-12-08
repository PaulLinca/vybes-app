package com.example.vybes.network

import android.util.Log
import com.example.vybes.auth.model.LoginResponse
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = SharedPreferencesManager.getRefreshToken()
        Log.e("WTF", "WTF")
        if (refreshToken.isNullOrEmpty()) {
            return null
        }

        return runBlocking {
            try {
                val newTokenResponse = getUpdatedToken(refreshToken)
                if (response.isSuccessful) {
                    val newToken = newTokenResponse.body()?.jwt
                    val newRefreshToken = newTokenResponse.body()?.refreshToken

                    if (newToken != null && newRefreshToken != null) {
                        SharedPreferencesManager.saveTokens(newToken, newRefreshToken)

                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .build()
                    }
                }
                null
            } catch (e: Exception) {
                null
            }
        }
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
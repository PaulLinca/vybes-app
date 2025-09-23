// File: 'app/src/main/java/com/linca/vybes/network/interceptor/FirebaseAuthInterceptor.kt'
package com.linca.vybes.network.interceptor

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import okhttp3.Protocol
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

class FirebaseAuthInterceptor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    private val cachedToken = AtomicReference<String?>()
    private val TAG = "FirebaseAuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val user = firebaseAuth.currentUser
        if (user == null) {
            Log.d(TAG, "No Firebase user. Proceeding without Authorization header for ${original.url}")
            return proceedSafely(chain, original)
        }

        val token = runBlocking(Dispatchers.IO) {
            cachedToken.get() ?: fetchToken(user, force = false).also {
                if (it == null) {
                    fetchToken(user, force = true)
                }
            }
        }

        if (token.isNullOrBlank()) {
            Log.w(TAG, "Failed to obtain Firebase ID token. Proceeding without header.")
            return proceedSafely(chain, original)
        }

        cachedToken.set(token)

        val newReq = original.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        Log.d(TAG, "Added Authorization header to ${original.method} ${original.url}")
        return proceedSafely(chain, newReq)
    }

    // Simulate error response (503) otherwise uncaught ConnectException crashes the app
    private fun proceedSafely(chain: Interceptor.Chain, request: okhttp3.Request): Response {
        return try {
            chain.proceed(request)
        } catch (e: IOException) {
            Log.e(TAG, "Network failure: ${e.message}")
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(503)
                .message("Service Unavailable")
                .body("".toResponseBody("text/plain".toMediaType()))
                .build()
        }
    }

    private suspend fun fetchToken(user: com.google.firebase.auth.FirebaseUser, force: Boolean): String? {
        return try {
            user.getIdToken(force).await().token
        } catch (e: Exception) {
            Log.e(TAG, "Token fetch failed (force=$force): ${e.message}")
            null
        }
    }
}

package com.linca.vybes.network.interceptor

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log
import java.util.concurrent.atomic.AtomicReference

class FirebaseAuthInterceptor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    private val cachedToken = AtomicReference<String?>()
    private val TAG = "FirebaseAuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Skip if no user
        val user = firebaseAuth.currentUser
        if (user == null) {
            Log.d(TAG, "No Firebase user. Proceeding without Authorization header for ${original.url}")
            return chain.proceed(original)
        }

        val token = runBlocking(Dispatchers.IO) {
            // Try cached first
            cachedToken.get() ?: fetchToken(user, force = false).also {
                if (it == null) {
                    // Retry with force refresh
                    fetchToken(user, force = true)
                }
            }
        }

        if (token.isNullOrBlank()) {
            Log.w(TAG, "Failed to obtain Firebase ID token. Proceeding without header.")
            return chain.proceed(original)
        }

        cachedToken.set(token)

        val newReq = original.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        Log.d(TAG, "Added Authorization header to ${original.method} ${original.url}")
        return chain.proceed(newReq)
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

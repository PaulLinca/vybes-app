package com.linca.vybes.auth.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthManager @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(appContext)

    suspend fun signInWithGoogle(activity: android.app.Activity): Result<String> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId("54902794891-t3u4uqpt138qi2dkf9gg841emjr1p3pv.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            handleSignIn(result)
        } catch (e: NoCredentialException) {
            Log.w("FirebaseAuthManager", "NoCredentialException", e)
            val msg = when {
                e.message?.contains("User canceled", ignoreCase = true) == true ->
                    "Sign in canceled."
                else ->
                    "No selectable Google account or config mismatch. Re-add fingerprints (SHA-1 & SHA-256), update google-services.json, ensure at least one account."
            }
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Google sign-in failed", e)
            Result.failure(Exception("Google sign-in failed: ${e.message ?: "Unknown error"}"))
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Result<String> {
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    val authCredential = GoogleAuthProvider
                        .getCredential(googleIdTokenCredential.idToken, null)

                    try {
                        val authResult = auth.signInWithCredential(authCredential).await()
                        val idToken = authResult.user?.getIdToken(false)?.await()?.token
                        if (idToken != null) {
                            Result.success(idToken)
                        } else {
                            Result.failure(Exception("Failed to get ID token"))
                        }
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                } else {
                    Result.failure(Exception("Unexpected credential type"))
                }
            }

            else -> Result.failure(Exception("Unexpected credential type"))
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    fun isSignedIn() = auth.currentUser != null
}
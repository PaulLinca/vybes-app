package com.example.vybes.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SharedPreferencesManager {
    private const val PREF_NAME = "MyEncryptedAppPreferences"
    private const val USER_ID_KEY = "USER_ID"
    private const val USERNAME_KEY = "USERNAME"
    private const val EMAIL_KEY = "USERNAME"
    private const val JWT_KEY = "JWT"
    private const val REFRESH_TOKEN = "REFRESH_TOKEN"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveDataOnLogin(
        userId: Long,
        email: String,
        username: String,
        jwt: String,
        refreshToken: String
    ) {
        saveUserData(userId, email, username)
        saveTokens(jwt, refreshToken)
    }

    fun saveUserData(userId: Long, email: String, username: String) {
        sharedPreferences.edit()
            .putLong(USER_ID_KEY, userId)
            .putString(EMAIL_KEY, email)
            .putString(USERNAME_KEY, username)
            .apply()
    }

    fun saveTokens(jwt: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString(JWT_KEY, jwt)
            .putString(REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getUserId(): Long = sharedPreferences.getLong(USER_ID_KEY, -1)

    fun getUsername(): String? = sharedPreferences.getString(USERNAME_KEY, null)

    fun getEmail(): String? = sharedPreferences.getString(EMAIL_KEY, null)

    fun getJwt(): String? = sharedPreferences.getString(JWT_KEY, null)

    fun getRefreshToken(): String? = sharedPreferences.getString(REFRESH_TOKEN, null)

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getUserId() != -1L &&
                !getUsername().isNullOrEmpty() &&
                !getEmail().isNullOrEmpty() &&
                !getJwt().isNullOrEmpty() &&
                !getRefreshToken().isNullOrEmpty()
    }

    fun clearTokens() {
        sharedPreferences.edit()
            .remove(JWT_KEY)
            .remove(REFRESH_TOKEN)
            .apply()
    }
}
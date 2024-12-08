package com.example.vybes.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SharedPreferencesManager {
    private const val PREF_NAME = "MyEncryptedAppPreferences"
    private const val USER_ID_KEY = "USER_ID"
    private const val USERNAME_KEY = "USERNAME"
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

    fun saveDataOnLogin(userId: Long, username: String, jwt: String, refreshToken: String) {
        saveUserData(userId, username)
        saveTokens(jwt, refreshToken)
    }

    fun saveUserData(userId: Long, username: String) {
        sharedPreferences.edit()
            .putLong(USER_ID_KEY, userId)
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

    fun getJwt(): String? = sharedPreferences.getString(JWT_KEY, null)

    fun getRefreshToken(): String? = sharedPreferences.getString(REFRESH_TOKEN, null)

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}
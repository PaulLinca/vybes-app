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

    private fun getEncryptedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveUserData(context: Context, userId: Long, username: String, jwt: String) {
        val editor = getEncryptedPreferences(context).edit()
        editor.putLong(USER_ID_KEY, userId)
        editor.putString(USERNAME_KEY, username)
        editor.putString(JWT_KEY, jwt)
        editor.apply()
    }

    fun getUserId(context: Context): Long {
        return getEncryptedPreferences(context).getLong(USER_ID_KEY, -1)
    }

    fun getUsername(context: Context): String? {
        return getEncryptedPreferences(context).getString(USERNAME_KEY, null)
    }

    fun getJwt(context: Context): String? {
        return getEncryptedPreferences(context).getString(JWT_KEY, null)
    }

    fun clearUserData(context: Context) {
        val editor = getEncryptedPreferences(context).edit()
        editor.clear()
        editor.apply()
    }
}
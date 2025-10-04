package com.linca.vybes.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth

object SharedPreferencesManager {
    private const val PREFS_NAME = "vybes_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_FIREBASE_USER_ID = "firebase_user_id"
    private const val KEY_VYBES_USER_ID = "vybes_user_id"
    private const val KEY_PROFILE_PICTURE_URL = "profile_picture_url"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isLoggedIn(): Boolean = FirebaseAuth.getInstance().currentUser != null

    fun setUsername(username: String) {
        sharedPreferences.edit { putString(KEY_USERNAME, username) }
    }

    fun getUsername(): String? = sharedPreferences.getString(KEY_USERNAME, null)

    fun setUserId(userId: Long) {
        sharedPreferences.edit { putLong(KEY_VYBES_USER_ID, userId) }
    }

    fun getUserId(): Long = sharedPreferences.getLong(KEY_VYBES_USER_ID, -1)

    fun setFirebaseId(firebaseId: String) {
        sharedPreferences.edit { putString(KEY_FIREBASE_USER_ID, firebaseId) }
    }

    fun getFirebaseId(): String? = sharedPreferences.getString(KEY_FIREBASE_USER_ID, null)

    fun setProfilePictureUrl(profilePictureUrl: String) {
        sharedPreferences.edit { putString(KEY_PROFILE_PICTURE_URL, profilePictureUrl) }
    }

    fun getProfilePictureUrl(): String? = sharedPreferences.getString(KEY_PROFILE_PICTURE_URL, null)

    fun clearAll() {
        sharedPreferences.edit { clear() }
        FirebaseAuth.getInstance().signOut()
    }
}

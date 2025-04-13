package com.example.vybes.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.AuthEvent
import com.example.vybes.auth.AuthEventBus
import com.example.vybes.auth.model.UserResponse
import com.example.vybes.auth.setup.UserService
import com.example.vybes.post.model.User
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user

    fun loadUser(username: String) {
        viewModelScope.launch {
            val response = userService.getUser(username)
            if (response.isSuccessful) {
                _user.value = response.body()
            } else {
                Log.e("ProfileViewModel", "Error fetching user: ${response.errorBody()?.string()}")
            }
        }
    }

    fun isCurrentUser(user: User): Boolean {
        return user.username == SharedPreferencesManager.getUsername().orEmpty()
    }

    fun logout() {
        viewModelScope.launch {
            SharedPreferencesManager.clearUserData()
            AuthEventBus.emit(AuthEvent.TokenCleared)
        }
    }
}
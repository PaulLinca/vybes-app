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

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null
    )

    fun loadUser(username: String) {
        viewModelScope.launch {
            try {
                // Set loading state
                _uiState.value = UiState(isLoading = true)

                val response = userService.getUser(username)
                if (response.isSuccessful) {
                    _user.value = response.body()
                    // Clear loading and error state
                    _uiState.value = UiState(isLoading = false)
                } else {
                    // Set error state
                    _uiState.value = UiState(
                        isLoading = false,
                        error = "Failed to load user data. Error: ${response.code()}"
                    )
                    Log.e(
                        "ProfileViewModel",
                        "Error fetching user: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                // Handle network exceptions
                _uiState.value = UiState(
                    isLoading = false,
                    error = "Network error: ${e.message ?: "Unknown error"}"
                )
                Log.e("ProfileViewModel", "Exception fetching user", e)
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
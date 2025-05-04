package com.example.vybes.auth.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSetupSuccess = MutableStateFlow(false)
    val isSetupSuccess = _isSetupSuccess.asStateFlow()

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError = _usernameError.asStateFlow()

    private var _usernameText by mutableStateOf("")
    val usernameText: String
        get() = _usernameText

    fun updateUsernameText(updatedText: String) {
        _usernameText = updatedText
        _usernameError.value = null
    }

    private fun isUsernameValid(): Boolean {
        if (usernameText.isNotBlank()) {
            return true
        }

        _usernameError.value = "Username can't be blank"
        return false
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _usernameError.value = null

            if (isUsernameValid()) {
                val response = userService.setupUsername(usernameText)
                if (response.isSuccessful && response.body() != null) {
                    _usernameError.value = null

                    val loginResponse = response.body()!!
                    SharedPreferencesManager.saveUsername(loginResponse.username)

                    _isSetupSuccess.value = true
                } else {
                    _usernameError.value = "Username invalid"
                }
            }

        }
        _isLoading.value = false
    }
}


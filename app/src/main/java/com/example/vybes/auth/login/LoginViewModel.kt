package com.example.vybes.auth.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.service.AuthService
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private val EMAIL_REGEX = Patterns.EMAIL_ADDRESS

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess = _isLoginSuccess.asStateFlow()

    private val _requiresUsernameSetup = MutableStateFlow(false)
    val requiresUsernameSetup = _requiresUsernameSetup.asStateFlow()

    private var _emailText by mutableStateOf("")
    val emailText: String
        get() = _emailText

    private var _passwordText by mutableStateOf("")
    val passwordText: String
        get() = _passwordText

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    fun updateEmailText(updatedText: String) {
        _emailText = updatedText
    }

    fun updatePasswordText(updatedText: String) {
        _passwordText = updatedText
    }

    private fun isLoginInfoValid(): Boolean {
        var isValid = true

        if (emailText.isBlank()) {
            _emailError.value = "Email cannot be empty"
            isValid = false
        } else if (!EMAIL_REGEX.matcher(emailText).matches()) {
            _emailError.value = "Please enter a valid email address"
            isValid = false
        } else {
            _emailError.value = null
        }

        if (passwordText.isBlank()) {
            _passwordError.value = "Password cannot be empty"
            isValid = false
        } else {
            _passwordError.value = null
        }

        return isValid
    }

    fun login() {
        viewModelScope.launch {
            _loginError.value = null
            _isLoading.value = true

            if (isLoginInfoValid()) {
                try {
                    val response = authService.login(emailText, passwordText)
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!
                        SharedPreferencesManager.saveDataOnLogin(
                            loginResponse.userId,
                            loginResponse.email,
                            loginResponse.username,
                            loginResponse.jwt,
                            loginResponse.refreshToken
                        )

                        _requiresUsernameSetup.value = loginResponse.requiresUsernameSetup
                        _isLoginSuccess.value = true
                    } else {
                        when (response.code()) {
                            401 -> _loginError.value = "Invalid email or password"
                            403 -> _loginError.value = "Account is locked or disabled"
                            else -> _loginError.value = "Login failed unexpectedly"
                        }
                    }
                } catch (e: Exception) {
                    _loginError.value = "Unexpected error occurred"
                }
            }
            _isLoading.value = false
        }
    }
}
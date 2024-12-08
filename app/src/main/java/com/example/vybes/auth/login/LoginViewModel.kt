package com.example.vybes.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.service.AuthService
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess = _isLoginSuccess.asStateFlow()

    private var _usernameText by mutableStateOf("")
    val usernameText: String
        get() = _usernameText

    private var _passwordText by mutableStateOf("")
    val passwordText: String
        get() = _passwordText

    private val _isLoginInfoInvalid = MutableStateFlow(false)
    val isLoginInfoInvalid = _isLoginInfoInvalid.asStateFlow()

    fun updateUsernameText(updatedText: String) {
        _usernameText = updatedText
        _isLoginInfoInvalid.value = false
    }

    fun updatePasswordText(updatedText: String) {
        _passwordText = updatedText
        _isLoginInfoInvalid.value = false
    }

    private fun validateLoginInfo() {
        _isLoginInfoInvalid.value = usernameText.isBlank() || passwordText.isBlank()
    }

    fun login() {
        viewModelScope.launch {
            _isLoginInfoInvalid.value = false
            _isLoading.value = true

            validateLoginInfo()

            delay(1000)
            if (!_isLoginInfoInvalid.value) {
                val response = authService.login(usernameText, passwordText)
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    SharedPreferencesManager.saveDataOnLogin(
                        loginResponse.userId,
                        loginResponse.username,
                        loginResponse.jwt,
                        loginResponse.refreshToken
                    )

                    _isLoginSuccess.value = true
                } else {
                    _isLoginInfoInvalid.value = true
                }
            }
            _isLoading.value = false
        }
    }
}
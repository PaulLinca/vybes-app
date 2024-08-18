package com.example.vybes.auth.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.service.AuthService
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authService: AuthService
) : ViewModel() {

    private var _usernameText: String by mutableStateOf("")
    val usernameText: String
        get() = _usernameText

    fun updateUsernameText(updatedText: String) {
        _usernameText = updatedText
        _isLoginInfoInvalid.value = false
    }

    private var _passwordText: String by mutableStateOf("")
    val passwordText: String
        get() = _passwordText

    fun updatePasswordText(updatedText: String) {
        _passwordText = updatedText
        _isLoginInfoInvalid.value = false
    }

    private val _isLoginInfoInvalid = MutableStateFlow(false)
    val isLoginInfoInvalid = _isLoginInfoInvalid.asStateFlow()

    private fun validateRegisterInfo() {
        val isUsernameValid = usernameText.isNotBlank()
        val isPasswordValid = passwordText.isNotBlank()

        _isLoginInfoInvalid.value = !isUsernameValid || !isPasswordValid
    }

    fun login() {
        viewModelScope.launch {
            validateRegisterInfo()
            if (!_isLoginInfoInvalid.value) {
                val loginResponse = authService.login(usernameText, passwordText)
                SharedPreferencesManager.saveUserData(
                    context,
                    loginResponse.userId,
                    loginResponse.username,
                    loginResponse.jwt
                )
            }
        }
    }
}
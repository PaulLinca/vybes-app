package com.example.vybes.auth.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val MIN_PASSWORD_LENGTH = 8

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _usernameText: String by mutableStateOf("")
    val usernameText: String
        get() = _usernameText

    fun updateUsernameText(updatedText: String) {
        _usernameText = updatedText
        _isRegisterInfoInvalid.value = false
    }

    private var _passwordText: String by mutableStateOf("")
    val passwordText: String
        get() = _passwordText

    fun updatePasswordText(updatedText: String) {
        _passwordText = updatedText
        _isRegisterInfoInvalid.value = false
    }

    private var _repeatPasswordText: String by mutableStateOf("")
    val repeatPasswordText: String
        get() = _repeatPasswordText

    fun updateRepeatPasswordText(updatedText: String) {
        _repeatPasswordText = updatedText
        _isRegisterInfoInvalid.value = false
    }

    private val _isRegisterInfoInvalid = MutableStateFlow(false)
    val isRegisterInfoInvalid = _isRegisterInfoInvalid.asStateFlow()

    private fun validateRegisterInfo() {
        val isUsernameValid = usernameText.isNotBlank()
        val isPasswordValid = passwordText.length >= MIN_PASSWORD_LENGTH
        val isRepeatPasswordValid = passwordText == repeatPasswordText

        _isRegisterInfoInvalid.value =
            !isUsernameValid || !isPasswordValid || !isRepeatPasswordValid
    }

    fun register(onRegisterSuccess: () -> Unit) {
        viewModelScope.launch {
            _isRegisterInfoInvalid.value = false
            _isLoading.value = true

            validateRegisterInfo()

            if (!_isRegisterInfoInvalid.value) {
                val response = authService.register(usernameText, passwordText)
                if (response.isSuccessful && response.body() != null) {
                    _isLoading.value = false
                    onRegisterSuccess()
                } else {
                    _isRegisterInfoInvalid.value = true
                }
            }
            _isLoading.value = false
        }
    }
}
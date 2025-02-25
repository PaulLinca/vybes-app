package com.example.vybes.auth.register

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val MIN_PASSWORD_LENGTH = 8
    private val EMAIL_REGEX = Patterns.EMAIL_ADDRESS

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _emailText: String by mutableStateOf("")
    val emailText: String
        get() = _emailText

    fun updateEmailText(updatedText: String) {
        _emailText = updatedText
    }

    private var _passwordText: String by mutableStateOf("")
    val passwordText: String
        get() = _passwordText

    fun updatePasswordText(updatedText: String) {
        _passwordText = updatedText
    }

    private var _repeatPasswordText: String by mutableStateOf("")
    val repeatPasswordText: String
        get() = _repeatPasswordText

    fun updateRepeatPasswordText(updatedText: String) {
        _repeatPasswordText = updatedText
    }

    private val _isRegisterInfoInvalid = MutableStateFlow(false)

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    private val _repeatPasswordError = MutableStateFlow<String?>(null)
    val repeatPasswordError = _repeatPasswordError.asStateFlow()

    private fun validateRegisterInfo(): Boolean {
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

        if (passwordText.length < MIN_PASSWORD_LENGTH) {
            _passwordError.value = "Password must be at least $MIN_PASSWORD_LENGTH characters"
            isValid = false
        } else {
            _passwordError.value = null
        }

        if (passwordText != repeatPasswordText) {
            _repeatPasswordError.value = "Passwords do not match"
            isValid = false
        } else {
            _repeatPasswordError.value = null
        }

        _isRegisterInfoInvalid.value = !isValid
        return isValid
    }

    fun register(onRegisterSuccess: () -> Unit) {
        viewModelScope.launch {
            _isRegisterInfoInvalid.value = false
            _isLoading.value = true

            validateRegisterInfo()

            if (!_isRegisterInfoInvalid.value) {
                val response = authService.register(emailText, passwordText)
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
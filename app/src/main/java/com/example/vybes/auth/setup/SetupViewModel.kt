package com.example.vybes.auth.setup

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
class SetupViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSetupSuccess = MutableStateFlow(false)
    val isSetupSuccess = _isSetupSuccess.asStateFlow()

    private val _isUsernameInvalid = MutableStateFlow(false)
    val isUsernameInvalid = _isUsernameInvalid.asStateFlow()

    private var _usernameText by mutableStateOf("")
    val usernameText: String
        get() = _usernameText

    fun updateUsernameText(updatedText: String) {
        _usernameText = updatedText
    }

    private fun validateUsernameInfo() {
        _isUsernameInvalid.value = usernameText.isBlank()
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true

            validateUsernameInfo()

            delay(1000)
            if (!_isUsernameInvalid.value) {
                val response = userService.setupUsername(usernameText)
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    SharedPreferencesManager.saveUsername(loginResponse.username)

                    _isSetupSuccess.value = true
                } else {
                    _isUsernameInvalid.value = true
                }
            }
            _isLoading.value = false
        }
    }
}

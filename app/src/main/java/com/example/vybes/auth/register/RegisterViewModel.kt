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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    data class RegisterUiState(
        val isLoading: Boolean = false,
        val email: String = "",
        val password: String = "",
        val repeatPassword: String = "",
        val emailError: String? = null,
        val passwordError: String? = null,
        val repeatPasswordError: String? = null,
        val networkError: String? = null,
        val isRegisterSuccess: Boolean = false
    )

    private val _uiState = MutableStateFlow(RegisterUiState())

    val isLoading = _uiState.map { it.isLoading }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val emailError = _uiState.map { it.emailError }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val passwordError = _uiState.map { it.passwordError }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val repeatPasswordError = _uiState.map { it.repeatPasswordError }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val networkError = _uiState.map { it.networkError }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val isRegisterSuccess = _uiState.map { it.isRegisterSuccess }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    var emailText by mutableStateOf(_uiState.value.email)
        private set

    var passwordText by mutableStateOf(_uiState.value.password)
        private set

    var repeatPasswordText by mutableStateOf(_uiState.value.repeatPassword)
        private set

    fun updateEmailText(updatedText: String) {
        emailText = updatedText
        _uiState.update {
            it.copy(
                email = updatedText,
                emailError = null,
                networkError = null
            )
        }
    }

    fun updatePasswordText(updatedText: String) {
        passwordText = updatedText
        _uiState.update {
            it.copy(
                password = updatedText,
                passwordError = null,
                networkError = null
            )
        }
    }

    fun updateRepeatPasswordText(updatedText: String) {
        repeatPasswordText = updatedText
        _uiState.update {
            it.copy(
                repeatPassword = updatedText,
                repeatPasswordError = null,
                networkError = null
            )
        }
    }

    fun register() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, networkError = null) }

            try {
                val result = safeApiCall { authService.register(emailText, passwordText) }

                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isRegisterSuccess = true) }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(networkError = result.message ?: "Registration failed unexpectedly")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(networkError = e.localizedMessage ?: "Unexpected error occurred")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val emailError = when {
            emailText.isBlank() -> "Email cannot be empty"
            !Patterns.EMAIL_ADDRESS.matcher(emailText).matches() -> "Please enter a valid email address"
            else -> null
        }

        val passwordError = when {
            passwordText.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }

        val repeatPasswordError = when {
            passwordText != repeatPasswordText -> "Passwords do not match"
            else -> null
        }

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                repeatPasswordError = repeatPasswordError
            )
        }

        return emailError == null && passwordError == null && repeatPasswordError == null
    }

    sealed class Resource<out T> {
        data class Success<T>(val data: T) : Resource<T>()
        data class Error(val errorCode: Int? = null, val message: String? = null) : Resource<Nothing>()
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.code(), response.message())
            }
        } catch (e: IOException) {
            Resource.Error(message = "Network error. Please check your connection")
        } catch (e: Exception) {
            Resource.Error(message = e.localizedMessage)
        }
    }
}

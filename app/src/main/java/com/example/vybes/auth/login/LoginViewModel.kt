package com.example.vybes.auth.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.model.LoginResponse
import com.example.vybes.auth.service.AuthService
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    data class LoginUiState(
        val isLoading: Boolean = false,
        val isLoginSuccess: Boolean = false,
        val requiresUsernameSetup: Boolean = false,
        val email: String = "",
        val password: String = "",
        val emailError: String? = null,
        val passwordError: String? = null,
        val networkError: String? = null
    )

    private val _uiState = MutableStateFlow(LoginUiState())

    val isLoading = _uiState.map { it.isLoading }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val isLoginSuccess = _uiState.map { it.isLoginSuccess }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val requiresUsernameSetup =
        _uiState.map { it.requiresUsernameSetup }.distinctUntilChanged().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), false
        )

    val emailError = _uiState.map { it.emailError }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val passwordError = _uiState.map { it.passwordError }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val loginError = _uiState.map { it.networkError }.distinctUntilChanged().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    var emailText by mutableStateOf(_uiState.value.email)
        private set

    var passwordText by mutableStateOf(_uiState.value.password)
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

    fun login() {
        if (!validateInputs()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, networkError = null) }

                val result = safeApiCall { authService.login(emailText, passwordText) }

                when (result) {
                    is Resource.Success -> handleSuccessfulLogin(result.data)
                    is Resource.Error -> handleLoginError(result.errorCode, result.message)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        networkError = e.localizedMessage ?: "Unexpected error occurred"
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val emailError = when {
            emailText.isBlank() -> "Email cannot be empty"
            !Patterns.EMAIL_ADDRESS.matcher(emailText)
                .matches() -> "Please enter a valid email address"

            else -> null
        }

        val passwordError = when {
            passwordText.isBlank() -> "Password cannot be empty"
            passwordText.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError
            )
        }

        return emailError == null && passwordError == null
    }

    private fun handleSuccessfulLogin(loginResponse: LoginResponse) {
        SharedPreferencesManager.saveDataOnLogin(
            loginResponse.userId,
            loginResponse.email,
            loginResponse.username,
            loginResponse.jwt,
            loginResponse.refreshToken
        )

        _uiState.update {
            it.copy(
                isLoginSuccess = true,
                requiresUsernameSetup = loginResponse.requiresUsernameSetup
            )
        }
    }

    private fun handleLoginError(errorCode: Int?, message: String?) {
        val errorMessage = when (errorCode) {
            401 -> "Invalid email or password"
            500 -> "Server error. Please try again later"
            null -> message ?: "Network error"
            else -> message ?: "Login failed unexpectedly"
        }

        _uiState.update { it.copy(networkError = errorMessage) }
    }

    sealed class Resource<out T> {
        data class Success<T>(val data: T) : Resource<T>()
        data class Error(val errorCode: Int? = null, val message: String? = null) :
            Resource<Nothing>()
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
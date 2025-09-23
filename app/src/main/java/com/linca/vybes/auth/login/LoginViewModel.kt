package com.linca.vybes.auth.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.linca.vybes.auth.firebase.FirebaseAuthManager
import com.linca.vybes.auth.service.AuthService
import com.linca.vybes.network.response.LoginResponse
import com.linca.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val firebaseAuthManager: FirebaseAuthManager
) : ViewModel() {

    data class LoginUiState(
        val isLoading: Boolean = false,
        val isLoginSuccess: Boolean = false,
        val requiresUsernameSetup: Boolean = false,
        val networkError: String? = null
    )

    private val _uiState = MutableStateFlow(LoginUiState())

    val isLoading = _uiState.map { it.isLoading }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val isLoginSuccess = _uiState.map { it.isLoginSuccess }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val requiresUsernameSetup = _uiState.map { it.requiresUsernameSetup }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val loginError = _uiState.map { it.networkError }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun signIn(activity: Activity) {
        _uiState.update { it.copy(isLoading = true, networkError = null, isLoginSuccess = false) }

        viewModelScope.launch {
            firebaseAuthManager.signInWithGoogle(activity)
                .onSuccess {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                networkError = "No Firebase user"
                            )
                        }
                        return@launch
                    }

                    SharedPreferencesManager.setFirebaseId(user.uid)

                    val idToken = try {
                        user.getIdToken(true).await().token
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(isLoading = false, networkError = "Failed to get ID token")
                        }
                        return@launch
                    }

                    authenticate(idToken)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            networkError = e.message ?: "Google sign in failed"
                        )
                    }
                }
        }
    }

    private fun authenticate(idToken: String?) {
        if (idToken == null) {
            _uiState.update { it.copy(isLoading = false, networkError = "Missing ID token") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val result = safeApiCall { authService.authenticate() }

                when (result) {
                    is Resource.Success -> handleSuccessfulLogin(result.data)
                    is Resource.Error -> handleLoginError(result.errorCode, result.message)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(networkError = e.localizedMessage ?: "Unexpected error")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleSuccessfulLogin(loginResponse: LoginResponse) {
        SharedPreferencesManager.setUsername(loginResponse.username)
        SharedPreferencesManager.setUserId(loginResponse.userId)

        _uiState.update {
            it.copy(
                isLoginSuccess = true,
                requiresUsernameSetup = loginResponse.requiresUsernameSetup
            )
        }
    }

    private fun handleLoginError(errorCode: Int?, message: String?) {
        val errorMessage = when (errorCode) {
            401 -> "Invalid credentials"
            500 -> "Server error. Try again later"
            null -> message ?: "Network error"
            else -> message ?: "Login failed"
        }
        _uiState.update { it.copy(isLoginSuccess = false, networkError = errorMessage) }
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
            Resource.Error(message = "Network error. Please try again later.")
        } catch (e: Exception) {
            Resource.Error(message = e.localizedMessage)
        }
    }
}

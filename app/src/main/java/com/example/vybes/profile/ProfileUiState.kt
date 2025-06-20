package com.example.vybes.profile

import com.example.vybes.auth.model.UserResponse

data class ProfileUiState(
    val isLoadingUser: Boolean = false,
    val uploadSuccessMessage: String? = null,
    val userError: String? = null,
    val user: UserResponse? = null
) {
    val isLoading = isLoadingUser
    val error = userError
}
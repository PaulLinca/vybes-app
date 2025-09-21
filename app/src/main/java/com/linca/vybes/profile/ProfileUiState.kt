package com.linca.vybes.profile

import com.linca.vybes.network.response.UserResponse

data class ProfileUiState(
    val isLoadingUser: Boolean = false,
    val uploadSuccessMessage: String? = null,
    val userError: String? = null,
    val user: UserResponse? = null
) {
    val isLoading = isLoadingUser
    val error = userError
}
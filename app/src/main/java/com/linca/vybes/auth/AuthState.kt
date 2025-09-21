package com.linca.vybes.auth

sealed class AuthState {
    object Authenticating : AuthState()
    object Authenticated : AuthState()
    object NeedsLogin : AuthState()
}
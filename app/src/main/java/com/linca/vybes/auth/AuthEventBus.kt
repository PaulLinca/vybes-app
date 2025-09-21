package com.linca.vybes.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEventBus {
    private val _authEvents = MutableSharedFlow<AuthEvent>()
    val authEvents = _authEvents.asSharedFlow()

    suspend fun emit(event: AuthEvent) {
        _authEvents.emit(event)
    }
}

sealed class AuthEvent {
    data object TokenExpired : AuthEvent()
    data object TokenCleared : AuthEvent()
}
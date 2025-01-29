package com.example.vybes.auth

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
    object TokenExpired : AuthEvent()
}
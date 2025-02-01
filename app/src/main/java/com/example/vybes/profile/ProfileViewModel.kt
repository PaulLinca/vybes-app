package com.example.vybes.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.AuthEvent
import com.example.vybes.auth.AuthEventBus
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    init {
        _username.value = SharedPreferencesManager.getUsername().orEmpty()
    }

    fun logout() {
        viewModelScope.launch {
            SharedPreferencesManager.clearUserData()
            AuthEventBus.emit(AuthEvent.TokenCleared)
        }
    }
}
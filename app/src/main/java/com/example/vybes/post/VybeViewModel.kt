package com.example.vybes.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.post.service.VybeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VybeViewModel @Inject constructor(
    private val vybeService: VybeService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val args = VybeScreen.from(savedStateHandle)

    private val _vybe = MutableStateFlow<Vybe?>(null)
    val vybe: StateFlow<Vybe?> = _vybe

    private val _isLikedByCurrentUser = MutableStateFlow(false)
    val isLikedByCurrentUser = _isLikedByCurrentUser.asStateFlow()

    init {
        loadVybe()
    }

    fun likeVybe() {
        viewModelScope.launch {
            val vybe = vybeService.likeVybe(args.id)
            _vybe.value = vybe
            _isLikedByCurrentUser.value = true
        }
    }

    fun unlikeVybe() {
        viewModelScope.launch {
            val vybe = vybeService.unlikeVybe(args.id)
            _vybe.value = vybe
            _isLikedByCurrentUser.value = false
        }
    }

    private fun loadVybe() {
        viewModelScope.launch {
            _vybe.value = vybeService.getVybe(args.id)
        }
    }
}
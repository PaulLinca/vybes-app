package com.example.vybes.post.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.service.VybeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val vybeService: VybeService
) : ViewModel() {

    private val _vybes = MutableStateFlow<List<Vybe>>(emptyList())
    val vybes: StateFlow<List<Vybe>> = _vybes

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _vybes.value = vybeService.getAllVybes()
        }
    }
}
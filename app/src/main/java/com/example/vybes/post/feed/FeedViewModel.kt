package com.example.vybes.post.feed

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.service.VybeService
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vybeService: VybeService
) : ViewModel() {

    private val _vybes = MutableStateFlow<List<Vybe>>(emptyList())
    val vybes: StateFlow<List<Vybe>> = _vybes

    init {
        loadPosts()
        Log.e("SAVED DATA", SharedPreferencesManager.getUsername(context).orEmpty())
        Log.e("SAVED DATA", SharedPreferencesManager.getUserId(context).toString())
        Log.e("SAVED DATA", SharedPreferencesManager.getJwt(context).orEmpty())
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _vybes.value = vybeService.getAllVybes()
        }
    }
}
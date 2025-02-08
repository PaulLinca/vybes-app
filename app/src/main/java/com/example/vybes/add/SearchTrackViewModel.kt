package com.example.vybes.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.add.model.network.TrackSearchResult
import com.example.vybes.post.service.VybesPostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchTrackViewModel @Inject constructor(
    private val vybesPostService: VybesPostService
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<TrackSearchResult>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun updateSearchQuery(query: String) {
        searchQuery = query
        if (query.length >= 3) {
            performSearch(query)
        } else {
            searchResults = emptyList()
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = vybesPostService.searchTrack(query)
                if (response.isSuccessful) {
                    searchResults = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Failed to fetch results: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
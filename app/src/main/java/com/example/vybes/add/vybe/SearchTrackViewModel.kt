package com.example.vybes.add.vybe

import androidx.lifecycle.viewModelScope
import com.example.vybes.add.GenericSearchViewModel
import com.example.vybes.model.TrackSearchResult
import com.example.vybes.post.service.VybesPostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchTrackViewModel @Inject constructor(
    private val vybesPostService: VybesPostService
) : GenericSearchViewModel<TrackSearchResult>(vybesPostService) {

    override fun performSearch(query: String) {
        viewModelScope.launch {
            updateLoadingState(true)
            updateErrorState(null)
            try {
                val response = vybesPostService.searchTrack(query)
                if (response.isSuccessful) {
                    updateResults(response.body().orEmpty())
                } else {
                    updateErrorState("Error searching tracks")
                }
            } catch (e: Exception) {
                updateErrorState("Error searching tracks: ${e.message}")
            } finally {
                updateLoadingState(false)
            }
        }
    }
}
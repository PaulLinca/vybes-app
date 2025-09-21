package com.linca.vybes.add.album

import androidx.lifecycle.viewModelScope
import com.linca.vybes.add.GenericSearchViewModel
import com.linca.vybes.model.AlbumSearchResult
import com.linca.vybes.post.service.VybesPostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchAlbumViewModel @Inject constructor(
    private val vybesPostService: VybesPostService
) : GenericSearchViewModel<AlbumSearchResult>(vybesPostService) {

    override fun performSearch(query: String) {
        viewModelScope.launch {
            updateLoadingState(true)
            updateErrorState(null)
            try {
                val response = vybesPostService.searchAlbum(query)
                if (response.isSuccessful) {
                    updateResults(response.body().orEmpty())
                } else {
                    updateErrorState("Error searching albums")
                }
            } catch (e: Exception) {
                updateErrorState("Error searching albums: ${e.message}")
            } finally {
                updateLoadingState(false)
            }
        }
    }
}
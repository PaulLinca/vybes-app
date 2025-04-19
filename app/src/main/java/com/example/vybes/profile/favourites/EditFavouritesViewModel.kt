package com.example.vybes.profile.favourites

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.model.MediaItem
import com.example.vybes.auth.setup.UserService
import com.example.vybes.post.service.VybesPostService
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditFavouritesViewModel @Inject constructor(
    private val vybesPostService: VybesPostService,
    private val userService: UserService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val favoriteType: FavoriteType = savedStateHandle.get<String>("favoriteType")?.let {
        try {
            FavoriteType.valueOf(it)
        } catch (e: Exception) {
            FavoriteType.ARTISTS
        }
    } ?: FavoriteType.ARTISTS

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _currentFavorites = MutableStateFlow<List<MediaItem>>(emptyList())
    val currentFavorites: StateFlow<List<MediaItem>> = _currentFavorites

    private val _selectedIndex = MutableStateFlow<Int?>(null)
    val selectedIndex: StateFlow<Int?> = _selectedIndex

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val searchResults: StateFlow<List<MediaItem>> = _searchResults

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSearching: Boolean = false,
        val saveSuccess: Boolean = false
    )

    init {
        loadCurrentUserFavorites()
    }

    private fun loadCurrentUserFavorites() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState(isLoading = true)

                val username = SharedPreferencesManager.getUsername().orEmpty()
                val response = userService.getUser(username)

                if (response.isSuccessful && response.body() != null) {
                    val userResponse = response.body()!!

                    val favorites = when (favoriteType) {
                        FavoriteType.ARTISTS -> userResponse.favoriteArtists
                        FavoriteType.ALBUMS -> userResponse.favoriteAlbums
                    }

                    val filledFavorites = favorites.take(3)
                    val paddedFavorites = if (filledFavorites.size < 3) {
                        filledFavorites + List(3 - filledFavorites.size) {
                            createEmptyMediaItem()
                        }
                    } else {
                        filledFavorites
                    }

                    _currentFavorites.value = paddedFavorites
                    _uiState.value = UiState(isLoading = false)
                } else {
                    _uiState.value = UiState(
                        isLoading = false,
                        error = "Failed to load user data"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiState(
                    isLoading = false,
                    error = "Network error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        if (query.length >= 2) {
            search(query)
        } else {
            _searchResults.value = emptyList()
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSearching = true)

                // Search for the appropriate type
                val response = when (favoriteType) {
                    FavoriteType.ARTISTS -> vybesPostService.searchArtist(query)
                    FavoriteType.ALBUMS -> vybesPostService.searchAlbum(query)
                }

                if (response.isSuccessful && response.body() != null) {
                    _searchResults.value = response.body()!!
                    _uiState.value = _uiState.value.copy(isSearching = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        error = "Search failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = "Search error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun selectFavoriteToReplace(index: Int) {
        _selectedIndex.value = index
    }

    fun replaceSelectedFavorite(newItem: MediaItem) {
        _selectedIndex.value?.let { index ->
            val updatedFavorites = _currentFavorites.value.toMutableList()
            updatedFavorites[index] = newItem
            _currentFavorites.value = updatedFavorites
            _selectedIndex.value = null
            updateSearchQuery("")
        }
    }

    fun deleteFavorite(index: Int) {
        val updatedFavorites = _currentFavorites.value.toMutableList()
        updatedFavorites[index] = createEmptyMediaItem()
        _currentFavorites.value = updatedFavorites
    }

    fun saveFavorites() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                Log.e("WTF", _currentFavorites.value.toString())
                val favoritesToSave = _currentFavorites.value.filter { it.spotifyId != "" }
                val response = when (favoriteType) {
                    FavoriteType.ARTISTS -> userService.setFavoriteArtists(favoritesToSave)
                    FavoriteType.ALBUMS -> userService.setFavoriteAlbums(favoritesToSave)
                }

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        saveSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to save favorites"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    private fun createEmptyMediaItem(): MediaItem {
        return object : MediaItem {
            override val spotifyId: String = ""
            override val name: String = ""
            override val imageUrl: String = ""
            override val isEmpty: Boolean = true
        }
    }
}
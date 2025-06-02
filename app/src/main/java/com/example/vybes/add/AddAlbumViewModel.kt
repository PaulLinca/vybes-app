package com.example.vybes.add

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.model.Album
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.post.service.PostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AddAlbumViewModel @Inject constructor(
    private val postService: PostService,
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {

    private val args = AddAlbumReviewScreen.from(savedStateHandle)

    sealed class ReviewUiState {
        data object Loading : ReviewUiState()
        data class LoadingCall(val album: Album) : ReviewUiState()
        data class Success(val album: Album) : ReviewUiState()
        data class Error(val message: String) : ReviewUiState()
    }

    private val _album = MutableStateFlow<Album?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private var _descriptionText: String by mutableStateOf("")

    val descriptionText: String get() = _descriptionText
    val album = _album.asStateFlow()

    val uiState = combine(
        _album,
        _isLoading,
        _errorMessage
    ) { album, isLoading, error ->
        when {
            album != null && isLoading -> ReviewUiState.LoadingCall(album)
            isLoading -> ReviewUiState.Loading
            error != null -> ReviewUiState.Error(error)
            album != null -> ReviewUiState.Success(album)
            else -> ReviewUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReviewUiState.Loading)

    init {
        loadAlbum()
    }

    fun updateText(updatedText: String) {
        _descriptionText = updatedText
    }

    private fun loadAlbum() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            safeApiCall { postService.getAlbum(args.spotifyId) }.onSuccess { a ->
                _album.value = a
            }.onFailure { error ->
                _errorMessage.value = "Failed to load album: ${error.localizedMessage}"
            }

            _isLoading.value = false
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                Result.success(response.body()) as Result<T>
            } else {
                Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

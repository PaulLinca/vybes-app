package com.example.vybes.add

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.model.Album
import com.example.vybes.post.model.network.CreateAlbumReviewRequest
import com.example.vybes.post.model.network.TrackRating
import com.example.vybes.post.model.network.TrackReview
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
    private val maxReviewLength = 5000

    sealed class ReviewUiState {
        data object Loading : ReviewUiState()
        data class LoadingCall(val album: Album) : ReviewUiState()
        data class Success(val album: Album) : ReviewUiState()
        data class AlbumReviewExists(val album: Album) : ReviewUiState()
        data class Error(val message: String) : ReviewUiState()
    }

    private val _album = MutableStateFlow<Album?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private var _descriptionText: String by mutableStateOf("")
    private var _albumRating: Int by mutableStateOf(0)
    private val _trackRatings = mutableStateMapOf<String, TrackRating>()
    private val _favoriteTrackIds = mutableStateListOf<String>()
    private var _remainingCharacters: Int by mutableStateOf(maxReviewLength)

    val descriptionText: String get() = _descriptionText
    val albumRating: Int get() = _albumRating
    val trackRatings: Map<String, TrackRating> get() = _trackRatings.toMap()
    val favoriteTrackIds: List<String> get() = _favoriteTrackIds.toList()
    val album = _album.asStateFlow()
    val remainingCharacters: Int get() = _remainingCharacters

    val uiState = combine(
        _album,
        _isLoading,
        _errorMessage
    ) { album, isLoading, error ->
        when {
            album != null && isLoading -> ReviewUiState.LoadingCall(album)
            isLoading -> ReviewUiState.Loading
            album?.reviewId != null -> ReviewUiState.AlbumReviewExists(album)
            error != null -> ReviewUiState.Error(error)
            album != null -> ReviewUiState.Success(album)
            else -> ReviewUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReviewUiState.Loading)

    init {
        loadAlbum()
    }

    fun updateText(updatedText: String) {
        if (updatedText.length <= maxReviewLength) {
            _descriptionText = updatedText
            _remainingCharacters = maxReviewLength - updatedText.length
        }
    }

    fun updateAlbumRating(rating: Int) {
        _albumRating = rating.coerceIn(0, 10)
    }

    fun updateTrackRating(trackId: String, rating: TrackRating) {
        _trackRatings[trackId] = rating
    }

    fun toggleFavoriteTrack(trackId: String) {
        if (_favoriteTrackIds.contains(trackId)) {
            _favoriteTrackIds.remove(trackId)
        } else if (_favoriteTrackIds.size < 3) {
            _favoriteTrackIds.add(trackId)
        }
    }

    fun submit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val request = buildCreateAlbumReviewRequest()
            if (request != null) {
                safeApiCall { postService.postAlbumReview(request) }.onSuccess {
                    onSuccess.invoke()
                }.onFailure { error ->
                    _errorMessage.value = "Failed to submit: ${error.localizedMessage}"
                }
            }

            _isLoading.value = false
        }
    }

    private fun loadAlbum() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            safeApiCall { postService.getAlbum(args.spotifyId, true) }.onSuccess { a ->
                _album.value = a
            }.onFailure { error ->
                _errorMessage.value = "Failed to load album: ${error.localizedMessage}"
            }

            _isLoading.value = false
        }
    }

    fun buildCreateAlbumReviewRequest(): CreateAlbumReviewRequest? {
        val currentAlbum = _album.value ?: return null

        if (_albumRating == 0 || _descriptionText.isBlank()) {
            return null
        }

        val trackReviews = currentAlbum.tracks.map { track ->
            TrackReview(
                id = null,
                name = track.name,
                spotifyTrackId = track.spotifyId,
                rating = _trackRatings[track.spotifyId],
                isFavorite = _favoriteTrackIds.contains(track.spotifyId)
            )
        }

        return CreateAlbumReviewRequest(
            spotifyAlbumId = currentAlbum.spotifyId,
            score = _albumRating,
            description = _descriptionText,
            trackReviews = trackReviews
        )
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
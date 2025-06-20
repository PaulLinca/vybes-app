package com.example.vybes.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.AuthEvent
import com.example.vybes.auth.AuthEventBus
import com.example.vybes.auth.model.UserResponse
import com.example.vybes.auth.setup.UserService
import com.example.vybes.post.model.AlbumReview
import com.example.vybes.post.model.Post
import com.example.vybes.post.model.User
import com.example.vybes.post.model.Vybe
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    enum class PostFilter {
        ALL, VYBES, ALBUM_REVIEWS
    }

    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _allPosts = MutableStateFlow<List<Post>>(emptyList()) // Store all posts
    private val _filteredPosts = MutableStateFlow<List<Post>>(emptyList())
    val filteredPosts = _filteredPosts.asStateFlow()

    private val _selectedPostFilter = MutableStateFlow(PostFilter.ALL)
    val selectedPostFilter = _selectedPostFilter.asStateFlow()

    private val _isLoadingPosts = MutableStateFlow(false)
    val isLoadingPosts = _isLoadingPosts.asStateFlow()

    private val _isLoadingMorePosts = MutableStateFlow(false)
    val isLoadingMorePosts = _isLoadingMorePosts.asStateFlow()

    private val _hasMorePosts = MutableStateFlow(true)
    val hasMorePosts = _hasMorePosts.asStateFlow()

    private val _postsError = MutableStateFlow<String?>(null)
    val postsError = _postsError.asStateFlow()

    private var currentPostsPage = 0
    private val postsPageSize = 10
    private var totalPostsPages = Int.MAX_VALUE
    private var currentUserId: Long? = null

    var imagePart: MultipartBody.Part? = null

    data class UiState(
        val isLoading: Boolean = false,
        val uploadSuccessMessage: String? = null,
        val error: String? = null
    )

    fun loadUser(username: String) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState(isLoading = true)

                val response = userService.getUser(username)
                if (response.isSuccessful) {
                    _user.value = response.body()
                    _uiState.value = UiState(isLoading = false)

                    response.body()?.let { userResponse ->
                        currentUserId = userResponse.userId
                        loadInitialPosts(userResponse.userId)
                    }
                } else {
                    _uiState.value = UiState(
                        isLoading = false,
                        error = "Failed to load user data. Error: ${response.code()}"
                    )
                    Log.e(
                        "ProfileViewModel",
                        "Error fetching user: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiState(
                    isLoading = false,
                    error = "Network error: ${e.message ?: "Unknown error"}"
                )
                Log.e("ProfileViewModel", "Exception fetching user", e)
            }
        }
    }

    private fun loadInitialPosts(userId: Long) {
        viewModelScope.launch {
            _isLoadingPosts.value = true
            _postsError.value = null
            currentPostsPage = 0

            try {
                val response = userService.getPostsPaginated(
                    userId = userId,
                    page = 0,
                    size = postsPageSize,
                    sort = null,
                    direction = null
                )
                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    _allPosts.value = pageResponse.content
                    totalPostsPages = pageResponse.totalPages
                    _hasMorePosts.value = !pageResponse.last
                    applyFilter()
                } else {
                    _postsError.value = "Failed to load posts: ${response.message()}"
                }
            } catch (e: Exception) {
                _postsError.value = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isLoadingPosts.value = false
            }
        }
    }

    fun loadMorePosts() {
        val userId = currentUserId ?: return
        if (_isLoadingMorePosts.value || !_hasMorePosts.value || currentPostsPage >= totalPostsPages - 1) return

        viewModelScope.launch {
            _isLoadingMorePosts.value = true
            try {
                val nextPage = currentPostsPage + 1
                val response = userService.getPostsPaginated(
                    userId = userId,
                    page = nextPage,
                    size = postsPageSize,
                    sort = null,
                    direction = null
                )

                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    val newPosts = pageResponse.content

                    if (newPosts.isNotEmpty()) {
                        _allPosts.value += newPosts
                        currentPostsPage = nextPage
                        _hasMorePosts.value = !pageResponse.last
                        applyFilter()
                    } else {
                        _hasMorePosts.value = false
                    }
                } else {
                    _postsError.value = "Failed to load more posts"
                }
            } catch (e: Exception) {
                _postsError.value = "Network error while loading more posts"
            } finally {
                _isLoadingMorePosts.value = false
            }
        }
    }

    fun setPostFilter(filter: PostFilter) {
        _selectedPostFilter.value = filter
        applyFilter()
    }

    private fun applyFilter() {
        val allPosts = _allPosts.value
        _filteredPosts.value = when (_selectedPostFilter.value) {
            PostFilter.ALL -> allPosts
            PostFilter.VYBES -> allPosts.filterIsInstance<Vybe>()
            PostFilter.ALBUM_REVIEWS -> allPosts.filterIsInstance<AlbumReview>()
        }
    }

    fun isCurrentUser(user: User): Boolean {
        return user.username == SharedPreferencesManager.getUsername().orEmpty()
    }

    fun logout() {
        viewModelScope.launch {
            SharedPreferencesManager.clearUserData()
            AuthEventBus.emit(AuthEvent.TokenCleared)
        }
    }

    fun createMultipartFromUri(context: Context, uri: Uri): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = "profile_picture_${System.currentTimeMillis()}.jpg"

        val file = File(context.cacheDir, fileName)
        file.outputStream().use { output ->
            inputStream?.copyTo(output)
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }

    fun uploadProfilePicture() {
        _uiState.value = UiState(isLoading = true)

        val part = imagePart ?: return

        viewModelScope.launch {
            val response = try {
                userService.setupProfilePicture(part)
            } catch (e: Exception) {
                Log.e("Upload", "Failed: ${e.message}")
                _uiState.value = UiState(isLoading = false, error = "Upload failed")
                return@launch
            }

            if (response.isSuccessful) {
                _user.value = response.body()
                _uiState.value = UiState(
                    isLoading = false,
                    uploadSuccessMessage = "Upload successful!\nChanges may take a moment to appear"
                )
            } else {
                _uiState.value = UiState(isLoading = false, error = "Upload failed")
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = UiState(uploadSuccessMessage = null)
    }
}
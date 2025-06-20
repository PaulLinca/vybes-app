package com.example.vybes.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.auth.AuthEvent
import com.example.vybes.auth.AuthEventBus
import com.example.vybes.auth.setup.UserService
import com.example.vybes.common.posts.PostFilter
import com.example.vybes.common.posts.PostsManager
import com.example.vybes.post.model.User
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val postsManager = PostsManager()
    private var currentUserId: Long? = null
    var imagePart: MultipartBody.Part? = null

    val filteredPosts = postsManager.filteredPosts
    val selectedPostFilter = postsManager.selectedPostFilter
    val isLoadingPosts = postsManager.isLoadingPosts
    val isLoadingMorePosts = postsManager.isLoadingMorePosts
    val hasMorePosts = postsManager.hasMorePosts
    val postsError = postsManager.postsError

    fun loadUser(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingUser = true, userError = null)

            try {
                val response = userService.getUser(username)
                if (response.isSuccessful) {
                    val user = response.body()
                    _uiState.value = _uiState.value.copy(
                        isLoadingUser = false,
                        user = user
                    )

                    user?.let { userResponse ->
                        currentUserId = userResponse.userId
                        loadInitialPosts(userResponse.userId)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingUser = false,
                        userError = "Failed to load user data. Error: ${response.code()}"
                    )
                    Log.e(
                        "ProfileViewModel",
                        "Error fetching user: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingUser = false,
                    userError = "Network error: ${e.message ?: "Unknown error"}"
                )
                Log.e("ProfileViewModel", "Exception fetching user", e)
            }
        }
    }

    private fun loadInitialPosts(userId: Long) {
        viewModelScope.launch {
            postsManager.setLoadingState(true)
            postsManager.setError(null)

            try {
                val response = userService.getPostsPaginated(
                    userId = userId,
                    page = 0,
                    size = postsManager.getPageSize(),
                    sort = null,
                    direction = null
                )

                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    postsManager.setInitialPosts(
                        posts = pageResponse.content,
                        totalPages = pageResponse.totalPages,
                        isLastPage = pageResponse.last
                    )
                } else {
                    postsManager.setError("Failed to load posts: ${response.message()}")
                }
            } catch (e: Exception) {
                postsManager.setError("Network error: ${e.localizedMessage ?: "Unknown error"}")
            } finally {
                postsManager.setLoadingState(false)
            }
        }
    }

    fun loadMorePosts() {
        val userId = currentUserId ?: return
        if (!postsManager.canLoadMore()) return

        viewModelScope.launch {
            postsManager.setLoadingMoreState(true)

            try {
                val nextPage = postsManager.getCurrentPage() + 1
                val response = userService.getPostsPaginated(
                    userId = userId,
                    page = nextPage,
                    size = postsManager.getPageSize(),
                    sort = null,
                    direction = null
                )

                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    postsManager.addMorePosts(
                        newPosts = pageResponse.content,
                        isLastPage = pageResponse.last
                    )
                } else {
                    postsManager.setError("Failed to load more posts")
                }
            } catch (e: Exception) {
                postsManager.setError("Network error while loading more posts")
            } finally {
                postsManager.setLoadingMoreState(false)
            }
        }
    }

    fun setPostFilter(filter: PostFilter) {
        postsManager.setPostFilter(filter)
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
        val part = imagePart ?: return

        _uiState.value = _uiState.value.copy(isLoadingUser = true)

        viewModelScope.launch {
            try {
                val response = userService.setupProfilePicture(part)

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoadingUser = false,
                        user = response.body(),
                        uploadSuccessMessage = "Upload successful!\nChanges may take a moment to appear"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingUser = false,
                        userError = "Upload failed"
                    )
                }
            } catch (e: Exception) {
                Log.e("Upload", "Failed: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoadingUser = false,
                    userError = "Upload failed: ${e.message}"
                )
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(uploadSuccessMessage = null)
    }
}
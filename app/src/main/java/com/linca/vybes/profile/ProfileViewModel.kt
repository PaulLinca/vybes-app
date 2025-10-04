package com.linca.vybes.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.linca.vybes.auth.AuthEvent
import com.linca.vybes.auth.AuthEventBus
import com.linca.vybes.auth.setup.UserService
import com.linca.vybes.common.posts.PostFilter
import com.linca.vybes.common.posts.PostsManager
import com.linca.vybes.model.User
import com.linca.vybes.network.request.ProfilePictureRequest
import com.linca.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userService: UserService,
    private val firebaseStorage: FirebaseStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val postsManager = PostsManager()
    private var currentUserId: Long? = null

    val filteredPosts = postsManager.filteredPosts
    val selectedPostFilter = postsManager.selectedPostFilter
    val isLoadingPosts = postsManager.isLoadingPosts
    val isLoadingMorePosts = postsManager.isLoadingMorePosts
    val hasMorePosts = postsManager.hasMorePosts

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
            SharedPreferencesManager.clearAll()
            AuthEventBus.emit(AuthEvent.TokenCleared)
        }
    }

    fun uploadProfilePictureToFirebase() {
        val userId = currentUserId ?: return
        _uiState.value = _uiState.value.copy(isLoadingUser = true, userError = null)

        viewModelScope.launch {
            try {
                val objectPath = "profile_pictures/$userId.jpg"
                val ref = firebaseStorage.reference.child(objectPath)

                val downloadUrl = ref.downloadUrl.await().toString()
                val updateResp = userService.setProfilePicture(
                    ProfilePictureRequest(
                        profilePictureUrl = downloadUrl
                    )
                )

                if (!updateResp.isSuccessful) {

                    _uiState.value = _uiState.value.copy(
                        isLoadingUser = false,
                        userError = "Failed to save URL"
                    )

                    SharedPreferencesManager.setProfilePictureUrl(updateResp.body()?.profilePictureUrl.orEmpty())
                    return@launch
                }

                _uiState.value.user?.let { current ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingUser = false,
                        user = current.copy(profilePictureUrl = downloadUrl),
                        uploadSuccessMessage = "Upload successful!\nChanges may take a moment to appear"
                    )
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoadingUser = false,
                        uploadSuccessMessage = "Upload successful!"
                    )
                }
            } catch (e: Exception) {
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
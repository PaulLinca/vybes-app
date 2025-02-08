package com.example.vybes.post

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.Like
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.post.service.PostService
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VybeViewModel @Inject constructor(
    private val postService: PostService,
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {
    private val args = VybeScreen.from(savedStateHandle)

    private val _vybe = MutableStateFlow<Vybe?>(null)
    val vybe: StateFlow<Vybe?> = _vybe

    private var _commentText: String by mutableStateOf("")
    val commentText: String
        get() = _commentText

    private val _isLikedByCurrentUser = MutableStateFlow(false)
    val isLikedByCurrentUser = _isLikedByCurrentUser.asStateFlow()

    init {
        loadVybe()
    }

    fun likeVybe() {
        viewModelScope.launch {
            val response = postService.likeVybe(args.id)
            if (response.isSuccessful) {
                _vybe.value?.let { v ->
                    _vybe.value = v.copy(likes = v.likes + Like(response.body()!!.userId))
                    _isLikedByCurrentUser.value = true
                }
            }
        }
    }

    fun unlikeVybe() {
        viewModelScope.launch {
            val removedLike = postService.unlikeVybe(args.id)
            _vybe.value?.let { v ->
                _vybe.value =
                    v.copy(likes = v.likes.filterNot { it.userId == removedLike.body()!!.userId })
                _isLikedByCurrentUser.value = false
            }
        }
    }

    fun likeComment(commentId: Long) {
        viewModelScope.launch {
            val newLike = postService.likeComment(args.id, commentId)
            _vybe.value?.let { v ->
                val updatedComments = v.comments.map { comment ->
                    if (comment.id == commentId) {
                        comment.copy(
                            likes = comment.likes + listOf(
                                Like(
                                    userId = newLike.body()?.userId
                                        ?: throw IllegalStateException("Like response is null")
                                )
                            )
                        )
                    } else {
                        comment
                    }
                }
                _vybe.value = v.copy(comments = updatedComments)
            }
        }
    }

    fun unlikeComment(commentId: Long) {
        viewModelScope.launch {
            postService.unlikeComment(args.id, commentId)
            _vybe.value?.let { v ->
                val updatedComments = v.comments.map { comment ->
                    if (comment.id == commentId) {
                        comment.copy(likes = comment.likes.filterNot { it.userId == SharedPreferencesManager.getUserId() })
                    } else {
                        comment
                    }
                }
                _vybe.value = v.copy(comments = updatedComments)
            }
        }
    }

    fun addComment() {
        viewModelScope.launch {
            val addedComment = postService.addComment(args.id, commentText.trim())
            _vybe.value?.let { v ->
                _vybe.value = v.copy(
                    comments = v.comments + addedComment.body()!!
                )
            }
            clearText()
        }
    }

    fun updateText(updatedText: String) {
        _commentText = updatedText
    }

    private fun clearText() {
        _commentText = ""
    }

    private fun loadVybe() {
        viewModelScope.launch {
            val retrievedVybe = postService.getVybe(args.id)

            retrievedVybe.body().let { v ->
                val currentUserId = SharedPreferencesManager.getUserId()
                _isLikedByCurrentUser.value = v?.likes?.any { it.userId == currentUserId } == true
                _vybe.value = v
            }
        }
    }
}
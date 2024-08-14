package com.example.vybes.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.post.service.VybeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VybeViewModel @Inject constructor(
    private val vybeService: VybeService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val args = VybeScreen.from(savedStateHandle)

    private val _vybe = MutableStateFlow<Vybe?>(null)
    val vybe: StateFlow<Vybe?> = _vybe

    private val _isLikedByCurrentUser = MutableStateFlow(false)
    val isLikedByCurrentUser = _isLikedByCurrentUser.asStateFlow()

    init {
        loadVybe()
    }

    fun likeVybe() {
        viewModelScope.launch {
            val newLike = vybeService.likeVybe(args.id)
            _vybe.value?.let { v ->
                _vybe.value = v.copy(likes = v.likes + newLike)
            }
            _isLikedByCurrentUser.value = true
        }
    }

    fun unlikeVybe() {
        viewModelScope.launch {
            val removedLike = vybeService.unlikeVybe(args.id)
            _vybe.value?.let { v ->
                _vybe.value =
                    v.copy(likes = v.likes.filterNot { it.user.name == removedLike.user.name })
            }
            _isLikedByCurrentUser.value = false
        }
    }

    fun likeComment(commentId: Int) {
        viewModelScope.launch {
            val newLike = vybeService.likeComment(args.id, commentId)
            _vybe.value?.let { v ->
                val updatedComments = v.comments.map { comment ->
                    if (comment.id == commentId) {
                        comment.copy(likes = comment.likes + newLike)
                    } else {
                        comment
                    }
                }
                _vybe.value = v.copy(comments = updatedComments)
            }
        }
    }

    fun unlikeComment(commentId: Int) {
        viewModelScope.launch {
            val removedLike = vybeService.unlikeComment(args.id, commentId)
            _vybe.value?.let { v ->
                val updatedComments = v.comments.map { comment ->
                    if (comment.id == commentId) {
                        comment.copy(likes = comment.likes.filterNot { it.user.name == removedLike.user.name })
                    } else {
                        comment
                    }
                }
                _vybe.value = v.copy(comments = updatedComments)
            }
        }
    }

    private fun loadVybe() {
        viewModelScope.launch {
            val retrievedVybe = vybeService.getVybe(args.id)
            _vybe.value = retrievedVybe
        }
    }
}
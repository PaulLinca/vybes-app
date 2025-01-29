package com.example.vybes.post.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vybes.post.model.Like
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.service.PostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postService: PostService
) : ViewModel() {

    private val _vybes = MutableStateFlow<List<Vybe>>(emptyList())
    val vybes: StateFlow<List<Vybe>> = _vybes

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _vybes.value = postService.getAllVybes().body().orEmpty()
        }
    }

    fun clickLikeButton(vybeId: Long, isLikedByCurrentUser: Boolean) {
        if (isLikedByCurrentUser) {
            unlikeVybe(vybeId)
        } else {
            likeVybe(vybeId)
        }
    }

    private fun likeVybe(vybeId: Long) {
        viewModelScope.launch {
            val response = postService.likeVybe(vybeId)
            if (response.isSuccessful) {
                updateVybeLikes(vybeId) { currentLikes ->
                    currentLikes + Like(response.body()!!.userId)
                }
            }
        }
    }

    private fun unlikeVybe(vybeId: Long) {
        viewModelScope.launch {
            val response = postService.unlikeVybe(vybeId)
            if (response.isSuccessful) {
                updateVybeLikes(vybeId) { currentLikes ->
                    currentLikes.filter { it.userId != response.body()!!.userId }
                }
            }
        }
    }

    private fun updateVybeLikes(vybeId: Long, update: (List<Like>) -> List<Like>) {
        _vybes.value = _vybes.value.map { vybe ->
            if (vybe.id == vybeId) {
                vybe.copy(likes = update(vybe.likes))
            } else {
                vybe
            }
        }
    }
}
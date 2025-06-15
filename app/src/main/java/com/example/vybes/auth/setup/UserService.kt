package com.example.vybes.auth.setup

import com.example.vybes.auth.model.MediaItem
import com.example.vybes.auth.model.UserResponse
import com.example.vybes.post.model.Post
import com.example.vybes.post.model.network.PageResponse
import okhttp3.MultipartBody
import retrofit2.Response

interface UserService {
    suspend fun setupUsername(username: String): Response<UserResponse>

    suspend fun setupProfilePicture(image: MultipartBody.Part): Response<UserResponse>

    suspend fun getUser(username: String): Response<UserResponse>

    suspend fun setFavoriteArtists(artists: List<MediaItem>): Response<Void>

    suspend fun setFavoriteAlbums(albums: List<MediaItem>): Response<Void>

    suspend fun getPostsPaginated(
        userId: Long,
        page: Int,
        size: Int,
        sort: String?,
        direction: String?
    ): Response<PageResponse<Post>>
}
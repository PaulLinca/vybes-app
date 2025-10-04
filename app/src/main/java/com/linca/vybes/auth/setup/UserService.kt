package com.linca.vybes.auth.setup

import com.linca.vybes.model.Post
import com.linca.vybes.network.request.ProfilePictureRequest
import com.linca.vybes.network.response.MediaItem
import com.linca.vybes.network.response.PageResponse
import com.linca.vybes.network.response.UserResponse

import retrofit2.Response

interface UserService {
    suspend fun setupUsername(username: String): Response<UserResponse>

    suspend fun setProfilePicture(request: ProfilePictureRequest): Response<UserResponse>

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
package com.linca.vybes.auth.setup

import com.linca.vybes.model.Post
import com.linca.vybes.network.VybesApiClient
import com.linca.vybes.network.request.ProfilePictureRequest
import com.linca.vybes.network.request.SetFavoritesRequest
import com.linca.vybes.network.request.UsernameSetupRequest
import com.linca.vybes.network.response.MediaItem
import com.linca.vybes.network.response.PageResponse
import com.linca.vybes.network.response.UserResponse
import retrofit2.Response

class VybesUserService(private val vybesApiClient: VybesApiClient) : UserService {
    override suspend fun setupUsername(username: String): Response<UserResponse> {
        return vybesApiClient.setupUsername(
            UsernameSetupRequest(username)
        )
    }

    override suspend fun setProfilePicture(request: ProfilePictureRequest): Response<UserResponse> {
        return vybesApiClient.uploadProfilePicture(request)
    }

    override suspend fun getUser(username: String): Response<UserResponse> {
        return vybesApiClient.getUser(username)
    }

    override suspend fun setFavoriteArtists(artists: List<MediaItem>): Response<Void> {
        return vybesApiClient.setFavorites(
            SetFavoritesRequest(
                artistIds = artists.map { a -> a.spotifyId.orEmpty() },
                albumIds = null
            )
        )
    }

    override suspend fun setFavoriteAlbums(albums: List<MediaItem>): Response<Void> {
        return vybesApiClient.setFavorites(
            SetFavoritesRequest(
                artistIds = null,
                albumIds = albums.map { a -> a.spotifyId.orEmpty() }
            )
        )
    }

    override suspend fun getPostsPaginated(
        userId: Long,
        page: Int,
        size: Int,
        sort: String?,
        direction: String?
    ): Response<PageResponse<Post>> {
        return vybesApiClient.getUserPosts(userId, page, size, sort, direction)
    }
}
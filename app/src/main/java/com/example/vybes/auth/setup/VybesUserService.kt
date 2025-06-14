package com.example.vybes.auth.setup

import com.example.vybes.auth.model.MediaItem
import com.example.vybes.auth.model.SetFavoritesRequest
import com.example.vybes.auth.model.UserResponse
import com.example.vybes.auth.model.UsernameSetupRequest
import com.example.vybes.network.VybesApiClient
import okhttp3.MultipartBody
import retrofit2.Response

class VybesUserService(private val vybesApiClient: VybesApiClient) : UserService {
    override suspend fun setupUsername(username: String): Response<UserResponse> {
        return vybesApiClient.setupUsername(
            UsernameSetupRequest(username)
        )
    }

    override suspend fun setupProfilePicture(image: MultipartBody.Part): Response<UserResponse> {
        return vybesApiClient.uploadProfilePicture(image)
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
}
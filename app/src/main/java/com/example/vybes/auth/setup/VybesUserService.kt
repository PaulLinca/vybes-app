package com.example.vybes.auth.setup

import android.util.Log
import com.example.vybes.auth.model.MediaItem
import com.example.vybes.auth.model.SetFavoritesRequest
import com.example.vybes.auth.model.UserResponse
import com.example.vybes.auth.model.UsernameSetupRequest
import com.example.vybes.network.VybesApiClient
import retrofit2.Response

class VybesUserService(private val vybesApiClient: VybesApiClient) : UserService {
    override suspend fun setupUsername(username: String): Response<UserResponse> {
        return vybesApiClient.setupUsername(
            UsernameSetupRequest(username)
        )
    }

    override suspend fun getUser(username: String): Response<UserResponse> {
        return vybesApiClient.getUser(username)
    }

    override suspend fun setFavoriteArtists(artists: List<MediaItem>): Response<Void> {
        Log.e("WTF", artists.map { a -> a.spotifyId }.joinToString { ", " })
        return vybesApiClient.setFavorites(
            SetFavoritesRequest(
                artistIds = artists.map { a -> a.spotifyId },
                albumIds = null
            )
        )
    }

    override suspend fun setFavoriteAlbums(albums: List<MediaItem>): Response<Void> {
        return vybesApiClient.setFavorites(
            SetFavoritesRequest(
                artistIds = null,
                albumIds = albums.map { a -> a.spotifyId }
            )
        )
    }
}
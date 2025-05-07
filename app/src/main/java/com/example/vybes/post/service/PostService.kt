package com.example.vybes.post.service

import com.example.vybes.add.model.network.AlbumSearchResult
import com.example.vybes.add.model.network.ArtistSearchResult
import com.example.vybes.add.model.network.TrackSearchResult
import com.example.vybes.post.model.Comment
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.network.LikeResponse
import com.example.vybes.post.model.network.PageResponse
import retrofit2.Response

interface PostService {
    suspend fun getAllVybes(): Response<List<Vybe>>

    suspend fun getVybesPaginated(
        page: Int,
        size: Int,
        sort: String? = "postedDate",
        direction: String? = "DESC"
    ): Response<PageResponse<Vybe>>

    suspend fun getVybe(id: Long): Response<Vybe>

    suspend fun postVybe(id: String, description: String): Response<Vybe>

    suspend fun getAllLikesOnVybe(vybeId: Long): Response<List<LikeResponse>>

    suspend fun likeVybe(vybeId: Long): Response<LikeResponse>

    suspend fun unlikeVybe(vybeId: Long): Response<LikeResponse>

    suspend fun getAllComments(vybeId: Long): Response<List<Comment>>

    suspend fun likeComment(vybeId: Long, commentId: Long): Response<LikeResponse>

    suspend fun unlikeComment(vybeId: Long, commentId: Long): Response<LikeResponse>

    suspend fun addComment(vybeId: Long, text: String): Response<Comment>

    suspend fun deleteComment(vybeId: Long, commentId: Long): Response<Comment>

    suspend fun searchTrack(query: String): Response<List<TrackSearchResult>>

    suspend fun searchArtist(query: String): Response<List<ArtistSearchResult>>

    suspend fun searchAlbum(query: String): Response<List<AlbumSearchResult>>
}
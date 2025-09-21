package com.linca.vybes.post.service

import com.linca.vybes.model.AlbumReview
import com.linca.vybes.model.AlbumSearchResult
import com.linca.vybes.model.ArtistSearchResult
import com.linca.vybes.model.Challenge
import com.linca.vybes.model.Comment
import com.linca.vybes.model.Post
import com.linca.vybes.model.TrackSearchResult
import com.linca.vybes.model.Vybe
import com.linca.vybes.network.request.CreateAlbumReviewRequest
import com.linca.vybes.network.response.Album
import com.linca.vybes.network.response.LikeResponse
import com.linca.vybes.network.response.PageResponse
import retrofit2.Response

interface PostService {
    suspend fun getAllVybes(): Response<List<Vybe>>

    suspend fun getPostsPaginated(
        page: Int,
        size: Int,
        sort: String? = "postedDate",
        direction: String? = "DESC"
    ): Response<PageResponse<Post>>

    suspend fun getFeaturedChallenge(): Response<Challenge?>

    suspend fun getVybe(id: Long): Response<Vybe>

    suspend fun getAlbumReview(id: Long): Response<AlbumReview>

    suspend fun getAlbumReviewsBySpotifyId(albumSpotifyId: String): Response<List<AlbumReview>>

    suspend fun postVybe(id: String, description: String): Response<Vybe>

    suspend fun postAlbumReview(request: CreateAlbumReviewRequest): Response<Void>

    suspend fun getAllLikesOnVybe(vybeId: Long): Response<List<LikeResponse>>

    suspend fun likePost(postId: Long): Response<LikeResponse>

    suspend fun unlikePost(postId: Long): Response<LikeResponse>

    suspend fun getAllComments(vybeId: Long): Response<List<Comment>>

    suspend fun likeComment(vybeId: Long, commentId: Long): Response<LikeResponse>

    suspend fun unlikeComment(vybeId: Long, commentId: Long): Response<LikeResponse>

    suspend fun addComment(vybeId: Long, text: String): Response<Comment>

    suspend fun deleteComment(vybeId: Long, commentId: Long): Response<Comment>

    suspend fun searchTrack(query: String): Response<List<TrackSearchResult>>

    suspend fun searchArtist(query: String): Response<List<ArtistSearchResult>>

    suspend fun searchAlbum(query: String): Response<List<AlbumSearchResult>>

    suspend fun getAlbum(id: String): Response<Album>

    suspend fun deletePost(postId: Long): Response<Void>

    suspend fun voteChallengeOption(id: Long, optionId: Long): Response<Challenge>
}
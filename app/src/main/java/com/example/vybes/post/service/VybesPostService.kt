package com.example.vybes.post.service

import com.example.vybes.model.AlbumSearchResult
import com.example.vybes.model.ArtistSearchResult
import com.example.vybes.model.TrackSearchResult
import com.example.vybes.network.response.Album
import com.example.vybes.network.VybesApiClient
import com.example.vybes.model.AlbumReview
import com.example.vybes.model.Challenge
import com.example.vybes.model.Comment
import com.example.vybes.model.Post
import com.example.vybes.model.Vybe
import com.example.vybes.network.request.AddCommentRequest
import com.example.vybes.network.request.CreateAlbumReviewRequest
import com.example.vybes.network.response.LikeResponse
import com.example.vybes.network.response.PageResponse
import retrofit2.Response
import java.time.ZonedDateTime
import javax.inject.Inject

class VybesPostService @Inject constructor(
    private val vybesApiClient: VybesApiClient
) : PostService {

    override suspend fun getAllVybes(): Response<List<Vybe>> {
        return vybesApiClient.getAllPosts()
    }

    override suspend fun getPostsPaginated(
        page: Int,
        size: Int,
        sort: String?,
        direction: String?
    ): Response<PageResponse<Post>> {
        return vybesApiClient.getPostsPaginated(page, size, sort, direction)
    }

    override suspend fun getFeaturedChallenge(): Response<Challenge?> {
        return vybesApiClient.getFeaturedChallenge()
    }

    override suspend fun getVybe(id: Long): Response<Vybe> {
        return vybesApiClient.getVybeById(id)
    }

    override suspend fun getAlbumReview(id: Long): Response<AlbumReview> {
        return vybesApiClient.getAlbumReviewById(id)
    }

    override suspend fun getAlbumReviewsBySpotifyId(albumSpotifyId: String): Response<List<AlbumReview>> {
        return vybesApiClient.getAlbumReviewBySpotifyId(albumSpotifyId)
    }

    override suspend fun postVybe(id: String, description: String): Response<Vybe> {
        return vybesApiClient.post(PostRequest(id, ZonedDateTime.now(), description))
    }

    override suspend fun postAlbumReview(request: CreateAlbumReviewRequest): Response<Void> {
        return vybesApiClient.postAlbumReview(request)
    }

    override suspend fun getAllLikesOnVybe(vybeId: Long): Response<List<LikeResponse>> {
        return vybesApiClient.getLikesByPostId(vybeId)
    }

    override suspend fun deletePost(postId: Long): Response<Void> {
        return vybesApiClient.deletePost(postId)
    }

    override suspend fun voteChallengeOption(id: Long, optionId: Long): Response<Challenge> {
        return vybesApiClient.voteChallengeOption(id, optionId)
    }

    override suspend fun likePost(postId: Long): Response<LikeResponse> {
        return vybesApiClient.likePost(postId)
    }

    override suspend fun unlikePost(postId: Long): Response<LikeResponse> {
        return vybesApiClient.unlikePost(postId)
    }

    override suspend fun likeComment(vybeId: Long, commentId: Long): Response<LikeResponse> {
        return vybesApiClient.likeComment(vybeId, commentId)
    }

    override suspend fun unlikeComment(vybeId: Long, commentId: Long): Response<LikeResponse> {
        return vybesApiClient.unlikeComment(vybeId, commentId)
    }

    override suspend fun addComment(vybeId: Long, text: String): Response<Comment> {
        return vybesApiClient.addComment(
            vybeId,
            addCommentRequest = AddCommentRequest(text, ZonedDateTime.now())
        )
    }

    override suspend fun deleteComment(vybeId: Long, commentId: Long): Response<Comment> {
        return vybesApiClient.deleteComment(vybeId, commentId)
    }

    override suspend fun getAllComments(vybeId: Long): Response<List<Comment>> {
        return vybesApiClient.getCommentsByPostId(
            vybeId
        )
    }

    override suspend fun searchTrack(query: String): Response<List<TrackSearchResult>> {
        return vybesApiClient.getTrackSearchResults(query)
    }

    override suspend fun searchArtist(query: String): Response<List<ArtistSearchResult>> {
        return vybesApiClient.getArtistSearchResults(query)
    }

    override suspend fun searchAlbum(query: String): Response<List<AlbumSearchResult>> {
        return vybesApiClient.getAlbumSearchResults(query)
    }

    override suspend fun getAlbum(id: String): Response<Album> {
        return vybesApiClient.getAlbum(id)
    }
}
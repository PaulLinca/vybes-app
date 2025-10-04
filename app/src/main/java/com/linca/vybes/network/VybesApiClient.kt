package com.linca.vybes.network

import com.linca.vybes.model.AlbumReview
import com.linca.vybes.model.AlbumSearchResult
import com.linca.vybes.model.ArtistSearchResult
import com.linca.vybes.model.Challenge
import com.linca.vybes.model.Comment
import com.linca.vybes.model.Post
import com.linca.vybes.model.TrackSearchResult
import com.linca.vybes.model.Vybe
import com.linca.vybes.network.request.AddCommentRequest
import com.linca.vybes.network.request.CreateAlbumReviewRequest
import com.linca.vybes.network.request.FeedbackRequest
import com.linca.vybes.network.request.ProfilePictureRequest
import com.linca.vybes.network.request.SetFavoritesRequest
import com.linca.vybes.network.request.UsernameSetupRequest
import com.linca.vybes.network.response.Album
import com.linca.vybes.network.response.LikeResponse
import com.linca.vybes.network.response.LoginResponse
import com.linca.vybes.network.response.PageResponse
import com.linca.vybes.network.response.UserResponse
import com.linca.vybes.post.service.PostRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VybesApiClient {

    @POST("api/auth/authenticate")
    suspend fun authenticate(): Response<LoginResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Void>

    @POST("api/user/setUsername")
    suspend fun setupUsername(@Body loginRequest: UsernameSetupRequest): Response<UserResponse>

    @POST("api/user/setProfilePicture")
    suspend fun uploadProfilePicture(
        @Body request: ProfilePictureRequest
    ): Response<UserResponse>

    @POST("api/user/updateFavorites")
    suspend fun setFavorites(@Body request: SetFavoritesRequest): Response<Void>

    @GET("api/user")
    suspend fun getUser(@Query("username") username: String): Response<UserResponse>

    @GET("api/user/{userId}/posts")
    suspend fun getUserPosts(
        @Path("userId") userId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String? = "createdAt",
        @Query("direction") direction: String? = "DESC"
    ): Response<PageResponse<Post>>

    @POST("api/auth/refresh")
    suspend fun refresh(@Header("Authorization") refreshToken: String): Response<LoginResponse>

    @GET("api/feed/all")
    suspend fun getAllPosts(): Response<List<Vybe>>

    @GET("api/feed")
    suspend fun getPostsPaginated(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String? = "createdAt",
        @Query("direction") direction: String? = "DESC"
    ): Response<PageResponse<Post>>

    @GET("api/featured-challenges/current")
    suspend fun getFeaturedChallenge(): Response<Challenge?>

    @POST("api/vybes/post")
    suspend fun post(@Body postRequest: PostRequest): Response<Vybe>

    @POST("api/album-reviews/post")
    suspend fun postAlbumReview(@Body postRequest: CreateAlbumReviewRequest): Response<Void>

    @GET("api/vybes/{vybeId}")
    suspend fun getVybeById(@Path("vybeId") vybeId: Long): Response<Vybe>

    @GET("api/album-reviews/{albumReviewId}")
    suspend fun getAlbumReviewById(@Path("albumReviewId") albumReviewId: Long): Response<AlbumReview>

    @GET("api/album-reviews")
    suspend fun getAlbumReviewBySpotifyId(
        @Query("albumSpotifyId") albumSpotifyId: String
    ): Response<List<AlbumReview>>

    @DELETE("api/posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: Long
    ): Response<Void>

    @POST("api/posts/{postId}/likes")
    suspend fun likePost(@Path("postId") postId: Long): Response<LikeResponse>

    @DELETE("api/posts/{postId}/likes")
    suspend fun unlikePost(@Path("postId") postId: Long): Response<LikeResponse>

    @GET("api/posts/{postId}/comments")
    suspend fun getLikesByPostId(
        @Path("postId") postId: Long
    ): Response<List<LikeResponse>>

    @GET("api/posts/{postId}/comments")
    suspend fun getCommentsByPostId(
        @Path("postId") postId: Long
    ): Response<List<Comment>>

    @POST("api/posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: Long,
        @Body addCommentRequest: AddCommentRequest
    ): Response<Comment>

    @DELETE("api/posts/{postId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long,
    ): Response<Comment>

    @POST("api/posts/{postId}/comments/{commentId}/likes")
    suspend fun likeComment(
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long,
    ): Response<LikeResponse>

    @DELETE("api/posts/{postId}/comments/{commentId}/likes")
    suspend fun unlikeComment(
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long
    ): Response<LikeResponse>

    @GET("api/music/search/track")
    suspend fun getTrackSearchResults(
        @Query("query") query: String
    ): Response<List<TrackSearchResult>>

    @GET("api/music/search/artist")
    suspend fun getArtistSearchResults(
        @Query("query") query: String
    ): Response<List<ArtistSearchResult>>

    @GET("api/music/search/album")
    suspend fun getAlbumSearchResults(
        @Query("query") query: String
    ): Response<List<AlbumSearchResult>>

    @GET("api/music/album")
    suspend fun getAlbum(
        @Query("id") id: String
    ): Response<Album>

    @POST("api/feedback/submit")
    suspend fun submitFeedback(
        @Body feedbackRequest: FeedbackRequest
    ): Response<Void>

    @POST("api/challenges/{id}/options/{optionId}/vote")
    suspend fun voteChallengeOption(
        @Path("id") id: Long,
        @Path("optionId") optionId: Long
    ): Response<Challenge>
}
package com.example.vybes.network

import com.example.vybes.add.model.AlbumSearchResult
import com.example.vybes.add.model.ArtistSearchResult
import com.example.vybes.add.model.TrackSearchResult
import com.example.vybes.auth.model.Album
import com.example.vybes.auth.model.AuthRequest
import com.example.vybes.auth.model.LoginResponse
import com.example.vybes.auth.model.RegisterResponse
import com.example.vybes.auth.model.SetFavoritesRequest
import com.example.vybes.auth.model.UserResponse
import com.example.vybes.auth.model.UsernameSetupRequest
import com.example.vybes.feedback.model.FeedbackRequest
import com.example.vybes.post.model.AlbumReview
import com.example.vybes.post.model.Comment
import com.example.vybes.post.model.Post
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.network.AddCommentRequest
import com.example.vybes.post.model.network.CreateAlbumReviewRequest
import com.example.vybes.post.model.network.LikeResponse
import com.example.vybes.post.model.network.PageResponse
import com.example.vybes.post.service.PostRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface VybesApiClient {

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: AuthRequest): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: AuthRequest): Response<LoginResponse>

    @POST("api/user/setUsername")
    suspend fun setupUsername(@Body loginRequest: UsernameSetupRequest): Response<UserResponse>

    @Multipart
    @POST("api/user/setProfilePicture")
    suspend fun uploadProfilePicture(
        @Part image: MultipartBody.Part
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
}
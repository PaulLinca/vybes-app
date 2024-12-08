package com.example.vybes.network

import com.example.vybes.add.model.network.TrackSearchResult
import com.example.vybes.auth.model.AuthRequest
import com.example.vybes.auth.model.LoginResponse
import com.example.vybes.auth.model.RegisterResponse
import com.example.vybes.post.model.Comment
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.network.AddCommentRequest
import com.example.vybes.post.model.network.LikeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VybesApiClient {

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: AuthRequest): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: AuthRequest): Response<LoginResponse>

    @POST("api/auth/refresh")
    suspend fun refresh(@Header("Authorization") refreshToken: String): Response<LoginResponse>

    @GET("api/vybes/findAll")
    suspend fun getAllPosts(): Response<List<Vybe>>

    @GET("api/vybes/post")
    suspend fun post(): Response<Vybe>

    @GET("api/vybes/{vybeId}")
    suspend fun getPostById(@Path("vybeId") vybeId: Long): Response<Vybe>

    @POST("api/vybes/{vybeId}/likes")
    suspend fun likeVybe(@Path("vybeId") vybeId: Long): Response<LikeResponse>

    @DELETE("api/vybes/{vybeId}/likes")
    suspend fun unlikeVybe(@Path("vybeId") vybeId: Long): Response<LikeResponse>

    @GET("api/vybes/{vybeId}/comments")
    suspend fun getLikesByVybeId(
        @Path("vybeId") vybeId: Long
    ): Response<List<LikeResponse>>

    @GET("api/vybes/{vybeId}/comments")
    suspend fun getCommentsByVybeId(
        @Path("vybeId") vybeId: Long
    ): Response<List<Comment>>

    @POST("api/vybes/{vybeId}/comments")
    suspend fun addComment(
        @Path("vybeId") vybeId: Long,
        @Body addCommentRequest: AddCommentRequest
    ): Response<Comment>

    @DELETE("api/vybes/{vybeId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("vybeId") vybeId: Long,
        @Path("commentId") commentId: Long,
    ): Response<Comment>

    @POST("api/vybes/{vybeId}/comments/{commentId}/likes")
    suspend fun likeComment(
        @Path("vybeId") vybeId: Long,
        @Path("commentId") commentId: Long,
    ): Response<LikeResponse>

    @DELETE("api/vybes/{vybeId}/comments/{commentId}/likes")
    suspend fun unlikeComment(
        @Path("vybeId") vybeId: Long,
        @Path("commentId") commentId: Long
    ): Response<LikeResponse>

    @GET("api/spotify")
    suspend fun getSearchResults(
        @Query("query") query: String
    ): Response<List<TrackSearchResult>>
}
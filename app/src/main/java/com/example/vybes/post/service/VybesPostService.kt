package com.example.vybes.post.service

import com.example.vybes.network.VybesApiClient
import com.example.vybes.post.model.Comment
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.network.AddCommentRequest
import com.example.vybes.post.model.network.LikeResponse
import retrofit2.Response
import java.time.ZonedDateTime
import javax.inject.Inject

class VybesPostService @Inject constructor(
    private val vybesApiClient: VybesApiClient
) :
    PostService {

    override suspend fun getAllVybes(): Response<List<Vybe>> {
        return vybesApiClient.getAllPosts()
    }

    override suspend fun getVybe(id: Long): Response<Vybe> {
        return vybesApiClient.getPostById(id)
    }

    override suspend fun getAllLikesOnVybe(vybeId: Long): Response<List<LikeResponse>> {
        return vybesApiClient.getLikesByVybeId(vybeId)
    }

    override suspend fun likeVybe(vybeId: Long): Response<LikeResponse> {
        return vybesApiClient.likeVybe(vybeId)
    }

    override suspend fun unlikeVybe(vybeId: Long): Response<LikeResponse> {
        return vybesApiClient.unlikeVybe(vybeId)
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
        return vybesApiClient.getCommentsByVybeId(
            vybeId
        )
    }
}
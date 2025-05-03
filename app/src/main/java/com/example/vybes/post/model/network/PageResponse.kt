package com.example.vybes.post.model.network

data class PageResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)
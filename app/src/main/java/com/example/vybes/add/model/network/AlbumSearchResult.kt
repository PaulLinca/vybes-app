package com.example.vybes.add.model.network

import kotlinx.serialization.Serializable

@Serializable
data class AlbumSearchResult(
    val id: String,
    val name: String,
    val imageUrl: String
)
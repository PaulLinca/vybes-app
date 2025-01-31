package com.example.vybes.add.model.network

import kotlinx.serialization.Serializable

@Serializable
data class TrackSearchResult(
    val id: String,
    val name: String,
    val artist: String,
    val imageUrl: String
)
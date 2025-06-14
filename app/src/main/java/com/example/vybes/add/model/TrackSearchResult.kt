package com.example.vybes.add.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackSearchResult(
    override val id: String,
    override val name: String,
    override val artists: List<ArtistSearchResult>,
    override val imageUrl: String
) : SearchResultItem

interface SearchResultItem {
    val id: String
    val name: String
    val artists: List<ArtistSearchResult>
    val imageUrl: String
}
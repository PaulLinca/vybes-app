package com.example.vybes.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackSearchResult(
    override val spotifyId: String,
    override val name: String,
    override val artists: List<ArtistSearchResult>,
    override val imageUrl: String
) : SearchResultItem

interface SearchResultItem {
    val spotifyId: String
    val name: String
    val artists: List<ArtistSearchResult>
    val imageUrl: String
}
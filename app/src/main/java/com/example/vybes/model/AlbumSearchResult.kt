package com.example.vybes.model

import com.example.vybes.network.response.MediaItem
import kotlinx.serialization.Serializable

@Serializable
data class AlbumSearchResult(
    override val name: String,
    override val imageUrl: String,
    override val spotifyId: String,
    override val artists: List<ArtistSearchResult>
) : MediaItem, SearchResultItem
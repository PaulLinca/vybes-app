package com.example.vybes.add.model.network

import com.example.vybes.auth.model.MediaItem
import kotlinx.serialization.Serializable

@Serializable
data class ArtistSearchResult(
    override val name: String,
    override val imageUrl: String,
    override val spotifyId: String
) : MediaItem
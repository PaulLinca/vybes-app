package com.example.vybes.add.model.network

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.vybes.auth.model.MediaItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ArtistSearchResult(
    override val name: String,
    override val imageUrl: String?,
    override val spotifyId: String?
) : MediaItem

val ArtistSearchResultNavType =
    object : NavType<List<ArtistSearchResult>>(isNullableAllowed = true) {
        override fun get(bundle: Bundle, key: String): List<ArtistSearchResult> {
            return bundle.getSerializable(key) as List<ArtistSearchResult>
        }

        override fun parseValue(value: String): List<ArtistSearchResult> {
            return Json.decodeFromString(value)
        }

        override fun serializeAsValue(value: List<ArtistSearchResult>): String {
            return Uri.encode(Json.encodeToString(value))
        }


        override fun put(bundle: Bundle, key: String, value: List<ArtistSearchResult>) {
            bundle.putSerializable(key, value as java.io.Serializable)
        }

    }
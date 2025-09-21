package com.linca.vybes.network.adapters

import com.linca.vybes.model.AlbumReview
import com.linca.vybes.model.Post
import com.linca.vybes.model.Vybe
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class PostDeserializer : JsonDeserializer<Post> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Post {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString

        return when (type) {
            "VYBE" -> context.deserialize<Vybe>(json, Vybe::class.java)
            "ALBUM_REVIEW" -> context.deserialize<AlbumReview>(json, AlbumReview::class.java)
            else -> throw JsonParseException("Unknown post type: $type")
        }
    }
}
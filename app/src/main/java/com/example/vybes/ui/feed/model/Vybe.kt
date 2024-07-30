package com.example.vybes.ui.feed.model

import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Vybe(
    val id: Int,
    val songName: String,
    val spotifyTrackId: String,
    val spotifyArtistNames: List<String>,
    val spotifyArtistIds: List<String>,
    val spotifyAlbumId: String,
    val imageUrl: String,
    val postedDate: String,
    val vybesUser: String
)

val vybes = listOf(
    Vybe(
        1,
        "Kody Blu 31",
        "6ZGoFntcQUWTPGGQPQmecY",
        listOf("JID"),
        listOf("6U3ybJ9UHNKEdsH7ktGBZ7"),
        "4rJDCELWL0fjdmN9Gn4f4g",
        "https://i.scdn.co/image/ab67616d0000b273cae6e44dcc84e2035c3ad092",
        ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
        "John Doe"
    ),
    Vybe(
        2,
        "Lavish",
        "1KFWgQTw3EMTQebaaepVBI",
        listOf("Twenty One Pilots"),
        listOf("3YQKmKGau1PzlVlkL1iodx"),
        "1KFWgQTw3EMTQebaaepVBI",
        "https://i.scdn.co/image/ab67616d0000b2739cf15c7323fb85b7112197d5",
        ZonedDateTime.now().minusMinutes(44).format(DateTimeFormatter.ofPattern("HH:mm:ss")),
        "Matt Smith"
    ),
    Vybe(
        3,
        "Runaway",
        "3DK6m7It6Pw857FcQftMds",
        listOf("Kanye West", "Pusha T"),
        listOf("5K4W6rqBFWDnAN6FQUkS6x", "0ONHkAv9pCAFxb0zJwDNTy"),
        "4rJDCELWL0fjdmN9Gn4f4g",
        "https://i.scdn.co/image/ab67616d0000b273d9194aa18fa4c9362b47464f",
        ZonedDateTime.now().minusMinutes(13).format(DateTimeFormatter.ofPattern("HH:mm:ss")),
        "Cristian Pin"
    ),
    Vybe(
        4,
        "Otherside",
        "64BbK9SFKH2jk86U3dGj2P",
        listOf("Red Hot Chili Peppers"),
        listOf("0L8ExT028jH3ddEcZwqJJ5"),
        "2Y9IRtehByVkegoD7TcLfi",
        "https://i.scdn.co/image/ab67616d0000b27394d08ab63e57b0cae74e8595",
        ZonedDateTime.now().minusMinutes(200).format(DateTimeFormatter.ofPattern("HH:mm:ss")),
        "Clíona Murphy"
    ),
    Vybe(
        5,
        "Father Time (feat. Sampha)",
        "6ZGoFntcQUWTPGGQPQmecY",
        listOf("Kendrick Lamar"),
        listOf("2YZyLoL8N0Wb9xBt1NhZWg"),
        "79ONNoS4M9tfIA1mYLBYVX",
        "https://i.scdn.co/image/ab67616d0000b2732e02117d76426a08ac7c174f",
        ZonedDateTime.now().minusMinutes(34).format(DateTimeFormatter.ofPattern("HH:mm:ss")),
        "John Travolta"
    ),
    Vybe(
        6,
        "Life Of The Party (with André 3000)",
        "2yBb1CRq98SSWRuDSn36SZ",
        listOf("Kanye West", "André 3000"),
        listOf("5K4W6rqBFWDnAN6FQUkS6x", "74V3dE1a51skRkdII8y2C6"),
        "79ONNoS4M9tfIA1mYLBYVX",
        "https://i.scdn.co/image/ab67616d0000b273c5663e50de353981ed2b1a37",
        ZonedDateTime.now().minusMinutes(34).format(DateTimeFormatter.ofPattern("HH:mm:ss")),
        "Ion Creanga"
    ),
    Vybe(
        7,
        "Houdini",
        "2HYFX63wP3otVIvopRS99Z",
        listOf("Eminem"),
        listOf("7dGJo4pcD2V6oG8kP0tJRR"),
        "79ONNoS4M9tfIA1mYLBYVX",
        "https://i.scdn.co/image/ab67616d0000b273810603c94c9246379604cf1a",
        ZonedDateTime.now().minusMinutes(233).format(DateTimeFormatter.ofPattern("HH:mm:ss")),
        "Fiona Apple"
    ),
)

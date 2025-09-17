package com.example.vybes.model

import com.example.vybes.network.response.Album
import com.example.vybes.network.response.Artist
import com.example.vybes.network.response.Track

data class ChallengeOption(
    val id: Long,
    val album: Album? = null,
    val track: Track? = null,
    val artist: Artist? = null,
    val customText: String? = null,
    val votesCount: Int = 0,
    val votedByUser: Boolean = false
)
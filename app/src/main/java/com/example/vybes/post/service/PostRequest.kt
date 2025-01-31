package com.example.vybes.post.service

import java.time.ZonedDateTime

data class PostRequest(val spotifyTrackId: String, val postedDate: ZonedDateTime)

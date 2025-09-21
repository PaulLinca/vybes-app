package com.linca.vybes.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Long,
    val username: String,
    val profilePictureUrl: String? = null
)

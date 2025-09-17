package com.example.vybes.model

import java.time.ZonedDateTime

data class Challenge(
    val id: Long,
    val createdBy: User,
    val createdAt: ZonedDateTime? = null,
    val question: String,
    val type: String,
    val answerType: String,
    val options: List<ChallengeOption>? = null,
    val submissions: List<ChallengeSubmission>? = null,
)
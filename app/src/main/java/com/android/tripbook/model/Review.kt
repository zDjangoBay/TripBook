package com.android.tripbook.model

import java.time.LocalDateTime

data class Review(
    val id: String = "",
    val agencyId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
)

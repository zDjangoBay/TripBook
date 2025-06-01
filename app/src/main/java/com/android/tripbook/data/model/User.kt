package com.android.tripbook.data.model

import java.util.Date

data class User(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val profileImage: String?,
    val bio: String?,
    val createdAt: Date,
    val updatedAt: Date
)

package com.android.tripbook.userprofile.data.models

data class AuthToken(
    val token: String,
    val expiresAt: Long,
    val refreshToken: String
)

package com.android.tripbook

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val profilePicture: String?,
    val createdAt: String
)
package com.android.tripbook.model

data class User(
    val username: String,
    val avatarUrl: String? = null,
    val displayName: String? = null
)

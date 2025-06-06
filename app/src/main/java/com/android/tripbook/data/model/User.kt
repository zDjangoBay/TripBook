package com.android.tripbook.data.model


data class User(
    val id: String,
    val username: String,
    val email: String,
    val profilePictureUrl: String?,
    val bio: String?,
    val followers: List<String>,
    val following: List<String>,
    val savedPostIds: List<String>,
    val pinnedPostIds: List<String>
)
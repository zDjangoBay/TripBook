package com.android.tripbook.userprofile.data.models

data class ProfileUpdateRequest(
    val fullName: String,
    val phoneNumber: String?,
    val email: String,
    val bio: String?,
    val avatarUrl: String?
)

package com.tripbook.userprofile.data.models

data class UserProfile(
    val userId: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val dateJoined: Long
)

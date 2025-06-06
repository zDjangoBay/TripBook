package com.tripbook.data.model

data class TravelerProfile(
    val uid: String = "",
    val username: String = "",
    val bio: String = "",
    val interests: List<String> = emptyList(),
    val profileImageUrl: String = "" // For later image uploads
)

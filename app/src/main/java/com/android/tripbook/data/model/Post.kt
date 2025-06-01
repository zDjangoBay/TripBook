package com.android.tripbook.data.model

import java.util.Date

data class Post(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val images: List<String>,
    val location: String,
    val latitude: Double?,
    val longitude: Double?,
    val tags: List<String>,
    val agencyId: String?,
    val likes: Int,
    val comments: Int,
    val createdAt: Date,
    val updatedAt: Date
)

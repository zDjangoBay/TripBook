package com.android.tripcatalog.models

import kotlinx.serialization.*


@Serializable
data class Review(
    val tripId: Int,
    val username: String,
    val comment: String,
    val images: List<String>?
)

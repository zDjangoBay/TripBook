package com.android.tripbook.posts.model

data class ImageModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val uri: String,
    val path: String? = null,
    val isUploaded: Boolean = false,
    val uploadUrl: String? = null
)

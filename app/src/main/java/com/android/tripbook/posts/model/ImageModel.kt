package com.android.tripbook.posts.model

data class ImageModel(
    val id: String = "",
    val uri: String,
    val isUploaded: Boolean = false,
    val uploadProgress: Float = 0f,
    val caption: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val sizeBytes: Long? = null,
    val mimeType: String? = null
)

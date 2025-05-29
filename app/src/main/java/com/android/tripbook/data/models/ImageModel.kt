package com.android.tripbook.data.models

import android.net.Uri

data class ImageModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val uri: Uri? = null,
    val path: String = "",
    val caption: String = "",
    val isUploaded: Boolean = false
)
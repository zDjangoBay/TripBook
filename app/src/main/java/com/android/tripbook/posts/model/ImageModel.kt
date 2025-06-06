package com.android.tripbook.posts.model

import java.util.UUID

data class ImageModel(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val caption: String = "",
    val isLocal: Boolean = true
)
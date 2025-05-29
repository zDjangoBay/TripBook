package com.android.tripbook.data.models

import java.util.Date
import java.util.UUID

data class PostModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val images: List<ImageModel> = emptyList(),
    val location: String = "",
    val tags: List<TagModel> = emptyList(),
    val hashtags: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val isValid: Boolean = title.isNotBlank() && description.isNotBlank()
)
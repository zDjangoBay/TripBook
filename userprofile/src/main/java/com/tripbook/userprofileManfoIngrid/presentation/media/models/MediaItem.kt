package com.tripbook.userprofileManfoIngrid.presentation.media.models

data class MediaItem(
    val id: String,
    val name: String,
    val url: String,
    val type: MediaType
)

enum class MediaType {
    IMAGE,
    VIDEO
}

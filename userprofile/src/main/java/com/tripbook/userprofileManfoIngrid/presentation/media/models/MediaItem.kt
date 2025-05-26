package com.tripbook.userprofileManfoIngrid.presentation.media.models

import java.util.Date

data class MediaItem(
    val id: String,
    val name: String,
    val type: MediaType,
    val url: String,
    val createdDate: Date,
    val modifiedDate: Date,
    val size: Long
)

enum class MediaType {
    PHOTO, VIDEO
}

enum class MediaFilter {
    ALL, PHOTOS, VIDEOS
}

enum class SortOption {
    CREATION_DATE, MODIFICATION_DATE, NAME
}
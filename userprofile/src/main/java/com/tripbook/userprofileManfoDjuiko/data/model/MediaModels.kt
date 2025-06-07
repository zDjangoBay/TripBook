package com.tripbook.userprofileManfoDjuiko.data.model

import android.net.Uri
import java.util.Date

enum class MediaType {
    IMAGE, VIDEO
}

data class MediaItem(
    val id: String,
    val uri: Uri,
    val type: MediaType,
    val name: String,
    val size: Long,
    val dateAdded: Date,
    val duration: Long? = null, // For videos in milliseconds
    val thumbnail: Uri? = null
)

data class MediaFolder(
    val id: String,
    val name: String,
    val items: List<MediaItem>,
    val coverImage: Uri? = null
)

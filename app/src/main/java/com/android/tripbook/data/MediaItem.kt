package com.android.tripbook.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class MediaItem(
    val id: Long,
    val path: String,
    val type: MediaType,
    val dateAdded: Date,
    val size: Long,
    val duration: Long? = null // Only for videos
) : Parcelable

enum class MediaType {
    PHOTO,
    VIDEO
} 
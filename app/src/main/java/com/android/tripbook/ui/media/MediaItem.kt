package com.android.tripbook.ui.media

import android.net.Uri

data class MediaItem(
    val uri: Uri,
    val displayName: String?,
    val mimeType: String?,
    val dateAdded: Long
)

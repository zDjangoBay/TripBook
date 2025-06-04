package com.android.tripbook.model

import java.text.SimpleDateFormat
import java.util.*

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val imageUri: String? = null,
    val timestamp: String = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date()),
    val authorName: String = "You",
    val authorAvatar: String? = null
)
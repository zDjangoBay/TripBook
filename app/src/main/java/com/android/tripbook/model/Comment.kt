package com.android.tripbook.model

import java.text.SimpleDateFormat
import java.util.*

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val imageUri: String? = null,
    val timestamp: String = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date()),
    val authorName: String = "You",
    val authorAvatar: String? = null,
    val reactions: Map<String, List<CommentReaction>> = emptyMap(),
    val parentId: String? = null, // ID of parent comment if this is a reply
    val replies: List<Comment> = emptyList() // List of replies to this comment
)

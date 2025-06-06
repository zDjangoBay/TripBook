package com.android.tripbook.Model

import java.text.SimpleDateFormat
import java.util.*

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val imageUri: String? = null,
    val timestamp: String = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date()),
    val authorName: String = "You",
    val authorAvatar: String? = null,
    val reactions: MutableMap<String, MutableList<CommentReaction>> = mutableMapOf(),
    val parentId: String? = null, // ID of parent comment if this is a reply
    val replies: MutableList<Comment> = mutableListOf() // List of replies to this comment
)

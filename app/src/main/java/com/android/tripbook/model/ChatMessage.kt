
package com.android.tripbook.model

data class ChatMessage(
    val senderId: String = "",
    val receiverId: String = "", // optional for group or DMs
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val seen: Boolean = false
)
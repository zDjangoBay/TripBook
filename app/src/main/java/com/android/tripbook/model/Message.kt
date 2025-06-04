package com.android.tripbook.model

data class Message(
    val senderId: String = "",
    val senderName: String = "",
    val messageText: String = "",
    val timestamp: Long = 0L
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", 0L)
}
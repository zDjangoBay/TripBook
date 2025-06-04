package com.yourpackage.tripbook.model

import com.google.firebase.Timestamp
import java.util.Date

data class Message(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val tripId: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", Timestamp.now(), "")

    // Helper function to get formatted time
    fun getFormattedTime(): String {
        val date = timestamp.toDate()
        val now = Date()
        val diffInMillis = now.time - date.time
        val diffInHours = diffInMillis / (1000 * 60 * 60)

        return when {
            diffInHours < 1 -> "Just now"
            diffInHours < 24 -> "${diffInHours}h ago"
            else -> {
                val diffInDays = diffInHours / 24
                "${diffInDays}d ago"
            }
        }
    }
}
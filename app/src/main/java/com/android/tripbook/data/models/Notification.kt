package com.android.tripbook.data.models

import java.util.Date

data class Notification(
    val id: String,
    val type: NotificationType,
    val message: String,
    val timestamp: Date,
    var isRead: Boolean = false
)

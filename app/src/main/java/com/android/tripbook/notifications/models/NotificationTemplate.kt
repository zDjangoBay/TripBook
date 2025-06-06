package com.android.tripbook.notifications.models

data class NotificationTemplate(
    val title: String,
    val body: String,
    val emailSubject: String,
    val emailBody: String
)
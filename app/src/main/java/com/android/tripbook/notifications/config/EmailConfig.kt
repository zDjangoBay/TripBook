package com.android.tripbook.notifications.config

data class EmailConfig(
    val host: String = "smtp.gmail.com",
    val port: Int = 587,
    val username: String = "tripbook.notifications@gmail.com",
    val password: String = "your_app_password", // À remplacer
    val fromEmail: String = "noreply@tripbook.com"
)
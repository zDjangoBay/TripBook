package com.android.tripbook.notifications.config

data class EmailConfig(
    val host: String = "smtp.gmail.com",
    val port: Int = 587,
    val username: String = "client@gmail.com",
    val password: String = "password", //
    val fromEmail: String = "noreply@tripbook.com"
)
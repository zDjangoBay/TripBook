package com.android.tripbook.data

import java.util.Date

data class Trip(
    val id: Long = 0,
    val title: String,
    val destination: String,
    val startDate: Date,
    val endDate: Date,
    val duration: Int, // in days
    val type: String, // e.g., "Adventure", "Leisure", "Business"
    val description: String? = null,
    val agency: String? = null
)
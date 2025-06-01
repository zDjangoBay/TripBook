package com.android.tripbook.data.model

import java.util.Date

data class TravelAgency(
    val id: String,
    val name: String,
    val description: String,
    val logo: String?,
    val website: String?,
    val contactPhone: String?,
    val contactEmail: String?,
    val createdAt: Date,
    val updatedAt: Date
)

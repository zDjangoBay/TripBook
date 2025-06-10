package com.android.tripbook.data.models

data class TravelAgency(
    val id: String,
    val name: String,
    val contactEmail: String,
    val phoneNumber: String,
    val website: String? = null
)
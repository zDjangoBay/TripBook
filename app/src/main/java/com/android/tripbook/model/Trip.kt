package com.android.tripbook.model

data class Trip(
    val id: Int,
    val title: String,
    val price: Int,
    val location: String,
    val rating: Float
)

package com.android.tripbook.data.models

data class Service(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val price: String,
    val imageUrl: Int,
    val rating: Double = 0.0,
    val agency: String = ""
)
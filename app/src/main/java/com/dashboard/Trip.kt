package com.dashboard

data class Trip(
    val id: String,
    val imageResId: Int,
    val origin: String,
    val destination: String,
    val time: String,
    val price: Double,
    val transportType: String
)

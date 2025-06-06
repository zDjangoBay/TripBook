package com.android.tripbook.Model

import androidx.annotation.DrawableRes

data class BusCompany(
    val id: Int,
    val name: String,
    @DrawableRes val logoRes: Int,
    val rating: Double,
    val totalTrips: Int,
    val startingPrice: Int,
    val amenities: List<String>,
    val description: String,
    val routes: List<String>
)

data class PopularDestination(
    val id: Int,
    val name: String,
    @DrawableRes val imageRes: Int,
    val distance: Int,
    val description: String,
    val averagePrice: Int,
    val estimatedTime: String
)
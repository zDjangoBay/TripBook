package com.android.tripbook.Model

import androidx.annotation.DrawableRes

data class BusCompany(
    val id: Int,
    val name: String,
    @DrawableRes val logoRes: Int,
    val rating: Double,
    val totalTrips: Int,
    val startingPrice: Int, // Price in FCFA
    val amenities: List<String>,
    val description: String,
    val routes: List<String>
)

data class PopularDestination(
    val id: Int,
    val name: String,
    @DrawableRes val imageRes: Int,
    val distance: Int, // Distance in km
    val description: String,
    val averagePrice: Int, // Average price in FCFA
    val estimatedTime: String // Travel time
)
package com.android.tripbook.model
import androidx.annotation.DrawableRes

data class BoatCompany(
    val id: Int,
    val name: String,
    val description: String,
    val rating: Double,
    val priceRange: String,
    val routes: List<String>,
    val contact: String,
    val logoRes: Int = 0, // Resource ID for local logo
    val imageUrl: String? = null,
    val totalTrips: Int = 0,
    val amenities: List<String> = emptyList(),
    val startingPrice: String = ""
)

data class Destination(
    val id: Int,
    val name: String,
    val description: String,
    val priceFrom: String,
    val duration: String,
    val distance: String, // Added for consistency with PopularDestination
    val imageRes: Int = 0, // Resource ID for local image
    val imageUrl: String? = null,
    val activities: List<String>,
    val bestTimeToVisit: String
)
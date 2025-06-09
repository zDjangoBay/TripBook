package com.android.tripbook.model

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
    val startingPrice: String = "",
    val totalRatings: Int = 0, // Total number of ratings received
    val ratingSum: Double = 0.0, // Sum of all ratings for calculation
    val hasUserRated: Boolean = false // Track if current user has rated this company
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

data class UserRating(
    val userId: String,
    val companyId: Int,
    val rating: Float,
    val timestamp: Long = System.currentTimeMillis()
)

// Helper class to manage rating operations
data class RatingUpdate(
    val companyId: Int,
    val newRating: Float,
    val updatedCompany: BoatCompany
)
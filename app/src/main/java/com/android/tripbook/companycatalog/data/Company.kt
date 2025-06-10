
package com.android.tripbook.companycatalog.data

data class Company(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val rating: Float,
    val totalRatings: Int,
    val location: String,
    val city: String,
    val region: String,
    val phoneNumber: String,
    val email: String,
    val website: String,
    val imageUrl: String,
    val imageUrls: List<String>,
    val services: List<String>,
    val amenities: List<String>,
    val priceRange: String,
    val isPremium: Boolean,
    val isVerified: Boolean,
    val isOpen: Boolean,
    val openingHours: String,
    val specialOffers: List<String>,
    val languages: List<String>,
    val establishedYear: Int,
    val likes: Int,
    val views: Int,
    val stars: Float,
    val followers: Int,
    val isFollowing: Boolean = false,
    val isFavorite: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val coordinates: Coordinates
)

data class Review(
    val id: String,
    val userName: String,
    val userAvatar: String,
    val rating: Float,
    val comment: String,
    val date: String,
    val images: List<String> = emptyList()
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

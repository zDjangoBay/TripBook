package com.android.tripbook.data.models

data class TravelService(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String, 
    val price: Double,
    val currency: String,
    val rating: Double, /
    val reviewCount: Int,
    val location: String,
    val category: String, // e.g., "Flights", "Hotels", "Tours"
    val agency: TravelAgency 
)
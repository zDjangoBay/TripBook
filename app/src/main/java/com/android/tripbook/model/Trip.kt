package com.android.tripbook.model

data class Trip(
    // Fields provided by your addNewTrip
    val id: Int,
    val title: String,
    val caption: String,
    val description: String,
    val imageUrl: List<String>, // Assuming your model expects a List

    // Additional fields with default values
    val latitude: Double = 0.0,               // Default latitude
    val longitude: Double = 0.0,              // Default longitude
    val price: String = "N/A",                // Default price
    val rating: Float = 0.0f,                 // Default rating
    val reviewCount: Int = 0,                 // Default review count
    val duration: String = "Not specified",   // Default duration
    val city: String = "Unknown",             // Default city
    val country: String = "Unknown",          // Default country
    val region: String? = null                // Default region (can be nullable)
    // ... any other fields that should have a default
)
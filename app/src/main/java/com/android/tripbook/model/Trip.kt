package com.android.tripbook.model
import java.io.Serializable



/**
 * Represents a travel trip entry in the Trip Catalog.
 * @param id Unique identifier for the trip
 * @param title The name of the trip or destination
 * @param description A brief summary of the trip
 * @param imageUrl Optional image to show for the trip
 */

//extending the trip model class to contain my functionality

data class Trip(
    val id: Int,
    val title: String,
    val caption: String,
    val description: String,
    val imageUrl: List<String>,
    // Additional fields with default values
    val latitude: Double = 0.0,               // Default latitude
    val longitude: Double = 0.0,              // Default longitude
    val rating: Float = 0.0f,                 // Default rating
    val reviewCount: Int = 0,                 // Default review count
    val duration: String = "Not specified",   // Default duration
    val city: String = "Unknown",             // Default city
    val country: String = "Unknown",          // Default country
    val region: String? = null
) : Serializable



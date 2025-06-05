package com.android.tripbook.model
import java.io.Serializable



/**
 * Represents a travel trip entry in the Trip Catalog.
 * @param id Unique identifier for the trip
 * @param title The name of the trip or destination
 * @param description A brief summary of the trip
 * @param imageUrl Optional image to show for the trip
 */

data class Trip(
    val id: Int,
    val title: String,
    val description: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: List<String>, // Assuming you want to support multiple images

    // Optional fields or fields with common defaults
    val caption: String = "", // Optional caption, defaults to empty
    val region: String? = null, // Optional region, defaults to null
   // val rating: Float = 0.0f, // Default rating
   // val reviewCount: Int = 0, // Default review count
   // val duration: String = "N/A", // Default duration, or could be more structured

    // You might have other fields, for example:
    // val price: Double? = null,
    // val category: String = "Adventure",
    // val tags: List<String> = emptyList(),
    // val isFeatured: Boolean = false
): Serializable



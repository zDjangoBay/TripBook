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
    val price: String,
    val rating: Float,
    val reviewCount: Int,
    val duration: String,
    // NEW: Location fields for maps integration
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val country: String,
    val region: String? = null
) : Serializable



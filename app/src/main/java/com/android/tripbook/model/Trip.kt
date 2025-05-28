package com.android.tripbook.model


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
    val imageUrl: String,
)

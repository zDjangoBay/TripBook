package com.android.tripbook.model
import java.io.Serializable



/**
 * Represents a travel trip entry in the Trip Catalog.
 * @param id Unique identifier for the trip
 * @param title The name of the trip or destination
 * @param caption A brief summary of the trip
 * @param description A detailed description of the trip
 * @param imageUrl List of images to show for the trip
 */

data class Trip(
    val id: Int,
    val title: String,
    val caption: String,
    val description: String,
    val imageUrl: List<String>
) : Serializable



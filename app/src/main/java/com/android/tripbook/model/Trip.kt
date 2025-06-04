package com.android.tripbook.Model
import java.io.Serializable



/**
 * Represents a travel trip entry in the Trip Catalog.
 * @param id Unique identifier for the trip
 * @param title The name of the trip or destination
 * @param description A brief summary of the trip
 * @param imageUrl Optional image to show for the trip
 */

data class Trip(
    val id: Int = 0,
    val title: String = "",
    val caption: String = "",
    val description: String = "",
    val imageUrl: List<String> = emptyList(),
    val companyLogo: String = "",
    val companyName: String = "",
    val from: String = "",
    val fromshort: String = "",
    val to: String = "",
    val toshort: String = "",
    val arriveTime: String = "",
    val score: Double = 0.0,
    val price: Double = 0.0
) : Serializable



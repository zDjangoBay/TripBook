package com.android.tripbook

// Define this in a new file, e.g., 'data/TripItem.kt' or within MainActivity if it's small
data class TripItem(
    val id: Int, // A unique ID for each item, useful for keys in LazyColumn
    val imageUrl: Int, // Or use Int if you're storing drawable resource IDs
    val cityName: String,
    val dates: String
)
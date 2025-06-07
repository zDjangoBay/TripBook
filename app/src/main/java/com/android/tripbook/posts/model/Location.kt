package com.android.tripbook.posts.model

data class Location(
    val name: String,
    val city: String? = null,
    val country: String? = null,
    val coordinates: Coordinates? = null,
    val address: String? = null,
    val placeId: String? = null
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

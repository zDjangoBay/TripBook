package com.android.tripbook.posts.model


data class Location(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val coordinates: Coordinates?
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)
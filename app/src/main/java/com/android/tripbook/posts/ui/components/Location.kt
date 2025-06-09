package com.android.tripbook.posts.model

data class Location(
    val name: String,
    val city: String,
    val country: String,
    val coordinates: Coordinates? = null
)
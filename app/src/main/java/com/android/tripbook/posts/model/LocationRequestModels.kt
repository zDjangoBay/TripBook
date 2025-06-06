package com.android.tripbook.data.model


data class LocationSearchItem(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val coordinates: CoordinatesPayload? // Peut être nul si les coordonnées ne sont pas toujours disponibles
)

data class CoordinatesPayload(
    val latitude: Double,
    val longitude: Double
)

package com.android.tripbook.model

import kotlinx.serialization.Serializable
@Serializable
data class Destination(
    val id: Int,
    val agencyId: Int,
    val agencyRating: Int,
    val destinationName: String,
    val destinationTarif: Double
) {
    init {
        require(agencyRating in 1..5) { "Agency rating must be between 1 and 5" }
        require(destinationTarif >= 0) { "Destination tariff must be non-negative" }
        require(destinationName.isNotBlank()) { "Destination name cannot be blank" }
    }
}
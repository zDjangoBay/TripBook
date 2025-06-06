package com.android.tripbook.model

data class Destination(
    val id: Int,
    val agencyId: Int,
    val agencyRating: Int,
    val destinationName: String,
    val destinationTarif: Double
)

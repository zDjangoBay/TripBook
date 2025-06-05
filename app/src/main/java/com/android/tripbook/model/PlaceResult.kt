package com.android.tripbook.model

data class PlaceResult(
    val placeId: String,
    val name: String,
    val address: String,
    val types: List<String> = emptyList(),
    val rating: Double? = null,
    val priceLevel: Int? = null,
    val photoReference: String? = null
)
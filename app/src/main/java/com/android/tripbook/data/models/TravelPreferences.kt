package com.android.tripbook.data.model

data class TravelPreferences(
    var destinationTypes: List<String> = emptyList(),
    var travelStyle: String = "",
    var accommodation: List<String> = emptyList(),
    var activities: List<String> = emptyList(),
    var transport: String = "",
    var frequency: String = "",
    var duration: Int = 1,
    var budget: Int = 500
)

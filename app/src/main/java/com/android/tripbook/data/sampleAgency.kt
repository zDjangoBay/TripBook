package com.android.tripbook.data

import com.android.tripbook.model.Agency


object SampleAgency {
    fun getForTrip(tripId: Int): List<Agency> = listOf(
        Agency(
            id = 1,
            name = "GENERAL-EXPRESS VOYAGE",
            price = "7,000 XAF",
            rating = 4.2f,
            reviewCount = 128,
            perks = listOf("Free cancellation", "English guides"),
            imageUrl = "https://example.com/agency1.jpg",
            description = "Reliable agency with 10+ years experience in mountain trips over the country"
        ),
        Agency(
            id = 2,
            name = "TOURISTIQUE-EXPRESS",
            price = "8,500 XAF",
            rating = 4.5f,
            reviewCount = 215,
            perks = listOf("Private transfers", "Flexible schedules"),
            imageUrl = "https://example.com/agency2.jpg",
            description = "Premium service with luxury vehicles"
        ),
        Agency(
            id = 3,
            name = "AVENIR-VOYAGE",
            price = "7,500 XAF",
            rating = 4.5f,
            reviewCount = 228,
            perks = listOf("Private transfers with more commodities", "Flexible schedules and timing"),
            imageUrl = "https://example.com/agency2.jpg",
            description = "Premium service with luxury vehicles"
    )
    )
}
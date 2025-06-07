package com.android.tripbook.companycatalog.data

import com.android.tripbook.R

object MockCompanyData {

    val companies = listOf(
        CompanyData(
            id = "mock_001",
            name = "Safari Adventures Ltd.",
            description = "Unforgettable wildlife safaris across Africa.",
            contactInfo = mapOf(
                "phone" to "+237 670123456",
                "email" to "info@safariadventures.com",
                "website" to "https://www.safariadventures.com"
            ),
            address = "123 Wildlife Rd, Yaounde, Cameroon",
            location = Pair(3.8480, 11.5021),
            servicesOffered = listOf("Safari Tours", "Wildlife Photography", "Eco-Tourism"),
            logoResId = R.drawable.mock_logo_1,
            averageRating = 4.8f,
            numberOfReviews = 120,
            socialMediaLinks = mapOf(
                "facebook" to "https://fb.com/safariadventures",
                "instagram" to "https://instagram.com/safariadventures"
            )
        ),
        CompanyData(
            id = "mock_002",
            name = "Peak Explorers Travel",
            description = "Mountain climbs and hiking in scenic spots.",
            contactInfo = mapOf(
                "phone" to "+237 680987654",
                "email" to "contact@peakexplorers.com",
                "website" to "https://www.peakexplorers.com"
            ),
            address = "45 Summit St, Buea, Cameroon",
            location = Pair(4.1500, 9.2333),
            servicesOffered = listOf("Mountain Expeditions", "Hiking Tours", "Adventure Sports"),
            logoResId = R.drawable.mock_logo_2,
            averageRating = 4.5f,
            numberOfReviews = 85,
            socialMediaLinks = mapOf("twitter" to "https://twitter.com/peakexplorers")
        ),
        CompanyData(
            id = "mock_003",
            name = "Ocean Breeze Getaways",
            description = "Beach holidays and coastal adventures worldwide.",
            contactInfo = mapOf(
                "phone" to "+237 690112233",
                "email" to "hello@oceanbreeze.com",
                "website" to "https://www.oceanbreeze.com"
            ),
            address = "78 Beachfront Ave, Limbe, Cameroon",
            location = Pair(4.0000, 9.2167),
            servicesOffered = listOf("Beach Holidays", "Scuba Diving", "Island Hopping"),
            logoResId = R.drawable.mock_logo_3,
            averageRating = 4.2f,
            numberOfReviews = 210
        ),
        CompanyData(
            id = "mock_004",
            name = "Urban Explorers Co.",
            description = "Tailored city tours in vibrant African cities.",
            contactInfo = mapOf(
                "phone" to "+237 677445566",
                "email" to "info@urbanexplorers.com",
                "website" to "https://www.urbanexplorers.com"
            ),
            address = "20 City Center Blvd, Douala, Cameroon",
            location = Pair(4.0500, 9.7000),
            servicesOffered = listOf("City Tours", "Cultural Immersion", "Food Tours"),
            logoResId = R.drawable.mock_logo_4,
            averageRating = 4.7f,
            numberOfReviews = 95
        ),
        CompanyData(
            id = "mock_005",
            name = "Desert Trek Tours",
            description = "Guided desert treks across the Sahara.",
            contactInfo = mapOf(
                "phone" to "+237 666778899",
                "email" to "trek@deserttours.com",
                "website" to "https://www.deserttours.com"
            ),
            address = "10 Oasis Road, Maroua, Cameroon",
            location = Pair(10.5900, 14.3300),
            servicesOffered = listOf("Desert Safaris", "Camel Treks", "Cultural Encounters"),
            logoResId = R.drawable.mock_logo_5,
            averageRating = 4.9f,
            numberOfReviews = 60
        )
    )
}

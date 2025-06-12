
package com.android.tripbook.companycatalog.data

import com.android.tripbook.R

object MockCompanyData {
    val companies = listOf(
        CompanyData(
            id = "mock_001",
            name = "Cameroon Safari Adventures",
            description = "Unforgettable wildlife safaris across Cameroon's national parks.",
            contactInfo = mapOf(
                "phone" to "+237 670123456",
                "email" to "info@cameroon-safari.com",
                "website" to "https://www.cameroon-safari.com"
            ),
            address = "123 Safari Boulevard, Yaounde, Cameroon",
            location = Pair(3.8480, 11.5021), // Yaounde coordinates
            servicesOffered = listOf("Waza Park Tours", "Wildlife Photography", "Eco-Tourism"),
            logoResId = R.drawable.mock_logo_1,
            averageRating = 4.8f,
            numberOfReviews = 120,
            socialMediaLinks = mapOf(
                "facebook" to "https://fb.com/cameroonsafari",
                "instagram" to "https://instagram.com/cameroonsafari"
            )
        ),
        CompanyData(
            id = "mock_002",
            name = "Mount Cameroon Expeditions",
            description = "Mountain climbing and hiking adventures on Mount Cameroon.",
            contactInfo = mapOf(
                "phone" to "+237 680987654",
                "email" to "contact@mountcameroon.com",
                "website" to "https://www.mountcameroon.com"
            ),
            address = "45 Mountain View Street, Buea, Cameroon",
            location = Pair(4.1500, 9.2333), // Buea coordinates
            servicesOffered = listOf("Mount Cameroon Climbing", "Hiking Tours", "Adventure Sports"),
            logoResId = R.drawable.mock_logo_2,
            averageRating = 4.5f,
            numberOfReviews = 85,
            socialMediaLinks = mapOf("twitter" to "https://twitter.com/mountcameroon")
        ),
        CompanyData(
            id = "mock_003",
            name = "Kribi Beach Getaways",
            description = "Beach holidays and coastal adventures in Kribi and surrounding areas.",
            contactInfo = mapOf(
                "phone" to "+237 690112233",
                "email" to "hello@kribibeach.com",
                "website" to "https://www.kribibeach.com"
            ),
            address = "78 Beach Road, Kribi, Cameroon",
            location = Pair(2.9333, 9.9167), // Kribi coordinates
            servicesOffered = listOf("Beach Holidays", "Fishing Tours", "Coastal Excursions"),
            logoResId = R.drawable.mock_logo_3,
            averageRating = 4.2f,
            numberOfReviews = 210
        ),
        CompanyData(
            id = "mock_004",
            name = "Douala City Explorers",
            description = "Tailored city tours in Douala, Cameroon's economic capital.",
            contactInfo = mapOf(
                "phone" to "+237 677445566",
                "email" to "info@doualaexplorers.com",
                "website" to "https://www.doualaexplorers.com"
            ),
            address = "20 Akwa Boulevard, Douala, Cameroon",
            location = Pair(4.0500, 9.7000), // Douala coordinates
            servicesOffered = listOf("City Tours", "Cultural Immersion", "Local Food Tours"),
            logoResId = R.drawable.mock_logo_4,
            averageRating = 4.7f,
            numberOfReviews = 95
        ),
        CompanyData(
            id = "mock_005",
            name = "Adamawa Highland Adventures",
            description = "Guided treks across the Adamawa plateau and highlands.",
            contactInfo = mapOf(
                "phone" to "+237 666778899",
                "email" to "trek@adamawahighlands.com",
                "website" to "https://www.adamawahighlands.com"
            ),
            address = "10 Plateau Road, Ngaoundere, Cameroon",
            location = Pair(7.3167, 13.5833), // Ngaoundere coordinates
            servicesOffered = listOf("Highland Treks", "Cattle Ranch Visits", "Cultural Encounters"),
            logoResId = R.drawable.mock_logo_5,
            averageRating = 4.9f,
            numberOfReviews = 60
        )
    )
}

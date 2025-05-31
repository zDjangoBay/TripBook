package com.android.tripbook.companycatalog.model

import com.android.tripbook.R

object MockCompanyData {
    val companies = listOf(
        Company(
            id = 1,
            name = "Company 1",
            imageResIds = listOf(
                R.drawable.company11,
                R.drawable.company12,
                R.drawable.company13,
            ),
            description = "Company 1 helps you find travel deals and offers great customer support. We are dedicated to making your travel dreams come true with seamless booking and personalized service.",
            likes = 1250,
            isLiked = false,
            stars = 5,
            services = listOf(
                CompanyService("Flight Booking", "Book your flights"),
                CompanyService("Hotel Booking", "Find best hotels"),
                CompanyService("Travel Insurance", "Stay protected")
            ),
            contacts = listOf(
                CompanyContact("Email", "info@company1.com"),
                CompanyContact("Phone", "+1234567890")
            )
        ),
        Company(
            id = 2,
            name = "Company 2",
            imageResIds = listOf(
                R.drawable.company21,
                R.drawable.company22,
                R.drawable.company23
            ),
            description = "Local tours and guides for an unforgettable experience in the city. Discover hidden gems and local culture with our expert guides and customizable packages.",
            likes = 870,
            isLiked = false,
            stars = 3,
            services = listOf(
                CompanyService("Guided Tours", "Hire local experts"),
                CompanyService("Custom Packages", "Get tailored packages")
            ),
            contacts = listOf(
                CompanyContact("Email", "contact@company2.com"),
                CompanyContact("Phone", "+9876543210")
            )
        ),
        Company(
            id = 3,
            name = "Company 3",
            imageResIds = listOf(
                R.drawable.company31,
                R.drawable.company32,
                R.drawable.company33,
            ),
            description = "Company 3offers adventure trips and outdoor excursions for thrill seekers. Join us for hiking, rafting, and unforgettable experiences.",
            likes = 430,
            isLiked = false,
            stars = 4,
            services = listOf(
                CompanyService("Adventure Trips", "Exciting outdoor experiences"),
                CompanyService("Group Tours", "Explore with like-minded people")
            ),
            contacts = listOf(
                CompanyContact("Email", "hello@company3.com"),
                CompanyContact("Phone", "+1122334455")
            )
        ),
        Company(
            id = 4,
            name = "Company 4",
            imageResIds = listOf(
                R.drawable.company41,
                R.drawable.company42,
                R.drawable.company43,
            ),
            description = "Company 4 specializes in luxury travel experiences including 5-star hotels, premium flights, and exclusive destinations.",
            likes = 1600,
            isLiked = false,
            stars = 5,
            services = listOf(
                CompanyService("Luxury Flights", "Top-class air travel"),
                CompanyService("Premium Hotels", "Best-in-class accommodation")
            ),
            contacts = listOf(
                CompanyContact("Email", "luxury@company4.com"),
                CompanyContact("Phone", "+5566778899")
            )
        ),
        Company(
            id = 5,
            name = "Company 5",
            imageResIds = listOf(
                R.drawable.company51,
                R.drawable.company52,
                R.drawable.company53,
            ),
            description = "Company 5 is your travel tech partner, providing booking platforms and itinerary tools for smarter travel planning.",
            likes = 690,
            isLiked = false,
            stars = 4,
            services = listOf(
                CompanyService("Booking Platform", "Easy trip planning"),
                CompanyService("Smart Itineraries", "AI-powered suggestions")
            ),
            contacts = listOf(
                CompanyContact("Email", "support@company5.com"),
                CompanyContact("Phone", "+3344556677")
            )
        ),
        Company(
            id = 6,
            name = "Company 6",
            imageResIds = listOf(
                R.drawable.company61,
                R.drawable.company62,
                R.drawable.company63,
            ),
            description = "Company 6 offers eco-friendly travel solutions including sustainable lodging and green transportation.",
            likes = 540,
            isLiked = false,
            stars = 4,
            services = listOf(
                CompanyService("Eco Lodging", "Stay sustainably"),
                CompanyService("Green Transport", "Environment-friendly travel")
            ),
            contacts = listOf(
                CompanyContact("Email", "eco@company6.com"),
                CompanyContact("Phone", "+9988776655")
            )
        ),
    )
}
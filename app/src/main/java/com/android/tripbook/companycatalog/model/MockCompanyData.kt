package com.android.tripbook.companycatalog.model

import com.android.tripbook.R

object MockCompanyData {
    val companies = listOf(
        Company(
            id = 1,
            name = "Cameroon Travel and Tours",
            imageResIds = listOf(
                R.drawable.cameroon_travel_1,
                R.drawable.cameroon_travel_2,
                R.drawable.cameroon_travel_3
            ),
            description = "Cameroon Travel and Tours offers comprehensive and comfortable tours across Cameroon, showcasing the country's diverse cultures and landscapes.",
            likes = 1520,
            isLiked = false,
            stars = 5,
            services = listOf(
                CompanyService("Cultural Tours", "Experience Cameroon's rich heritage"),
                CompanyService("Wildlife Safaris", "Explore national parks and reserves"),
                CompanyService("Mount Cameroon Trekking", "Conquer the highest peak in West Africa")
            ),
            contacts = listOf(
                CompanyContact("Email", "info@cameroontravelandtours.com"),
                CompanyContact("Phone", "+237 677 644 831")
            )
        ),
        Company(
            id = 2,
            name = "Global Bush Travel and Tourism Agency",
            imageResIds = listOf(
                R.drawable.global_bush_1,
                R.drawable.global_bush_2,
                R.drawable.global_bush_3
            ),
            description = "Global Bush Travel and Tourism Agency offers a wide range of travel services, including safaris, cultural tours, and corporate travel solutions.",
            likes = 980,
            isLiked = false,
            stars = 4,
            services = listOf(
                CompanyService("Corporate Travel", "Professional travel management"),
                CompanyService("Safari Tours", "Experience Cameroon's wildlife"),
                CompanyService("Event Planning", "Organize memorable events")
            ),
            contacts = listOf(
                CompanyContact("Email", "info@globalbush.com"),
                CompanyContact("Phone", "+237 677 77 77 77")
            )
        ),
        Company(
            id = 3,
            name = "Luxecam Tours Ltd",
            imageResIds = listOf(
                R.drawable.luxecam_tours_1,
                R.drawable.luxecam_tours_2,
                R.drawable.luxecam_tours_3
            ),
            description = "Luxecam Tours Ltd provides luxury travel experiences, combining comfort with authentic Cameroonian adventures.",
            likes = 1120,
            isLiked = false,
            stars = 5,
            services = listOf(
                CompanyService("Luxury Safaris", "Premium wildlife experiences"),
                CompanyService("Beach Holidays", "Relax on Cameroon's pristine beaches"),
                CompanyService("Cultural Tours", "Explore Cameroon's heritage")
            ),
            contacts = listOf(
                CompanyContact("Email", "info@safarideal.com"),
                CompanyContact("Phone", "++44 (0)7788 194 180")
            )
        ),
        Company(
            id = 4,
            name = "ECOTREK Cameroon Travel Agency",
            imageResIds = listOf(
                R.drawable.ecotrek_cameroon_1,
                R.drawable.ecotrek_cameroon_2,
                R.drawable.ecotrek_cameroon_3
            ),
            description = "ECOTREK Cameroon specializes in eco-friendly tours, promoting sustainable tourism and conservation efforts.",
            likes = 890,
            isLiked = false,
            stars = 4,
            services = listOf(
                CompanyService("Eco-Tours", "Sustainable travel experiences"),
                CompanyService("Wildlife Conservation", "Support local conservation projects"),
                CompanyService("Community Engagement", "Interact with local communities")
            ),
            contacts = listOf(
                CompanyContact("Email", "info@ecotrekcameroon.com"),
                CompanyContact("Phone", "+237 666 66 66 66")
            )
        )
    )
}
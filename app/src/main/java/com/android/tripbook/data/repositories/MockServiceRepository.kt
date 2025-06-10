package com.android.tripbook.data.repositories

import com.android.tripbook.data.models.TravelAgency
import com.android.tripbook.data.models.TravelService

class MockServiceRepository {

    // Mock Travel Agencies
    private val mockAgencies = listOf(
        TravelAgency("A001", "Oceanic Travel Agency", "info@oceanic.com", "+1234567890", "https://oceanic.com"),
        TravelAgency("A002", "Mountain Adventures Inc.", "contact@mountainadv.com", "+1987654321"),
        TravelAgency("A003", "City Explorers Tours", "support@cityexplorers.com", "+1122334455", "https://cityexplorers.com"),
        TravelAgency("A004", "SkyHigh Airlines", "info@skyhigh.com", "+1555123456"),
        TravelAgency("A005", "GrandStay Hotels", "reservations@grandstay.com", "+1777987654")
    )

    // Mock Travel Services
    private val mockServices = listOf(
        TravelService(
            id = "S001",
            name = "Luxurious Beach Resort Stay",
            description = "Experience the ultimate relaxation at our exclusive beach resort with stunning ocean views.",
            imageUrl = "https://cdn.pixabay.com/photo/2019/09/12/15/21/resort-4471852_1280.jpg", // Placeholder image URL
            price = 1200.00,
            currency = "USD",
            rating = 4.7,
            reviewCount = 150,
            location = "Maldives",
            category = "Hotels",
            agency = mockAgencies[0] // Oceanic Travel Agency
        ),
        TravelService(
            id = "S002",
            name = "Himalayan Trekking Expedition",
            description = "Embark on an unforgettable journey through the majestic Himalayas. Guided tours, all-inclusive.",
            imageUrl = "https://cdn.pixabay.com/photo/2021/07/08/03/55/mount-everest-6395759_1280.jpg", // Placeholder image URL
            price = 2500.00,
            currency = "USD",
            rating = 4.9,
            reviewCount = 80,
            location = "Nepal",
            category = "Tours",
            agency = mockAgencies[1] // Mountain Adventures Inc.
        ),
        TravelService(
            id = "S003",
            name = "Paris City Break Package",
            description = "Discover the magic of Paris with our 3-day city break. Includes flights and centrally located hotel.",
            imageUrl = "https://cdn.pixabay.com/photo/2018/08/03/15/02/channel-3582081_1280.jpg", // Placeholder image URL
            price = 850.00,
            currency = "USD",
            rating = 4.5,
            reviewCount = 210,
            location = "Paris, France",
            category = "Tours",
            agency = mockAgencies[2] // City Explorers Tours
        ),
        TravelService(
            id = "S004",
            name = "New York to London Flight",
            description = "Direct flight from JFK to Heathrow. Economy class with all amenities.",
            imageUrl = "https://cdn.pixabay.com/photo/2021/12/13/07/06/airplane-6867678_960_720.jpg", // Placeholder image URL
            price = 600.00,
            currency = "USD",
            rating = 4.2,
            reviewCount = 300,
            location = "New York to London",
            category = "Flights",
            agency = mockAgencies[3] // SkyHigh Airlines
        ),
        TravelService(
            id = "S005",
            name = "Luxury Suite in Dubai",
            description = "Stay in a lavish suite overlooking the Dubai skyline.",
            imageUrl = "https://cdn.pixabay.com/photo/2021/09/22/08/35/architecture-6646154_1280.jpg", // Placeholder image URL
            price = 1800.00,
            currency = "USD",
            rating = 4.8,
            reviewCount = 95,
            location = "Dubai, UAE",
            category = "Hotels",
            agency = mockAgencies[4] // GrandStay Hotels
        )
    )


    fun getMockServices(): List<TravelService> {
        return mockServices
    }

  
    fun getServiceById(id: String): TravelService? {
        return mockServices.find { it.id == id }
    }
}
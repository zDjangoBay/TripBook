package com.android.tripbook.service

data class AgencyService(
    val id: String,
    val name: String,
    val type: String, // e.g., "Tour", "Accommodation", "Transportation"
    val description: String,
    val price: Int, // in USD
    val rating: Float, // 0.0 to 5.0
    val location: String // City or region
)

data class TravelAgency(
    val id: String,
    val name: String,
    val services: List<AgencyService>,
    val rating: Float
)

class TravelAgencyService {
    // Mock data for travel agencies
    private val agencies = listOf(
        TravelAgency(
            id = "agency1",
            name = "Nile Adventures",
            services = listOf(
                AgencyService(
                    id = "service1",
                    name = "Nile River Cruise",
                    type = "Tour",
                    description = "A 3-day cruise along the Nile River",
                    price = 500,
                    rating = 4.5f,
                    location = "Cairo, Egypt"
                ),
                AgencyService(
                    id = "service2",
                    name = "Pyramids Tour",
                    type = "Tour",
                    description = "Guided tour of the Giza Pyramids",
                    price = 150,
                    rating = 4.8f,
                    location = "Giza, Egypt"
                )
            ),
            rating = 4.6f
        ),
        TravelAgency(
            id = "agency2",
            name = "Sahara Travels",
            services = listOf(
                AgencyService(
                    id = "service3",
                    name = "Desert Safari",
                    type = "Tour",
                    description = "2-day desert safari experience",
                    price = 300,
                    rating = 4.2f,
                    location = "Marrakech, Morocco"
                ),
                AgencyService(
                    id = "service4",
                    name = "Luxury Hotel Booking",
                    type = "Accommodation",
                    description = "5-star hotel in Marrakech",
                    price = 200,
                    rating = 4.7f,
                    location = "Marrakech, Morocco"
                )
            ),
            rating = 4.4f
        )
    )

    fun getAgenciesForDestination(destination: String): List<TravelAgency> {
        // Filter agencies based on destination (case-insensitive, partial match)
        val normalizedDestination = destination.lowercase()
        return agencies.filter { agency ->
            agency.services.any { service ->
                service.location.lowercase().contains(normalizedDestination)
            }
        }
    }

    fun filterAgencies(
        agencies: List<TravelAgency>,
        minRating: Float? = null,
        maxPrice: Int? = null,
        serviceType: String? = null
    ): List<TravelAgency> {
        return agencies.map { agency ->
            agency.copy(
                services = agency.services.filter { service ->
                    (minRating == null || service.rating >= minRating) &&
                            (maxPrice == null || service.price <= maxPrice) &&
                            (serviceType == null || service.type == serviceType)
                }
            )
        }.filter { it.services.isNotEmpty() }
    }
}
package com.android.tripbook.data.repositories

import com.android.tripbook.R
import com.android.tripbook.data.models.Service
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Locale

@Singleton
class MockServiceRepository @Inject constructor() : ServiceRepository {

    private val allServices = listOf(
        Service("s1", "Flight to Paris", "Flight", "Experience the romantic city of Paris.", "$550", R.drawable.flight_placeholder, 4.7, "Airways Pro"),
        Service("s2", "Eiffel Tower Tour", "Tour", "Guided tour of the iconic Eiffel Tower.", "$50", R.drawable.tour_placeholder, 4.9, "City Tours"),
        Service("s3", "Car Rental: Economy", "Car Rental", "Affordable car rental for city exploration.", "$40/day", R.drawable.car_placeholder, 4.2, "Rent-A-Ride"),
        Service("s4", "Flight to Rome", "Flight", "Discover the ancient wonders of Rome.", "$480", R.drawable.flight_placeholder, 4.5, "Roman Air"),
        Service("s5", "Colosseum & Forum Tour", "Tour", "Walk through history in Rome's ancient heart.", "$75", R.drawable.tour_placeholder, 4.8, "Historical Journeys"),
        Service("s6", "Luxury SUV Rental", "Car Rental", "Travel in comfort and style.", "$120/day", R.drawable.car_placeholder, 4.6, "Premium Wheels"),
        Service("s7", "Flight to Tokyo", "Flight", "Explore the vibrant city of Tokyo.", "$900", R.drawable.flight_placeholder, 4.6, "Japan Skies"),
        Service("s8", "Kyoto Temples Tour", "Tour", "A serene journey through Kyoto's historic temples.", "$90", R.drawable.tour_placeholder, 4.9, "Culture Explorers"),
        Service("s9", "Flight to London", "Flight", "Visit the bustling capital of England.", "$600", R.drawable.flight_placeholder, 4.3, "British Air"),
        Service("s10", "London Eye Experience", "Tour", "Panoramic views of London from the iconic Ferris wheel.", "$30", R.drawable.tour_placeholder, 4.7, "London Attractions")
    )

    override fun searchServices(query: String?): List<Service> {
        return if (query.isNullOrBlank()) {
            allServices
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            allServices.filter {
                it.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        it.description.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        it.type.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        it.agency.lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            }
        }
    }

    override fun getServiceById(id: String): Service? {
        return allServices.find { it.id == id }
    }
}
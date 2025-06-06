package com.android.tripbook.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.model.Agency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// Assume "agencyId" is passed as a navigation argument
class AgencyDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val agencyId: String? = savedStateHandle["agencyId"]

    private val _agency = MutableStateFlow<Agency?>(null)
    val agency: StateFlow<Agency?> = _agency.asStateFlow()

    init {
        // In a real app, you'd fetch details from a repository/use case:
        // getAgencyDetailsUseCase(agencyId).collect { resource -> ... }
        loadDummyAgencyDetails(agencyId)
    }

    private fun loadDummyAgencyDetails(id: String?) {
        viewModelScope.launch {
            // Simulate network delay
            kotlinx.coroutines.delay(500)

            // Find the dummy agency (or create a new one if ID not found for demo)
            _agency.value = dummyAgencies.find { it.id == id } ?: dummyAgencies.firstOrNull()
        }
    }

    // Dummy data for demonstration purposes
    private val dummyAgencies = listOf(
        Agency(
            id = "agency_1",
            name = "African Adventure Tours",
            description = "Specializing in safaris and cultural tours across East Africa. We offer bespoke travel experiences, focusing on sustainable and authentic encounters with wildlife and local communities. Our guides are experts in the region's flora and fauna, ensuring an unforgettable journey.",
            logoUrl = "https://picsum.photos/id/100/200/200",
            rating = 4.8,
            reviewCount = 125,
            contactEmail = "info@africantours.com",
            contactPhone = "+254712345678",
            servicesOffered = listOf("Wildlife Safaris", "Cultural Tours", "Mountaineering Expeditions", "Photography Workshops"),
            isVerified = true,
            minPrice = 1500.0,
            maxPrice = 8000.0
        ),
        Agency(
            id = "agency_2",
            name = "Sahara Expeditions",
            description = "Embark on epic desert adventures and explore historical sites in North Africa. From camel treks under starry skies to ancient city ruins, we craft journeys that connect you with the soul of the desert.",
            logoUrl = "https://picsum.photos/id/101/200/200",
            rating = 4.5,
            reviewCount = 80,
            contactEmail = "contact@saharaexp.com",
            contactPhone = "+212612345678",
            servicesOffered = listOf("Desert Safaris", "Historical Tours", "Camel Treks", "Oasis Visits"),
            isVerified = true,
            minPrice = 800.0,
            maxPrice = 5000.0
        ),
        Agency(
            id = "agency_3",
            name = "Nile Cruises & Beyond",
            description = "Experience the grandeur of ancient Egypt with luxury cruises on the Nile. Our tours combine relaxation with immersive historical explorations, led by experienced Egyptologists.",
            logoUrl = "https://picsum.photos/id/102/200/200",
            rating = 4.9,
            reviewCount = 210,
            contactEmail = "sales@nilecruises.com",
            contactPhone = "+201012345678",
            servicesOffered = listOf("Nile Cruises", "Egyptology Tours", "City Breaks", "Red Sea Diving"),
            isVerified = true,
            minPrice = 1200.0,
            maxPrice = 7000.0
        ),
        Agency(
            id = "agency_4",
            name = "Cape Explorer Co.",
            description = "Discover the breathtaking landscapes and vibrant culture of South Africa. From Table Mountain hikes to Garden Route adventures, we offer diverse experiences for every traveler.",
            logoUrl = "https://picsum.photos/id/103/200/200",
            rating = 4.7,
            reviewCount = 95,
            contactEmail = "info@capeexplorer.co",
            contactPhone = "+27721234567",
            servicesOffered = listOf("Wine Tours", "Coastal Drives", "Wildlife Encounters", "Table Mountain Hikes"),
            isVerified = false,
            minPrice = 900.0,
            maxPrice = 4500.0
        ),
        Agency(
            id = "agency_5",
            name = "West African Journeys",
            description = "Immerse yourself in the rich history and authentic cultures of West Africa. Our tours focus on local interactions, traditional markets, and historical sites.",
            logoUrl = null,
            rating = 4.3,
            reviewCount = 60,
            contactEmail = "hello@wajourneys.com",
            contactPhone = "+233241234567",
            servicesOffered = listOf("Cultural Festivals", "Historical Sites", "Local Cuisine", "Artisan Workshops"),
            isVerified = true,
            minPrice = 700.0,
            maxPrice = 3000.0
        )
    )
}
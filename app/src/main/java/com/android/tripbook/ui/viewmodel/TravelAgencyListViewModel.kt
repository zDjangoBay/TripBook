package com.android.tripbook.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.android.tripbook.data.model.Agency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow    
import kotlinx.coroutines.flow.asStateFlow
import javax.until.UUID 


class TravelAgencyListViewModel : ViewModel() {

    private val _agencies = MutableStateFlow<List<Agency>>(emptyList())
    val agencies: StateFlow<List<Agency>> = _agencies.asStateFlow()


    private val _filterRating = MutableStateFlow<Double?>(null)
    val filterRating: StateFlow<Double?> = _filterRating.asStateFlow()

    private val _filterPrice = MutableStateFlow<Double?>(null)
    val _filterPrice: StateFlow<Double?> = _filterPrice.asStateFlow()


    private val _filterService = MutableStateFlow<List<String>>(null)
    val filterService: StateFlow<List<String>> = _filterService.asStateFlow()

    private val _searchQuery = MutableStateFlow<String>(null)
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()



    init {
        // Load data 
        loadAgencies()
    }

    private fun loadAgencies() {
        // Simulate loading data
        _agencies.value = listOf(
            Agency(
                id = UUID.randomUUID().toString(),
                name = "Sharama Expeditions",
                description = "Desert adventures and histotical site exploration in North Africa."
                logoUrl = "https://example.com/logoA.png",
                rating = 4.5,
                reviewsCount = 120,
                name = "Sharama Expeditions",
                contactEmail = "contact@sharamaexpeditions.com",
                contactPhone = "123-456-7890",
                servicesOffered = listOf("Deserts Safaris", "Historical Tours", "Camel Rides"),
                isVerified = true,
                minPrice = 100.0,
                maxPrice = 5000.0
            ),
            Agency(
                id = UUID.randomUUID().toString(),
                name = "Oceanic Adventures",
                description = "Diving and marine exploration in the Caribbean.",
                logoUrl = "",
                rating = 4.8,
                reviewsCount = 200,
                contactEmail = "hello@oceanicadventures.com",
                contactPhone = "987-654-3210",
                servicesOffered = listOf("Scuba Diving", "Snorkeling", "Marine Tours"),
                isVerified = true,
                minPrice = 150.0,
                maxPrice = 6000.0
            ),
            Agency( 
                id = UUID.randomUUID().toString(),
                name = "Mountain Trekkers",
                description = "Guided treks and mountain climbing in the Himalayas.",
                logoUrl = "",
                rating = 4.7,
                reviewsCount = 150,
                contactEmail = "info@mountaintrekkers.com",
                contactPhone = "555-123-4567",  
                servicesOffered = listOf("Trekking", "Climbing", "Guided Tours"),
                isVerified = true,
                minPrice = 200.0,
                maxPrice = 7000.0
            ),
            Agency(
                id = UUID.randomUUID().toString(),
                name = "Cultural Journeys",
                description = "Cultural tours and experiences in Asia.",
                logoUrl = "",
                rating = 4.6,
                reviewsCount = 180,
                contactEmail = "",
                contactPhone = "555-987-6543",
                servicesOffered = listOf("Cultural Tours", "Workshops", "Local Experiences"),
                isVerified = true,
                minPrice = 80.0,            
                maxPrice = 4000.0
            )
        )
    }
    fun applyFilterRating(minRating: Double?, maxPrice: Double?, service:String?) {
        _filterRating.value = minrating
        _filterPrice.value = maxPrice
        _filterService.value = service?
    }

    fun applySearchQuery(query: String?) {
        _searchQuery.value = query
    }
}
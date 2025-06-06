package com.android.tripbook.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.model.Agency
import com.android.data.model.BookingRequest
import com.android.tripbook.data.model.BookingResponse
import com.android.tripbook.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class BookingUiState(
    val selectedAgency: Agency? = null,
    val selectedService: String = "",
    val travelerName: String = "",
    val travelerEmail: String = "",
    val travelerPhone: String = "",
    val preferredDate: String = "", // Example: "YYYY-MM-DD"
    val numberOfTravelers: String = "1",
    val additionalNotes: String = "",
    val bookingStatus: Resource<BookingResponse>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class BookingViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val agencyId: String? = savedStateHandle["agencyId"]

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        loadDummyAgencyDetails(agencyId)
    }

    private fun loadDummyAgencyDetails(id: String?) {
        viewModelScope.launch {
            // Simulate network delay
            delay(300)
            val agency = dummyAgencies.find { it.id == id } ?: dummyAgencies.firstOrNull()
            _uiState.update { it.copy(selectedAgency = agency) }
        }
    }

    fun onServiceSelected(service: String) {
        _uiState.update { it.copy(selectedService = service) }
    }

    fun onTravelerNameChanged(name: String) {
        _uiState.update { it.copy(travelerName = name) }
    }

    fun onTravelerEmailChanged(email: String) {
        _uiState.update { it.copy(travelerEmail = email) }
    }

    fun onTravelerPhoneChanged(phone: String) {
        _uiState.update { it.copy(travelerPhone = phone) }
    }

    fun onPreferredDateChanged(date: String) {
        _uiState.update { it.copy(preferredDate = date) }
    }

    fun onNumberOfTravelersChanged(count: String) {
        _uiState.update { it.copy(numberOfTravelers = count) }
    }

    fun onAdditionalNotesChanged(notes: String) {
        _uiState.update { it.copy(additionalNotes = notes) }
    }

    fun submitBooking() {
        val currentUiState = _uiState.value

        if (currentUiState.selectedAgency == null || currentUiState.selectedService.isBlank() ||
            currentUiState.travelerName.isBlank() || currentUiState.travelerEmail.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill all required fields.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, bookingStatus = Resource.Loading(), errorMessage = null) }

        viewModelScope.launch {
            val bookingRequest = BookingRequest(
                agencyId = currentUiState.selectedAgency.id,
                serviceId = currentUiState.selectedService, // Simplified: use service name as ID for demo
                userId = "user_123", // Placeholder user ID
                bookingDetails = mapOf(
                    "travelerName" to currentUiState.travelerName,
                    "travelerEmail" to currentUiState.travelerEmail,
                    "travelerPhone" to currentUiState.travelerPhone,
                    "preferredDate" to currentUiState.preferredDate,
                    "numberOfTravelers" to currentUiState.numberOfTravelers.toIntOrNull()
                        ?: 1,
                    "additionalNotes" to currentUiState.additionalNotes
                )
            )

            // Simulate API call
            delay(2000) // Simulate network delay

            // For demo: randomly succeed or fail
            val success = (0..1).random() == 0
            if (success) {
                val response = BookingResponse(
                    bookingId = UUID.randomUUID().toString(),
                    status = "confirmed",
                    message = "Your booking for ${currentUiState.selectedService} with ${currentUiState.selectedAgency.name} has been confirmed!"
                )
                _uiState.update { it.copy(isLoading = false, bookingStatus = Resource.Success(response)) }
            } else {
                _uiState.update { it.copy(isLoading = false, bookingStatus = Resource.Error("Booking failed. Please try again."), errorMessage = "Booking failed. Please try again.") }
            }
        }
    }

    // Dummy data (copied from AgencyDetailViewModel for self-containment)
    private val dummyAgencies = listOf(
        Agency(
            id = "agency_1",
            name = "African Adventure Tours",
            description = "Specializing in safaris and cultural tours across East Africa.",
            logoUrl = "https://picsum.photos/id/100/200/200",
            rating = 4.8, reviewCount = 125, contactEmail = "info@africantours.com",
            contactPhone = "+254712345678", servicesOffered = listOf("Wildlife Safaris", "Cultural Tours", "Mountaineering Expeditions"),
            isVerified = true, minPrice = 1500.0, maxPrice = 8000.0
        ),
        Agency(
            id = "agency_2",
            name = "Sahara Expeditions",
            description = "Desert adventures and historical site explorations in North Africa.",
            logoUrl = "https://picsum.photos/id/101/200/200",
            rating = 4.5, reviewCount = 80, contactEmail = "contact@saharaexp.com",
            contactPhone = "+212612345678", servicesOffered = listOf("Desert Safaris", "Dune Bashing", "Camel Treks"),
            isVerified = true, minPrice = 800.0, maxPrice = 5000.0
        ),
        Agency(
            id = "agency_3",
            name = "Nile Cruises & Beyond",
            description = "Luxury cruises on the Nile and ancient Egypt tours.",
            logoUrl = "https://picsum.photos/id/102/200/200",
            rating = 4.9, reviewCount = 210, contactEmail = "sales@nilecruises.com",
            contactPhone = "+201012345678", servicesOffered = listOf("Nile Cruises", "Egyptology Tours", "Pyramid Visits"),
            isVerified = true, minPrice = 1200.0,
        ),
        Agency(
            id = "agency_4",
            name = "Cape Explorer Co.",
            description = "Exploring the scenic beauty and vibrant culture of South Africa.",
            logoUrl = "https://picsum.photos/id/103/200/200",
            rating = 4.7, reviewCount = 95, contactEmail = "info@capeexplorer.co",
            contactPhone = "+27721234567", servicesOffered = listOf("Wine Tours", "Coastal Drives", "Wildlife Encounters"),
            isVerified = false, minPrice = 900.0, maxPrice = 4500.0
        ),
        Agency(
            id = "agency_5",
            name = "West African Journeys",
            description = "Authentic cultural experiences and historical tours in West Africa.",
            logoUrl = null,
            rating = 4.3,
            reviewCount = 60,
            contactEmail = "hello@wajourneys.com",
            contactPhone = "+233241234567",
            servicesOffered = listOf("Cultural Festivals", "Historical Sites", "Local Cuisine"),
            isVerified = true,
            minPrice = 700.0,
            maxPrice = 3000.0,
            reviewsCount = TODO()
        )
    )
}
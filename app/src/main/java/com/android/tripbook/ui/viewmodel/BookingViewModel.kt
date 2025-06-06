//package com.android.tripbook.ui.viewmodel//package com.android.tripbook.ui.viewmodel
//
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.android.tripbook.data.model.Agency
//import com.android.tripbook.data.model.BookingRequest
//import com.android.tripbook.data.model.BookingResponse
//import com.android.tripbook.util.Resource
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import java.util.UUID
//
//data class BookingUiState(
//    val selectedAgency: Agency? = null,
//    val selectedService: String = "",
//    val travelerName: String = "",
//    val travelerEmail: String = "",
//    val travelerPhone: String = "",
//    val preferredDate: String = "", // Example: "YYYY-MM-DD"
//    val numberOfTravelers: String = "1", // Still String from UI input
//    val additionalNotes: String = "",
//    val bookingStatus: Resource<BookingResponse>? = null,
//    val isLoading: Boolean = false,
//    val errorMessage: String? = null
//)
//
//class BookingViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
//
//    private val agencyId: String? = savedStateHandle["agencyId"]
//
//    private val _uiState = MutableStateFlow(BookingUiState())
//    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()
//
//    init {
//        loadDummyAgencyDetails(agencyId)
//    }
//
//    private fun loadDummyAgencyDetails(id: String?) {
//        viewModelScope.launch {
//            // Simulate network delay
//            delay(300)
//            val agency = dummyAgencies.find { it.id == id } ?: dummyAgencies.firstOrNull()
//            _uiState.update { it.copy(selectedAgency = agency) }
//        }
//    }
//
//    fun onServiceSelected(service: String) {
//        _uiState.update { it.copy(selectedService = service) }
//    }
//
//    fun onTravelerNameChanged(name: String) {
//        _uiState.update { it.copy(travelerName = name) }
//    }
//
//    fun onTravelerEmailChanged(email: String) {
//        _uiState.update { it.copy(travelerEmail = email) }
//    }
//
//    fun onTravelerPhoneChanged(phone: String) {
//        _uiState.update { it.copy(travelerPhone = phone) }
//    }
//
//    fun onPreferredDateChanged(date: String) {
//        _uiState.update { it.copy(preferredDate = date) }
//    }
//
//    fun onNumberOfTravelersChanged(count: String) {
//        // You might add validation here (e.g., if it's not a number, set error)
//        _uiState.update { it.copy(numberOfTravelers = count) }
//    }
//
//    fun onAdditionalNotesChanged(notes: String) {
//        _uiState.update { it.copy(additionalNotes = notes) }
//    }
//
//    fun submitBooking() {
//        val currentUiState = _uiState.value
//
//        if (currentUiState.selectedAgency == null || currentUiState.selectedService.isBlank() ||
//            currentUiState.travelerName.isBlank() || currentUiState.travelerEmail.isBlank()
//        ) {
//            _uiState.update { it.copy(errorMessage = "Please fill all required fields.") }
//            return
//        }
//
//        // Basic validation for number of travelers
//        val numTravelers = currentUiState.numberOfTravelers.toIntOrNull()
//        if (numTravelers == null || numTravelers <= 0) {
//            _uiState.update { it.copy(errorMessage = "Number of travelers must be a positive number.") }
//            return
//        }
//
//
//        _uiState.update {
//            it.copy(
//                isLoading = true,
//                bookingStatus = Resource.Loading(),
//                errorMessage = null
//            )
//        }
//
//        viewModelScope.launch {
//            // Use BookingRequest (singular)
//            val bookingRequest = BookingRequest(
//                agencyId = currentUiState.selectedAgency.id,
//                serviceId = currentUiState.selectedService, // Simplified: use service name as ID for demo
//                userId = "user_123", // Placeholder user ID - replace with actual user ID
//                bookingDetails = mapOf(
//                    "travelerName" to currentUiState.travelerName,
//                    "travelerEmail" to currentUiState.travelerEmail,
//                    "travelerPhone" to currentUiState.travelerPhone,
//                    "preferredDate" to currentUiState.preferredDate,
//                    "numberOfTravelers" to numTravelers, // Now using the validated Int
//                    "additionalNotes" to currentUiState.additionalNotes
//                ).toString()
//            )
//
//            // Simulate API call
//            delay(2000) // Simulate network delay
//
//            // For demo: randomly succeed or fail
//            val success = (0..1).random() == 0 // 50% chance of success
//            if (success) {
//                val response = BookingResponse(
//                    bookingId = UUID.randomUUID().toString(),
//                    status = "confirmed",
//                    message = "Your booking for ${currentUiState.selectedService} with ${currentUiState.selectedAgency.name} has been confirmed!"
//                )
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        bookingStatus = Resource.Success(response)
//                    )
//                }
//            } else {
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        bookingStatus = Resource.Error("Booking failed. Please try again."),
//                        errorMessage = "Booking failed. Please try again."
//                    )
//                }
//            }
//        }
//    }
//
//    // Dummy data (copied from AgencyDetailViewModel for self-containment)
//    private val dummyAgencies = listOf(
//        Agency(
//            id = "agency_1",
//            name = "African Adventure Tours",
//            description = "Specializing in safaris and cultural tours across East Africa.",
//            logoUrl = "https://picsum.photos/id/100/200/200",
//            rating = 4.8,
//            reviewsCount = 125,
//            contactEmail = "info@africantours.com",
//            contactPhone = "+254712345678",
//            servicesOffered = listOf(
//                "Wildlife Safaris",
//                "Cultural Tours",
//                "Mountaineering Expeditions"
//            ),
//            isVerified = true,
//            minPrice = 150.0,
//            maxPrice = 8000.0
//        ),
//    )
//}
//}
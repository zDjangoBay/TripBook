package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import com.android.tripbook.data.SampleAgency
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.model.Agency
import com.android.tripbook.model.Booking
import com.android.tripbook.model.BookingStep
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BookingViewModel : ViewModel() {
    
    private val _currentStep = MutableStateFlow(BookingStep.DATE_SELECTION)
    val currentStep: StateFlow<BookingStep> = _currentStep.asStateFlow()
    
    private val _booking = MutableStateFlow<Booking?>(null)
    val booking: StateFlow<Booking?> = _booking.asStateFlow()
    
    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip.asStateFlow()
    
    // Sample trip options - in a real app, these would come from an API
    private val _availableOptions = MutableStateFlow<List<TripOption>>(emptyList())
    val availableOptions: StateFlow<List<TripOption>> = _availableOptions.asStateFlow()

    // Add these properties
    private val _availableAgencies = MutableStateFlow<List<Agency>>(emptyList())
    val availableAgencies: StateFlow<List<Agency>> = _availableAgencies.asStateFlow()

    private val _selectedAgency = MutableStateFlow<Agency?>(null)
    val selectedAgency: StateFlow<Agency?> = _selectedAgency.asStateFlow()

    private val _departureTime = MutableStateFlow<String>("")
    val departureTime: StateFlow<String> = _departureTime.asStateFlow()

    // Update booking model to include agency and departure time
    fun updateDepartureTime(time: String) {
        _departureTime.value = time
        _booking.update { current ->
            current?.copy(departureTime = time)
        }
    }


    fun selectAgency(agency: Agency) {
        _selectedAgency.value = agency
        _booking.update { current ->
            current?.copy(agencyId = agency.id)
        }
    }

    fun initBooking(tripId: Int) {
        val selectedTrip = SampleTrips.get().find { it.id == tripId }

        // Load agencies for this trip
        _availableAgencies.value = SampleAgency.getForTrip(tripId)
        // ... rest of existing init

        _trip.value = selectedTrip

        val time = ""
        _booking.value = Booking(tripId = tripId, departureTime = time)
        
        // Initialize with sample options
        _availableOptions.value = listOf(
            TripOption(1, "Guided Tour", "Expert local guide for the duration of your trip", 49.99),
            TripOption(2, "Premium Transportation", "Luxury vehicle for all transfers", 79.99),
            TripOption(3, "Welcome Package", "Local treats and guidebook", 29.99),
            TripOption(4, "Photo Package", "Professional photographer for one day", 99.99)
        )
        
        // Reset to first step
        _currentStep.value = BookingStep.DATE_SELECTION
    }
    
    fun updateDates(startDate: LocalDate, endDate: LocalDate) {
        _booking.update { currentBooking ->
            currentBooking?.copy(
                startDate = startDate,
                endDate = endDate
            )
        }
    }
    
    fun updateTravelerInfo(
        adultCount: Int,
        childCount: Int,
        contactName: String,
        contactEmail: String,
        contactPhone: String,
        specialRequirements: String
    ) {
        _booking.update { currentBooking ->
            currentBooking?.copy(
                adultCount = adultCount,
                childCount = childCount,
                contactName = contactName,
                contactEmail = contactEmail,
                contactPhone = contactPhone,
                specialRequirements = specialRequirements
            )
        }
    }
    
    fun toggleOption(optionId: Int) {
        val updatedOptions = _availableOptions.value.map { option ->
            if (option.id == optionId) {
                option.copy(isSelected = !option.isSelected)
            } else {
                option
            }
        }
        _availableOptions.value = updatedOptions
        
        // Update the booking with selected options
        _booking.update { currentBooking ->
            currentBooking?.copy(
                selectedOptions = updatedOptions.filter { it.isSelected }
            )
        }
    }
    
    fun updateTermsAgreement(agreed: Boolean) {
        _booking.update { currentBooking ->
            currentBooking?.copy(agreedToTerms = agreed)
        }
    }
    
    fun nextStep() {
        _currentStep.update { currentStep ->
            when (currentStep) {
                BookingStep.DATE_SELECTION -> BookingStep.AGENCY_SELECTION
                BookingStep.AGENCY_SELECTION -> {
                    require(selectedAgency.value != null) { "Agency must be selected" }
                    require(departureTime.value.isNotEmpty()) { "Departure time must be selected" }
                    BookingStep.TRAVELER_INFO
                }
                BookingStep.TRAVELER_INFO -> BookingStep.ADDITIONAL_OPTIONS
                BookingStep.ADDITIONAL_OPTIONS -> BookingStep.SUMMARY
                BookingStep.SUMMARY -> BookingStep.SUMMARY // Stay on summary
            }
        }
    }
    
    fun previousStep() {
        _currentStep.update { currentStep ->
            when (currentStep) {
                BookingStep.DATE_SELECTION -> BookingStep.DATE_SELECTION
                BookingStep.AGENCY_SELECTION -> BookingStep.DATE_SELECTION
                BookingStep.TRAVELER_INFO -> BookingStep.AGENCY_SELECTION
                BookingStep.ADDITIONAL_OPTIONS -> BookingStep.TRAVELER_INFO
                BookingStep.SUMMARY -> BookingStep.ADDITIONAL_OPTIONS
            }
        }
    }
    
    fun calculateTotalPrice(): Double {
        val booking = _booking.value ?: return 0.0
        val basePrice = 99.99 // In a real app, this would come from the trip data
        
        // Calculate number of days
        val numberOfDays = if (booking.startDate != null && booking.endDate != null) {
            ChronoUnit.DAYS.between(booking.startDate, booking.endDate) + 1
        } else {
            0
        }
        
        // Base price per person per day
        val baseCost = basePrice * (booking.adultCount + (booking.childCount * 0.7)) * numberOfDays
        
        // Add options
        val optionsCost = booking.selectedOptions.sumOf { it.price }
        
        return baseCost + optionsCost
    }
    
    fun submitBooking(): Boolean {
        // In a real app, this would send the booking to a server
        // For now, we'll just return true to simulate success
        return true
    }
}

//private fun MutableStateFlow.update(function: (BookingStep) -> Unit) {}

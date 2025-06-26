package com.android.tripbook.service

import com.android.tripbook.model.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class TransportationService {
    
    // Mock data for demonstration
    private val mockFlights = listOf(
        "American Airlines", "Delta", "United", "Southwest", "JetBlue",
        "Emirates", "Lufthansa", "British Airways", "Air France", "KLM"
    )
    
    private val mockTrains = listOf(
        "Amtrak", "Eurostar", "TGV", "ICE", "Shinkansen",
        "Trenitalia", "Renfe", "SBB", "ÖBB", "SNCF"
    )
    
    private val mockBuses = listOf(
        "Greyhound", "Megabus", "FlixBus", "BoltBus", "Peter Pan",
        "National Express", "Eurolines", "ALSA", "Ouibus", "RegioJet"
    )
    
    private val mockCarRentals = listOf(
        "Hertz", "Avis", "Enterprise", "Budget", "Alamo",
        "National", "Thrifty", "Dollar", "Zipcar", "Turo"
    )
    
    private val mockRideServices = listOf(
        "Uber", "Lyft", "Grab", "Ola", "DiDi",
        "Bolt", "Via", "Gett", "Juno", "Cabify"
    )

    // Search for available transportation options
    suspend fun searchTransportation(
        type: TransportationType,
        from: String,
        to: String,
        departureDate: String,
        passengers: Int = 1
    ): List<TransportationOption> {
        delay(1000) // Simulate API call
        
        val providers = when (type) {
            TransportationType.FLIGHT -> mockFlights
            TransportationType.TRAIN -> mockTrains
            TransportationType.BUS -> mockBuses
            TransportationType.CAR_RENTAL -> mockCarRentals
            TransportationType.TAXI_UBER -> mockRideServices
            else -> listOf("Generic Provider")
        }
        
        return providers.take(5).mapIndexed { index, provider ->
            TransportationOption(
                id = UUID.randomUUID().toString(),
                type = type,
                provider = provider,
                departureLocation = from,
                arrivalLocation = to,
                departureTime = generateMockTime(8 + index * 2),
                arrivalTime = generateMockTime(10 + index * 2),
                duration = "${2 + index}h ${30 + index * 10}m",
                price = generateMockPrice(type, index),
                currency = "USD",
                availableSeats = 50 - index * 5,
                amenities = generateMockAmenities(type),
                cancellationPolicy = "Free cancellation up to 24 hours before departure",
                regulations = generateMockRegulations(type)
            )
        }
    }
    
    // Book transportation
    suspend fun bookTransportation(
        option: TransportationOption,
        tripId: String,
        passengers: Int,
        passengerDetails: List<PassengerInfo>
    ): TransportationBooking {
        delay(1500) // Simulate booking process
        
        return TransportationBooking(
            tripId = tripId,
            type = option.type,
            title = "${option.provider} - ${option.departureLocation} to ${option.arrivalLocation}",
            provider = option.provider,
            bookingReference = generateBookingReference(),
            departureLocation = option.departureLocation,
            arrivalLocation = option.arrivalLocation,
            departureDateTime = option.departureTime,
            arrivalDateTime = option.arrivalTime,
            passengers = passengers,
            cost = option.price * passengers,
            currency = option.currency,
            status = BookingStatus.CONFIRMED,
            confirmationDetails = "Booking confirmed. Check-in opens 24 hours before departure.",
            cancellationPolicy = option.cancellationPolicy,
            contactInfo = generateContactInfo(option.provider),
            regulations = option.regulations
        )
    }
    
    // Get transportation regulations for compliance
    fun getTransportationRegulations(type: TransportationType): List<String> {
        return when (type) {
            TransportationType.FLIGHT -> listOf(
                "Valid passport or ID required",
                "Arrive 2 hours early for domestic, 3 hours for international",
                "Liquid restrictions: 3-1-1 rule",
                "Check baggage weight limits",
                "No prohibited items in carry-on"
            )
            TransportationType.TRAIN -> listOf(
                "Valid ticket required",
                "Arrive 30 minutes before departure",
                "ID may be required for international travel",
                "Luggage restrictions may apply"
            )
            TransportationType.BUS -> listOf(
                "Valid ticket required",
                "Arrive 15 minutes before departure",
                "Luggage size and weight restrictions",
                "No hazardous materials"
            )
            TransportationType.CAR_RENTAL -> listOf(
                "Valid driver's license required",
                "Credit card for security deposit",
                "Minimum age requirements (usually 21+)",
                "International driving permit for foreign licenses",
                "Insurance coverage verification"
            )
            TransportationType.TAXI_UBER -> listOf(
                "Valid payment method required",
                "Follow local traffic regulations",
                "Seatbelt usage mandatory",
                "No smoking in vehicle"
            )
            else -> listOf("Follow local transportation regulations")
        }
    }
    
    private fun generateMockTime(hour: Int): String {
        val time = LocalDateTime.now().withHour(hour).withMinute((0..59).random())
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
    private fun generateMockPrice(type: TransportationType, index: Int): Double {
        val basePrice = when (type) {
            TransportationType.FLIGHT -> 200.0
            TransportationType.TRAIN -> 80.0
            TransportationType.BUS -> 40.0
            TransportationType.CAR_RENTAL -> 60.0
            TransportationType.TAXI_UBER -> 25.0
            else -> 50.0
        }
        return basePrice + (index * 20) + (0..50).random()
    }
    
    private fun generateMockAmenities(type: TransportationType): List<String> {
        return when (type) {
            TransportationType.FLIGHT -> listOf("WiFi", "In-flight entertainment", "Meals", "Baggage included")
            TransportationType.TRAIN -> listOf("WiFi", "Power outlets", "Dining car", "Comfortable seating")
            TransportationType.BUS -> listOf("WiFi", "Reclining seats", "Air conditioning", "Restroom")
            TransportationType.CAR_RENTAL -> listOf("GPS", "Air conditioning", "Automatic transmission", "Insurance")
            TransportationType.TAXI_UBER -> listOf("Air conditioning", "GPS tracking", "Cashless payment")
            else -> listOf("Standard service")
        }
    }
    
    private fun generateMockRegulations(type: TransportationType): List<String> {
        return getTransportationRegulations(type).take(3)
    }
    
    private fun generateBookingReference(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
    
    private fun generateContactInfo(provider: String): String {
        return "Contact $provider customer service: 1-800-XXX-XXXX or support@${provider.lowercase().replace(" ", "")}.com"
    }
}

// Data classes for transportation search and booking
data class TransportationOption(
    val id: String,
    val type: TransportationType,
    val provider: String,
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val price: Double,
    val currency: String,
    val availableSeats: Int,
    val amenities: List<String>,
    val cancellationPolicy: String,
    val regulations: List<String>
)

data class PassengerInfo(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val passportNumber: String = "",
    val nationality: String = ""
)

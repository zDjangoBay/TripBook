package com.android.tripbook.datamining.data.model

/**
 * Model representing an airport with tourist-relevant information
 */
data class Airport(
    val id: Long,
    val name: String,
    val iataCode: String,
    val icaoCode: String,
    val city: String,
    val country: String,
    val countryCode: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Int, // in feet
    val timezone: String,
    val isActive: Boolean = true,
    
    // Tourist-relevant information
    val distanceFromCityCenter: Double, // in kilometers
    val transportationOptions: List<TransportationOption>,
    val terminals: List<Terminal>,
    val amenities: List<Amenity>,
    val hasWifi: Boolean,
    val hasCurrencyExchange: Boolean,
    val immigrationInfo: String,
    
    // Flight information
    val majorAirlines: List<String>,
    val popularDestinations: List<String>,
    val seasonalNotes: String,
    val knownIssues: String,
    
    // Additional metadata
    val imageUrl: String? = null,
    val websiteUrl: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Model representing a transportation option from the airport
 */
data class TransportationOption(
    val type: TransportationType,
    val name: String,
    val estimatedCost: String, // e.g., "$10-15" or "200-300 MAD"
    val currency: String,
    val duration: String, // e.g., "30-45 min"
    val frequency: String, // e.g., "Every 15 min" or "On demand"
    val operatingHours: String, // e.g., "24/7" or "6:00-23:00"
    val notes: String
)

/**
 * Types of transportation available from airports
 */
enum class TransportationType {
    TAXI,
    RIDESHARE,
    BUS,
    SHUTTLE,
    TRAIN,
    METRO,
    FERRY,
    RENTAL_CAR,
    WALKING
}

/**
 * Model representing an airport terminal
 */
data class Terminal(
    val name: String, // e.g., "Terminal 1" or "International Terminal"
    val airlines: List<String>,
    val amenities: List<Amenity>,
    val gates: String, // e.g., "1-25" or "A1-A20"
    val notes: String
)

/**
 * Model representing an airport amenity
 */
data class Amenity(
    val type: AmenityType,
    val name: String,
    val location: String, // e.g., "Terminal 1, Level 2" or "Near Gate A15"
    val operatingHours: String, // e.g., "24/7" or "6:00-23:00"
    val description: String,
    val rating: Float? = null // 1-5 scale
)

/**
 * Types of amenities available at airports
 */
enum class AmenityType {
    RESTAURANT,
    CAFE,
    BAR,
    SHOP,
    DUTY_FREE,
    LOUNGE,
    CURRENCY_EXCHANGE,
    ATM,
    PHARMACY,
    MEDICAL_CENTER,
    PRAYER_ROOM,
    SMOKING_AREA,
    CHILDREN_PLAY_AREA,
    CHARGING_STATION,
    SLEEPING_POD,
    SHOWER,
    LUGGAGE_STORAGE,
    POST_OFFICE,
    INFORMATION_DESK,
    OTHER
}

/**
 * Model representing a simplified airport for list views
 */
data class AirportSummary(
    val id: Long,
    val name: String,
    val iataCode: String,
    val city: String,
    val country: String,
    val region: String,
    val imageUrl: String? = null,
    val majorAirlines: List<String>,
    val hasWifi: Boolean,
    val hasCurrencyExchange: Boolean,
    val rating: Float? = null
)

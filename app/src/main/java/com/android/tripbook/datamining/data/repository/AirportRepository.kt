package com.android.tripbook.datamining.data.repository

import com.android.tripbook.datamining.data.model.Airport
import com.android.tripbook.datamining.data.model.AirportSummary
import com.android.tripbook.datamining.data.model.Amenity
import com.android.tripbook.datamining.data.model.AmenityType
import com.android.tripbook.datamining.data.model.Terminal
import com.android.tripbook.datamining.data.model.TransportationOption
import com.android.tripbook.datamining.data.model.TransportationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Repository for accessing airport data
 */
class AirportRepository {
    
    // In-memory cache of airports
    private val _airports = MutableStateFlow<List<Airport>>(emptyList())
    val airports: Flow<List<Airport>> = _airports.asStateFlow()
    
    // Airport summaries for more efficient list display
    val airportSummaries: Flow<List<AirportSummary>> = airports.map { airportList ->
        airportList.map { airport ->
            AirportSummary(
                id = airport.id,
                name = airport.name,
                iataCode = airport.iataCode,
                city = airport.city,
                country = airport.country,
                region = airport.region,
                imageUrl = airport.imageUrl,
                majorAirlines = airport.majorAirlines,
                hasWifi = airport.hasWifi,
                hasCurrencyExchange = airport.hasCurrencyExchange,
                rating = calculateAirportRating(airport)
            )
        }
    }
    
    init {
        // Load sample data
        _airports.value = generateSampleAirports()
    }
    
    /**
     * Get an airport by its ID
     */
    fun getAirportById(id: Long): Flow<Airport?> {
        return airports.map { airportList ->
            airportList.find { it.id == id }
        }
    }
    
    /**
     * Get airports by country
     */
    fun getAirportsByCountry(country: String): Flow<List<Airport>> {
        return airports.map { airportList ->
            airportList.filter { it.country.equals(country, ignoreCase = true) }
        }
    }
    
    /**
     * Get airports by region
     */
    fun getAirportsByRegion(region: String): Flow<List<Airport>> {
        return airports.map { airportList ->
            airportList.filter { it.region.equals(region, ignoreCase = true) }
        }
    }
    
    /**
     * Search airports by name, city, or code
     */
    fun searchAirports(query: String): Flow<List<Airport>> {
        return airports.map { airportList ->
            airportList.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.city.contains(query, ignoreCase = true) ||
                it.iataCode.contains(query, ignoreCase = true) ||
                it.icaoCode.contains(query, ignoreCase = true)
            }
        }
    }
    
    /**
     * Calculate an overall rating for an airport based on its amenities
     */
    private fun calculateAirportRating(airport: Airport): Float {
        val amenityRatings = airport.amenities.mapNotNull { it.rating }
        val terminalRatings = airport.terminals.flatMap { it.amenities }.mapNotNull { it.rating }
        val allRatings = amenityRatings + terminalRatings
        
        return if (allRatings.isNotEmpty()) {
            allRatings.sum() / allRatings.size
        } else {
            0f
        }
    }
    
    /**
     * Generate sample airport data for major African airports
     */
    private fun generateSampleAirports(): List<Airport> {
        return listOf(
            // Cameroon Airports
            createDoualaCameroonAirport(),
            createYaoundeCameroonAirport(),
            createGarouaCameroonAirport(),
            createMarouaCameroonAirport(),
            
            // Other Major African Airports
            createJohannesburgSouthAfricaAirport(),
            createCairoEgyptAirport(),
            createLagosNigeriaAirport(),
            createNairobiKenyaAirport(),
            createCasablancaMoroccoAirport(),
            createAddisAbabaEthiopiaAirport(),
            createDarEsSalaamTanzaniaAirport(),
            createTunisAirport(),
            createAlgiersAirport(),
            createKigaliRwandaAirport(),
            createAccraGhanaAirport(),
            createDakarSenegalAirport(),
            createMaputoMozambiqueAirport(),
            createLuandaAngolaAirport(),
            createEntebbUgandaAirport(),
            createLibrevilleGabonAirport()
        )
    }
    
    // Individual airport creation methods will be implemented below
    // Each method creates a detailed Airport object for a specific airport
    
    private fun createDoualaCameroonAirport(): Airport {
        return Airport(
            id = 1,
            name = "Douala International Airport",
            iataCode = "DLA",
            icaoCode = "FKKD",
            city = "Douala",
            country = "Cameroon",
            countryCode = "CM",
            region = "Central Africa",
            latitude = 4.0061,
            longitude = 9.7192,
            elevation = 33,
            timezone = "Africa/Douala",
            
            distanceFromCityCenter = 5.0,
            transportationOptions = listOf(
                TransportationOption(
                    type = TransportationType.TAXI,
                    name = "Airport Taxi",
                    estimatedCost = "3000-5000 XAF",
                    currency = "XAF",
                    duration = "15-30 min",
                    frequency = "On demand",
                    operatingHours = "24/7",
                    notes = "Official airport taxis are yellow and have fixed rates"
                ),
                TransportationOption(
                    type = TransportationType.BUS,
                    name = "City Bus",
                    estimatedCost = "300-500 XAF",
                    currency = "XAF",
                    duration = "30-45 min",
                    frequency = "Every 30 min",
                    operatingHours = "6:00-20:00",
                    notes = "Buses can be crowded during peak hours"
                )
            ),
            
            terminals = listOf(
                Terminal(
                    name = "Main Terminal",
                    airlines = listOf("Air France", "Brussels Airlines", "Camair-Co", "Ethiopian Airlines", "Kenya Airways", "Royal Air Maroc", "Turkish Airlines"),
                    amenities = listOf(
                        Amenity(
                            type = AmenityType.RESTAURANT,
                            name = "Airport Restaurant",
                            location = "Main Terminal, Level 1",
                            operatingHours = "6:00-22:00",
                            description = "Serves local and international cuisine",
                            rating = 3.5f
                        ),
                        Amenity(
                            type = AmenityType.CURRENCY_EXCHANGE,
                            name = "Forex Bureau",
                            location = "Main Terminal, Arrivals Hall",
                            operatingHours = "7:00-21:00",
                            description = "Currency exchange services",
                            rating = 3.8f
                        )
                    ),
                    gates = "1-10",
                    notes = "The terminal can get crowded during peak hours"
                )
            ),
            
            amenities = listOf(
                Amenity(
                    type = AmenityType.DUTY_FREE,
                    name = "Duty Free Shop",
                    location = "Main Terminal, Departures",
                    operatingHours = "7:00-22:00",
                    description = "Sells perfumes, alcohol, and local souvenirs",
                    rating = 3.2f
                ),
                Amenity(
                    type = AmenityType.ATM,
                    name = "ATM",
                    location = "Main Terminal, Arrivals Hall",
                    operatingHours = "24/7",
                    description = "Multiple bank ATMs available",
                    rating = 4.0f
                )
            ),
            
            hasWifi = true,
            hasCurrencyExchange = true,
            immigrationInfo = "Visa on arrival available for many nationalities. Processing can take 30-60 minutes during peak times.",
            
            majorAirlines = listOf("Air France", "Brussels Airlines", "Camair-Co", "Ethiopian Airlines", "Kenya Airways", "Royal Air Maroc", "Turkish Airlines"),
            popularDestinations = listOf("Paris", "Brussels", "Yaoundé", "Addis Ababa", "Nairobi", "Casablanca", "Istanbul"),
            seasonalNotes = "Busiest during December-January and July-August holiday seasons",
            knownIssues = "Occasional power outages, though the airport has backup generators",
            
            imageUrl = "https://images.unsplash.com/photo-1583664426440-daef00e4ad6d",
            websiteUrl = "https://www.ccaa.aero/en/"
        )
    }
    
    // Additional airport creation methods will be implemented in the repository
    // For brevity, only one detailed example is shown here
    
    private fun createYaoundeCameroonAirport(): Airport {
        // Implementation will be similar to Douala airport but with Yaoundé-specific details
        // This is a placeholder that will be fully implemented
        return Airport(
            id = 2,
            name = "Yaoundé Nsimalen International Airport",
            iataCode = "NSI",
            icaoCode = "FKYS",
            city = "Yaoundé",
            country = "Cameroon",
            countryCode = "CM",
            region = "Central Africa",
            latitude = 3.7226,
            longitude = 11.5533,
            elevation = 2278,
            timezone = "Africa/Douala",
            
            // Placeholder data - will be replaced with accurate information
            distanceFromCityCenter = 16.0,
            transportationOptions = listOf(),
            terminals = listOf(),
            amenities = listOf(),
            hasWifi = true,
            hasCurrencyExchange = true,
            immigrationInfo = "Visa on arrival available for many nationalities",
            
            majorAirlines = listOf("Air France", "Camair-Co", "Ethiopian Airlines"),
            popularDestinations = listOf("Paris", "Douala", "Addis Ababa"),
            seasonalNotes = "Busiest during holiday seasons",
            knownIssues = "Distance from city center can make transportation time-consuming",
            
            imageUrl = "https://images.unsplash.com/photo-1583664426440-daef00e4ad6d"
        )
    }
    
    // Additional airport creation methods will be implemented similarly
    private fun createGarouaCameroonAirport(): Airport = Airport(id = 3, name = "Garoua International Airport", iataCode = "GOU", icaoCode = "FKKR", city = "Garoua", country = "Cameroon", countryCode = "CM", region = "Central Africa", latitude = 9.3359, longitude = 13.3701, elevation = 794, timezone = "Africa/Douala", distanceFromCityCenter = 3.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = false, hasCurrencyExchange = false, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createMarouaCameroonAirport(): Airport = Airport(id = 4, name = "Maroua Salak Airport", iataCode = "MVR", icaoCode = "FKKL", city = "Maroua", country = "Cameroon", countryCode = "CM", region = "Central Africa", latitude = 10.4513, longitude = 14.2574, elevation = 1390, timezone = "Africa/Douala", distanceFromCityCenter = 4.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = false, hasCurrencyExchange = false, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createJohannesburgSouthAfricaAirport(): Airport = Airport(id = 5, name = "O.R. Tambo International Airport", iataCode = "JNB", icaoCode = "FAJS", city = "Johannesburg", country = "South Africa", countryCode = "ZA", region = "Southern Africa", latitude = -26.1392, longitude = 28.246, elevation = 5558, timezone = "Africa/Johannesburg", distanceFromCityCenter = 21.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createCairoEgyptAirport(): Airport = Airport(id = 6, name = "Cairo International Airport", iataCode = "CAI", icaoCode = "HECA", city = "Cairo", country = "Egypt", countryCode = "EG", region = "North Africa", latitude = 30.1219, longitude = 31.4056, elevation = 382, timezone = "Africa/Cairo", distanceFromCityCenter = 15.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    // Placeholder methods for other airports
    private fun createLagosNigeriaAirport(): Airport = Airport(id = 7, name = "Murtala Muhammed International Airport", iataCode = "LOS", icaoCode = "DNMM", city = "Lagos", country = "Nigeria", countryCode = "NG", region = "West Africa", latitude = 6.5774, longitude = 3.3214, elevation = 135, timezone = "Africa/Lagos", distanceFromCityCenter = 14.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createNairobiKenyaAirport(): Airport = Airport(id = 8, name = "Jomo Kenyatta International Airport", iataCode = "NBO", icaoCode = "HKJK", city = "Nairobi", country = "Kenya", countryCode = "KE", region = "East Africa", latitude = -1.3192, longitude = 36.9278, elevation = 5330, timezone = "Africa/Nairobi", distanceFromCityCenter = 15.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createCasablancaMoroccoAirport(): Airport = Airport(id = 9, name = "Mohammed V International Airport", iataCode = "CMN", icaoCode = "GMMN", city = "Casablanca", country = "Morocco", countryCode = "MA", region = "North Africa", latitude = 33.3675, longitude = -7.5899, elevation = 656, timezone = "Africa/Casablanca", distanceFromCityCenter = 30.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createAddisAbabaEthiopiaAirport(): Airport = Airport(id = 10, name = "Bole International Airport", iataCode = "ADD", icaoCode = "HAAB", city = "Addis Ababa", country = "Ethiopia", countryCode = "ET", region = "East Africa", latitude = 8.9778, longitude = 38.7993, elevation = 7657, timezone = "Africa/Addis_Ababa", distanceFromCityCenter = 6.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    // Additional placeholder methods for other airports
    private fun createDarEsSalaamTanzaniaAirport(): Airport = Airport(id = 11, name = "Julius Nyerere International Airport", iataCode = "DAR", icaoCode = "HTDA", city = "Dar es Salaam", country = "Tanzania", countryCode = "TZ", region = "East Africa", latitude = -6.8781, longitude = 39.2026, elevation = 182, timezone = "Africa/Dar_es_Salaam", distanceFromCityCenter = 12.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createTunisAirport(): Airport = Airport(id = 12, name = "Tunis-Carthage International Airport", iataCode = "TUN", icaoCode = "DTTA", city = "Tunis", country = "Tunisia", countryCode = "TN", region = "North Africa", latitude = 36.851, longitude = 10.2272, elevation = 22, timezone = "Africa/Tunis", distanceFromCityCenter = 8.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createAlgiersAirport(): Airport = Airport(id = 13, name = "Houari Boumediene Airport", iataCode = "ALG", icaoCode = "DAAG", city = "Algiers", country = "Algeria", countryCode = "DZ", region = "North Africa", latitude = 36.691, longitude = 3.2155, elevation = 82, timezone = "Africa/Algiers", distanceFromCityCenter = 16.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createKigaliRwandaAirport(): Airport = Airport(id = 14, name = "Kigali International Airport", iataCode = "KGL", icaoCode = "HRYR", city = "Kigali", country = "Rwanda", countryCode = "RW", region = "East Africa", latitude = -1.9686, longitude = 30.1395, elevation = 4859, timezone = "Africa/Kigali", distanceFromCityCenter = 10.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createAccraGhanaAirport(): Airport = Airport(id = 15, name = "Kotoka International Airport", iataCode = "ACC", icaoCode = "DGAA", city = "Accra", country = "Ghana", countryCode = "GH", region = "West Africa", latitude = 5.6052, longitude = -0.1668, elevation = 205, timezone = "Africa/Accra", distanceFromCityCenter = 10.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createDakarSenegalAirport(): Airport = Airport(id = 16, name = "Blaise Diagne International Airport", iataCode = "DSS", icaoCode = "GOBD", city = "Dakar", country = "Senegal", countryCode = "SN", region = "West Africa", latitude = 14.67, longitude = -17.0734, elevation = 290, timezone = "Africa/Dakar", distanceFromCityCenter = 47.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createMaputoMozambiqueAirport(): Airport = Airport(id = 17, name = "Maputo International Airport", iataCode = "MPM", icaoCode = "FQMA", city = "Maputo", country = "Mozambique", countryCode = "MZ", region = "Southern Africa", latitude = -25.9208, longitude = 32.5726, elevation = 145, timezone = "Africa/Maputo", distanceFromCityCenter = 6.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createLuandaAngolaAirport(): Airport = Airport(id = 18, name = "Quatro de Fevereiro International Airport", iataCode = "LAD", icaoCode = "FNLU", city = "Luanda", country = "Angola", countryCode = "AO", region = "Southern Africa", latitude = -8.8583, longitude = 13.2312, elevation = 243, timezone = "Africa/Luanda", distanceFromCityCenter = 4.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createEntebbUgandaAirport(): Airport = Airport(id = 19, name = "Entebbe International Airport", iataCode = "EBB", icaoCode = "HUEN", city = "Entebbe", country = "Uganda", countryCode = "UG", region = "East Africa", latitude = 0.0423, longitude = 32.4435, elevation = 3782, timezone = "Africa/Kampala", distanceFromCityCenter = 35.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
    
    private fun createLibrevilleGabonAirport(): Airport = Airport(id = 20, name = "Léon-Mba International Airport", iataCode = "LBV", icaoCode = "FOOL", city = "Libreville", country = "Gabon", countryCode = "GA", region = "Central Africa", latitude = 0.4586, longitude = 9.4127, elevation = 39, timezone = "Africa/Libreville", distanceFromCityCenter = 11.0, transportationOptions = listOf(), terminals = listOf(), amenities = listOf(), hasWifi = true, hasCurrencyExchange = true, immigrationInfo = "", majorAirlines = listOf(), popularDestinations = listOf(), seasonalNotes = "", knownIssues = "", imageUrl = null)
}

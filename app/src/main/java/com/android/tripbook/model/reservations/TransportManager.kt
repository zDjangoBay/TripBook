import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

// --- Enums ---

enum class TransportType {
    PLANE,
    SHIP,
    CAR
}

enum class TravelClass {
    ECONOMY,
    PREMIUM_ECONOMY,
    BUSINESS,
    FIRST,
    STANDARD, // For Car or basic ship cabin
    DELUXE // For Car or upgraded ship cabin
}

enum class AddOn(val displayName: String) {
    EXTRA_BAGGAGE("Extra Baggage"),
    TRAVEL_INSURANCE("Travel Insurance"),
    MEAL_UPGRADE("Meal Upgrade"),
    WIFI_ACCESS("Wi-Fi Access"),
    GPS_NAVIGATION("GPS Navigation (Car)"),
    CHILD_SEAT("Child Seat (Car)"),
    PRIORITY_BOARDING("Priority Boarding")
}

// --- Data Classes ---

data class TransportOption(
    val id: String,
    val type: TransportType,
    val provider: String, // e.g., "Airline X", "Ferry Co Y", "Rental Z"
    val origin: String,
    val destination: String,
    val departureDateTime: LocalDateTime,
    val arrivalDateTime: LocalDateTime,
    val capacity: Int, // Max passengers for this option
    val basePricePerKmOrDay: Double, // Per km for Plane/Ship, Per day for Car
    val availableClasses: List<TravelClass>,
    val supportedAddOns: List<AddOn>
)

data class PriceQuote(
    val transportOption: TransportOption,
    val travelClass: TravelClass,
    val passengerCount: Int,
    val selectedAddOns: List<AddOn>,
    val distanceKm: Double?, // Nullable if not applicable (e.g. daily car rental)
    val rentalDays: Int?, // Nullable if not applicable
    val totalPrice: Double,
    val priceBreakdown: Map<String, Double>
)

// --- Business Logic Layer ---

class TransportManager(
    private val availableTransportOptions: List<TransportOption>
) {

    companion object {
        // Realistic distance calculation would use a Geo API
        // This is a simplified mock for demonstration
        fun estimateDistanceKm(origin: String, destination: String): Double {
            return when ("${origin.uppercase()}-${destination.uppercase()}") {
                "NYC-LON" -> 5570.0
                "LON-NYC" -> 5570.0
                "PAR-ROM" -> 1105.0
                "ROM-PAR" -> 1105.0
                "TOK-SYD" -> 7825.0
                "SYD-TOK" -> 7825.0
                "LAX-SFO" -> 545.0
                "SFO-LAX" -> 545.0
                "BER-MUN" -> 504.0 // Berlin to Munich (Car)
                "MUN-BER" -> 504.0
                else -> ((origin.length + destination.length) * 100).toDouble()
            }
        }

        // Realistic day calculation
        fun calculateRentalDays(
            pickupDate: LocalDate,
            returnDate: LocalDate
        ): Int {
            if (returnDate.isBefore(pickupDate)) return 0
            return java.time.temporal.ChronoUnit.DAYS.between(
                pickupDate,
                returnDate
            )
                .toInt() + 1 // Inclusive of pickup and return day
        }
    }

    fun filterTransport(
        date: LocalDate,
        origin: String,
        destination: String,
        passengerCount: Int
    ): List<TransportOption> {
        return availableTransportOptions.filter { option ->
            val departureDate = option.departureDateTime.toLocalDate()
            val isDateMatch = when (option.type) {
                TransportType.CAR ->
                    // For cars, allow booking if the start date is on or after the query date
                    // and the car is available (more complex availability logic not shown here)
                    !departureDate.isBefore(date) // Simplified: car is available from its listed departureDateTime
                else -> departureDate == date
            }

            isDateMatch &&
                option.origin.equals(origin, ignoreCase = true) &&
                option.destination.equals(destination, ignoreCase = true) &&
                option.capacity >= passengerCount
        }
    }

    fun calculatePrice(
        option: TransportOption,
        travelClass: TravelClass,
        passengerCount: Int,
        selectedAddOns: List<AddOn>,
        // For CAR, date is the pickup date, returnDate is needed for day calculation
        // For PLANE/SHIP, date is the travel date (used for distance if dynamic)
        date: LocalDate,
        returnDate: LocalDate? = null // Only for CAR
    ): PriceQuote {
        if (passengerCount <= 0) {
            throw IllegalArgumentException("Passenger count must be positive.")
        }
        if (!option.availableClasses.contains(travelClass)) {
            throw IllegalArgumentException(
                "Travel class ${travelClass.name} not available for this option."
            )
        }
        selectedAddOns.forEach { addOn ->
            if (!option.supportedAddOns.contains(addOn)) {
                throw IllegalArgumentException(
                    "Add-on ${addOn.displayName} not supported for this option."
                )
            }
        }

        val breakdown = mutableMapOf<String, Double>()
        var currentTotal = 0.0

        var distanceKm: Double? = null
        var rentalDays: Int? = null

        // 1. Base Price
        when (option.type) {
            TransportType.PLANE, TransportType.SHIP -> {
                distanceKm = estimateDistanceKm(option.origin, option.destination)
                val baseFarePerPassenger =
                    option.basePricePerKmOrDay * distanceKm
                breakdown["Base Fare (per passenger)"] =
                    roundToTwoDecimals(baseFarePerPassenger)
                currentTotal += baseFarePerPassenger * passengerCount
            }
            TransportType.CAR -> {
                if (returnDate == null) {
                    throw IllegalArgumentException(
                        "Return date is required for car rental pricing."
                    )
                }
                rentalDays = calculateRentalDays(date, returnDate)
                if (rentalDays <= 0) {
                    throw IllegalArgumentException(
                        "Return date must be after or on pickup date."
                    )
                }
                val baseRentalCost = option.basePricePerKmOrDay * rentalDays
                breakdown["Base Rental Cost (${rentalDays} days)"] =
                    roundToTwoDecimals(baseRentalCost)
                currentTotal += baseRentalCost
                // For cars, passenger count doesn't multiply the base vehicle cost
            }
        }

        // 2. Travel Class Surcharge
        val classMultiplier =
            getClassMultiplier(option.type, travelClass)
        if (classMultiplier > 1.0) {
            val baseForSurcharge = when (option.type) {
                TransportType.PLANE, TransportType.SHIP ->
                    (breakdown["Base Fare (per passenger)"] ?: 0.0) * passengerCount
                TransportType.CAR ->
                    breakdown["Base Rental Cost (${rentalDays} days)"] ?: 0.0
            }
            val surcharge = baseForSurcharge * (classMultiplier - 1.0)
            breakdown["Class Surcharge (${travelClass.name})"] =
                roundToTwoDecimals(surcharge)
            currentTotal += surcharge
        }

        // 3. Add-ons
        var totalAddOnCost = 0.0
        selectedAddOns.forEach { addOn ->
            val addOnPrice = getAddOnPrice(
                addOn,
                option.type,
                travelClass,
                passengerCount,
                rentalDays
            )
            breakdown["Add-on: ${addOn.displayName}"] =
                roundToTwoDecimals(addOnPrice.first) // Price
            totalAddOnCost += addOnPrice.first // Add to total
        }
        if (totalAddOnCost > 0) {
            // The breakdown already contains individual add-on prices
            currentTotal += totalAddOnCost
        }

        breakdown["TOTAL PRICE"] = roundToTwoDecimals(currentTotal)

        return PriceQuote(
            transportOption = option,
            travelClass = travelClass,
            passengerCount = passengerCount,
            selectedAddOns = selectedAddOns,
            distanceKm = distanceKm?.let { roundToTwoDecimals(it) },
            rentalDays = rentalDays,
            totalPrice = roundToTwoDecimals(currentTotal),
            priceBreakdown = breakdown
        )
    }

    private fun getClassMultiplier(
        type: TransportType,
        travelClass: TravelClass
    ): Double {
        return when (type) {
            TransportType.PLANE -> when (travelClass) {
                TravelClass.ECONOMY -> 1.0
                TravelClass.PREMIUM_ECONOMY -> 1.4
                TravelClass.BUSINESS -> 2.2
                TravelClass.FIRST -> 3.5
                else -> 1.0 // Default for unhandled plane classes
            }
            TransportType.SHIP -> when (travelClass) {
                TravelClass.STANDARD -> 1.0 // e.g. basic cabin
                TravelClass.DELUXE -> 1.6 // e.g. cabin with window/balcony
                TravelClass.BUSINESS -> 2.0 // e.g. suite
                else -> 1.0
            }
            TransportType.CAR -> when (travelClass) {
                TravelClass.STANDARD -> 1.0 // e.g. compact car
                TravelClass.DELUXE -> 1.5 // e.g. SUV or luxury sedan
                else -> 1.0
            }
        }
    }

    private fun getAddOnPrice(
        addOn: AddOn,
        type: TransportType,
        travelClass: TravelClass,
        passengerCount: Int,
        rentalDays: Int?
    ): Pair<Double, String> { // Returns (Total Price for AddOn, Unit Description)
        // Prices can be dynamic based on type, class, duration, etc.
        // This is a simplified model.
        val unitPrice = when (addOn) {
            AddOn.EXTRA_BAGGAGE -> if (type == TransportType.PLANE) 50.0 else 30.0
            AddOn.TRAVEL_INSURANCE -> 25.0 // Per person
            AddOn.MEAL_UPGRADE -> if (travelClass == TravelClass.ECONOMY || travelClass == TravelClass.PREMIUM_ECONOMY) 30.0 else 0.0 // Free for higher classes
            AddOn.WIFI_ACCESS -> if (type == TransportType.PLANE || type == TransportType.SHIP) 10.0 else 0.0 // N/A for car usually
            AddOn.GPS_NAVIGATION -> if (type == TransportType.CAR) 15.0 else 0.0 // Per rental for car
            AddOn.CHILD_SEAT -> if (type == TransportType.CAR) 10.0 else 0.0 // Per rental for car
            AddOn.PRIORITY_BOARDING -> if (type == TransportType.PLANE || type == TransportType.SHIP) 20.0 else 0.0 // Per person
        }

        return when (addOn) {
            AddOn.TRAVEL_INSURANCE, AddOn.PRIORITY_BOARDING, AddOn.MEAL_UPGRADE ->
                Pair(unitPrice * passengerCount, "per person")
            AddOn.EXTRA_BAGGAGE -> // Assume per item, but for simplicity, let's say 1 per passenger
                Pair(unitPrice * passengerCount, "per item (assumed 1 per passenger)")
            AddOn.WIFI_ACCESS -> // Could be per device or per trip. Let's say per person for flight/ship.
                if (type == TransportType.PLANE || type == TransportType.SHIP) Pair(unitPrice * passengerCount, "per person")
                else Pair(0.0, "N/A")
            AddOn.GPS_NAVIGATION, AddOn.CHILD_SEAT -> // Per rental for cars
                if (type == TransportType.CAR) Pair(unitPrice * (rentalDays ?: 1), "per rental period")
                else Pair(0.0, "N/A")
        }
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return (value * 100).roundToInt() / 100.0
    }
}

// --- Example Usage (typically in a different file/layer like Application Service or UI) ---
fun main() {
    val sampleTransportOptions = listOf(
        TransportOption(
            id = "FL001", type = TransportType.PLANE, provider = "SkyHigh Airlines",
            origin = "NYC", destination = "LON",
            departureDateTime = LocalDateTime.of(2025, 7, 15, 18, 0),
            arrivalDateTime = LocalDateTime.of(2025, 7, 16, 6, 0),
            capacity = 200, basePricePerKmOrDay = 0.12, // $0.12 per KM
            availableClasses = listOf(TravelClass.ECONOMY, TravelClass.BUSINESS, TravelClass.FIRST),
            supportedAddOns = listOf(AddOn.EXTRA_BAGGAGE, AddOn.TRAVEL_INSURANCE, AddOn.MEAL_UPGRADE, AddOn.WIFI_ACCESS, AddOn.PRIORITY_BOARDING)
        ),
        TransportOption(
            id = "SH002", type = TransportType.SHIP, provider = "Oceanic Ferries",
            origin = "NYC", destination = "LON", // A very long trip!
            departureDateTime = LocalDateTime.of(2025, 7, 20, 12, 0),
            arrivalDateTime = LocalDateTime.of(2025, 7, 27, 12, 0),
            capacity = 500, basePricePerKmOrDay = 0.08, // $0.08 per KM
            availableClasses = listOf(TravelClass.STANDARD, TravelClass.DELUXE, TravelClass.BUSINESS),
            supportedAddOns = listOf(AddOn.EXTRA_BAGGAGE, AddOn.TRAVEL_INSURANCE, AddOn.MEAL_UPGRADE, AddOn.WIFI_ACCESS)
        ),
        TransportOption(
            id = "CR003", type = TransportType.CAR, provider = "RoadRunner Rentals",
            origin = "BER", destination = "MUN", // Destination here is more like a typical drop-off zone for pricing
            departureDateTime = LocalDateTime.of(2025, 8, 1, 9, 0), // Represents availability start
            arrivalDateTime = LocalDateTime.of(2025, 12, 31, 18, 0), // Represents general availability end
            capacity = 5, basePricePerKmOrDay = 60.0, // $60 per DAY
            availableClasses = listOf(TravelClass.STANDARD, TravelClass.DELUXE),
            supportedAddOns = listOf(AddOn.TRAVEL_INSURANCE, AddOn.GPS_NAVIGATION, AddOn.CHILD_SEAT)
        ),
        TransportOption(
            id = "FL004", type = TransportType.PLANE, provider = "Budget Wings",
            origin = "LAX", destination = "SFO",
            departureDateTime = LocalDateTime.of(2025, 7, 15, 10, 0),
            arrivalDateTime = LocalDateTime.of(2025, 7, 15, 11, 30),
            capacity = 150, basePricePerKmOrDay = 0.10,
            availableClasses = listOf(TravelClass.ECONOMY, TravelClass.PREMIUM_ECONOMY),
            supportedAddOns = listOf(AddOn.EXTRA_BAGGAGE, AddOn.TRAVEL_INSURANCE, AddOn.PRIORITY_BOARDING)
        )
    )

    val transportManager = TransportManager(sampleTransportOptions)

    println("--- Filtering Example ---")
    val travelDate = LocalDate.of(2025, 7, 15)
    val origin = "NYC"
    val destination = "LON"
    val passengers = 2

    val filteredOptions = transportManager.filterTransport(travelDate, origin, destination, passengers)
    if (filteredOptions.isNotEmpty()) {
        println("Available options for $origin to $destination on $travelDate for $passengers passengers:")
        filteredOptions.forEach { println("  - ${it.provider} (${it.type}), ID: ${it.id}") }

        println("\n--- Pricing Example (Plane) ---")
        val selectedPlane = filteredOptions.first { it.type == TransportType.PLANE }
        try {
            val planeQuote = transportManager.calculatePrice(
                option = selectedPlane,
                travelClass = TravelClass.BUSINESS,
                passengerCount = passengers,
                selectedAddOns = listOf(AddOn.EXTRA_BAGGAGE, AddOn.TRAVEL_INSURANCE),
                date = travelDate
            )
            println("Quote for ${selectedPlane.provider} (${planeQuote.travelClass}):")
            planeQuote.priceBreakdown.forEach { (item, price) ->
                println(String.format("  %-40s: $%.2f", item, price))
            }
            println("Distance: ${planeQuote.distanceKm} km")

        } catch (e: IllegalArgumentException) {
            println("Error pricing plane: ${e.message}")
        }
    } else {
        println("No options found for the given criteria.")
    }

    println("\n--- Pricing Example (Car) ---")
    val carPickupDate = LocalDate.of(2025, 8, 5)
    val carReturnDate = LocalDate.of(2025, 8, 10)
    val carOrigin = "BER"
    val carDestination = "MUN" // For car, origin/destination in filter might be pickup/dropoff city
    val carPassengers = 4

    val filteredCars = transportManager.filterTransport(carPickupDate, carOrigin, carDestination, carPassengers)
    if (filteredCars.isNotEmpty()) {
        val selectedCar = filteredCars.firstOrNull { it.type == TransportType.CAR }
        if (selectedCar != null) {
            try {
                val carQuote = transportManager.calculatePrice(
                    option = selectedCar,
                    travelClass = TravelClass.DELUXE,
                    passengerCount = carPassengers, // Used for capacity check, not direct price multiplier for base car cost
                    selectedAddOns = listOf(AddOn.GPS_NAVIGATION, AddOn.CHILD_SEAT, AddOn.TRAVEL_INSURANCE),
                    date = carPickupDate,
                    returnDate = carReturnDate
                )
                println("\nQuote for ${selectedCar.provider} (${carQuote.travelClass}) from $carPickupDate to $carReturnDate:")
                carQuote.priceBreakdown.forEach { (item, price) ->
                    println(String.format("  %-40s: $%.2f", item, price))
                }
                println("Rental Days: ${carQuote.rentalDays}")
            } catch (e: IllegalArgumentException) {
                println("Error pricing car: ${e.message}")
            }
        } else {
             println("No car options found for the given criteria.")
        }
    } else {
        println("No car options found for $carOrigin to $carDestination on $carPickupDate for $carPassengers passengers.")
    }
}
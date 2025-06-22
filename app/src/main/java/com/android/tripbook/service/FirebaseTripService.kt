package com.android.tripbook.service

import android.util.Log
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.model.Location
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

/**
 * Service class for handling Firebase Firestore operations related to Trip data.
 * Provides methods to save and retrieve trips with comprehensive error handling.
 */
class FirebaseTripService {

    companion object {
        private const val TAG = "FirebaseTripService"
        private const val TRIPS_COLLECTION = "trips"

        // Error messages
        private const val ERROR_SAVE_TRIP = "Failed to save trip to Firestore"
        private const val ERROR_FETCH_TRIPS = "Failed to fetch trips from Firestore"
        private const val ERROR_PARSE_TRIP = "Failed to parse trip document"
        private const val ERROR_PARSE_ITINERARY = "Failed to parse itinerary item"
        private const val ERROR_INVALID_TRIP_DATA = "Invalid trip data provided"
    }

    private val db: FirebaseFirestore by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firestore instance", e)
            throw IllegalStateException("Firestore initialization failed", e)
        }
    }

    private val tripsCollection by lazy { db.collection(TRIPS_COLLECTION) }

    /**
     * Saves a new trip to Firestore with comprehensive validation and error handling.
     * @param trip The trip object to save
     * @return Result<Unit> indicating success or failure
     */
    suspend fun saveTrip(trip: Trip): Result<Unit> {
        return try {
            // Validate trip data before saving
            validateTripData(trip)

            val tripMap = createTripMap(trip)

            tripsCollection.document(trip.id).set(tripMap).await()
            Log.d(TAG, "Trip saved successfully: ${trip.name} (ID: ${trip.id})")
            Result.success(Unit)

        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "$ERROR_INVALID_TRIP_DATA: ${e.message}", e)
            Result.failure(IllegalArgumentException("$ERROR_INVALID_TRIP_DATA: ${e.message}", e))
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "$ERROR_SAVE_TRIP - Firestore error: ${e.message}", e)
            Result.failure(FirebaseFirestoreException("$ERROR_SAVE_TRIP: ${e.message}", e.code, e))
        } catch (e: Exception) {
            Log.e(TAG, "$ERROR_SAVE_TRIP: ${e.message}", e)
            Result.failure(Exception("$ERROR_SAVE_TRIP: ${e.message}", e))
        }
    }

    /**
     * Fetches all trips from Firestore with enhanced error handling and data validation.
     * @return Result<List<Trip>> containing the list of trips or error information
     */
    suspend fun getTrips(): Result<List<Trip>> {
        return try {
            val querySnapshot = tripsCollection.get().await()

            if (querySnapshot.isEmpty) {
                Log.d(TAG, "No trips found in Firestore")
                return Result.success(emptyList())
            }

            val trips = mutableListOf<Trip>()
            var failedDocuments = 0

            querySnapshot.documents.forEach { document ->
                try {
                    val trip = parseDocumentToTrip(document.data ?: emptyMap(), document.id)
                    trips.add(trip)
                } catch (e: Exception) {
                    failedDocuments++
                    Log.w(TAG, "Failed to parse document ${document.id}: ${e.message}", e)
                }
            }

            if (failedDocuments > 0) {
                Log.w(TAG, "Successfully parsed ${trips.size} trips, failed to parse $failedDocuments documents")
            } else {
                Log.d(TAG, "Successfully fetched ${trips.size} trips")
            }

            Result.success(trips)

        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "$ERROR_FETCH_TRIPS - Firestore error: ${e.message}", e)
            Result.failure(FirebaseFirestoreException("$ERROR_FETCH_TRIPS: ${e.message}", e.code, e))
        } catch (e: Exception) {
            Log.e(TAG, "$ERROR_FETCH_TRIPS: ${e.message}", e)
            Result.failure(Exception("$ERROR_FETCH_TRIPS: ${e.message}", e))
        }
    }

    /**
     * Validates trip data before saving to ensure data integrity.
     * @param trip The trip to validate
     * @throws IllegalArgumentException if validation fails
     */
    private fun validateTripData(trip: Trip) {
        when {
            trip.id.isBlank() -> throw IllegalArgumentException("Trip ID cannot be blank")
            trip.name.isBlank() -> throw IllegalArgumentException("Trip name cannot be blank")
            trip.destination.isBlank() -> throw IllegalArgumentException("Trip destination cannot be blank")
            trip.travelers < 0 -> throw IllegalArgumentException("Number of travelers cannot be negative")
            trip.budget < 0 -> throw IllegalArgumentException("Budget cannot be negative")
            trip.startDate.isAfter(trip.endDate) -> throw IllegalArgumentException("Start date cannot be after end date")
        }
    }

    /**
     * Creates a HashMap representation of the Trip object for Firestore storage.
     * @param trip The trip to convert
     * @return HashMap<String, Any?> representing the trip data
     */
    private fun createTripMap(trip: Trip): HashMap<String, Any?> {
        return hashMapOf(
            "id" to trip.id,
            "name" to trip.name,
            "startDate" to convertLocalDateToDate(trip.startDate),
            "endDate" to convertLocalDateToDate(trip.endDate),
            "destination" to trip.destination,
            "travelers" to trip.travelers,
            "budget" to trip.budget,
            "status" to trip.status.name,
            "type" to trip.type,
            "description" to trip.description,
            "activities" to trip.activities,
            "expenses" to trip.expenses,
            "travelersList" to trip.travelersList,
            "itinerary" to trip.itinerary.map { createItineraryItemMap(it) },
            "destinationCoordinates" to trip.destinationCoordinates?.let { createLocationMap(it) },
            "mapCenter" to trip.mapCenter?.let { createLocationMap(it) }
        )
    }

    /**
     * Creates a HashMap representation of an ItineraryItem for Firestore storage.
     */
    private fun createItineraryItemMap(item: ItineraryItem): HashMap<String, Any?> {
        return hashMapOf(
            "date" to convertLocalDateToDate(item.date),
            "time" to item.time,
            "title" to item.title,
            "location" to item.location,
            "type" to item.type.name,
            "notes" to item.notes,
            "coordinates" to item.coordinates?.let { createLocationMap(it) },
            "agencyService" to item.agencyService?.let { createAgencyServiceMap(it) }
        )
    }

    /**
     * Creates a HashMap representation of a Location for Firestore storage.
     */
    private fun createLocationMap(location: Location): HashMap<String, Any?> {
        return hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "name" to location.name,
            "address" to location.address,
            "rating" to location.rating,
            "types" to location.types,
            "placeId" to location.placeId,
            "phoneNumber" to location.phoneNumber,
            "website" to location.website,
            "openingHours" to location.openingHours,
            "priceLevel" to location.priceLevel
        )
    }

    /**
     * Creates a HashMap representation of an AgencyService for Firestore storage.
     */
    private fun createAgencyServiceMap(service: AgencyService): HashMap<String, Any?> {
        return hashMapOf(
            "id" to service.id,
            "name" to service.name,
            "type" to service.type,
            "description" to service.description,
            "price" to service.price,
            "rating" to service.rating,
            "location" to service.location,
            "isFromApi" to service.isFromApi,
            "photoUrl" to service.photoUrl,
            "distance" to service.distance,
            "serviceCode" to service.serviceCode
        )
    }

    /**
     * Converts a Firestore document to a Trip object with enhanced error handling.
     */
    private fun parseDocumentToTrip(data: Map<String, Any>, documentId: String): Trip {
        return try {
            Trip(
                id = documentId,
                name = data["name"] as? String ?: "",
                startDate = parseFirestoreDate(data["startDate"]) ?: LocalDate.MIN,
                endDate = parseFirestoreDate(data["endDate"]) ?: LocalDate.MAX,
                destination = data["destination"] as? String ?: "",
                travelers = (data["travelers"] as? Long)?.toInt() ?: 0,
                budget = (data["budget"] as? Long)?.toInt() ?: 0,
                status = parseEnum(data["status"] as? String, TripStatus.PLANNED),
                type = data["type"] as? String ?: "",
                description = data["description"] as? String ?: "",
                activities = data["activities"] as? List<String> ?: emptyList(),
                expenses = data["expenses"] as? List<String> ?: emptyList(),
                travelersList = data["travelersList"] as? List<String> ?: emptyList(),
                itinerary = parseItinerary(data["itinerary"]),
                destinationCoordinates = parseLocation(data["destinationCoordinates"]),
                mapCenter = parseLocation(data["mapCenter"])
            )
        } catch (e: Exception) {
            throw Exception("$ERROR_PARSE_TRIP (ID: $documentId): ${e.message}", e)
        }
    }

    /**
     * Parses the itinerary list from Firestore data with error handling.
     */
    private fun parseItinerary(itineraryData: Any?): List<ItineraryItem> {
        val itineraryMapList = itineraryData as? List<Map<String, Any>> ?: return emptyList()

        return itineraryMapList.mapNotNull { itemMap ->
            try {
                parseItineraryItem(itemMap)
            } catch (e: Exception) {
                Log.w(TAG, "$ERROR_PARSE_ITINERARY: ${e.message}", e)
                null
            }
        }
    }

    /**
     * Parses a single itinerary item from Firestore data.
     */
    private fun parseItineraryItem(itemMap: Map<String, Any>): ItineraryItem {
        return ItineraryItem(
            date = parseFirestoreDate(itemMap["date"]) ?: LocalDate.MIN,
            time = itemMap["time"] as? String ?: "",
            title = itemMap["title"] as? String ?: "",
            location = itemMap["location"] as? String ?: "",
            type = parseEnum(itemMap["type"] as? String, ItineraryType.ACTIVITY),
            notes = itemMap["notes"] as? String ?: "",
            agencyService = parseAgencyService(itemMap["agencyService"]),
            coordinates = parseLocation(itemMap["coordinates"]),
        )
    }

    /**
     * Parses Location data from Firestore.
     */
    private fun parseLocation(locationData: Any?): Location? {
        val locationMap = locationData as? Map<String, Any> ?: return null

        return try {
            Location(
                latitude = locationMap["latitude"] as? Double ?: 0.0,
                longitude = locationMap["longitude"] as? Double ?: 0.0,
                name = locationMap["name"] as? String ?: "",
                address = locationMap["address"] as? String ?: "",
                rating = locationMap["rating"] as? Double ?: 0.0,
                types = locationMap["types"] as? List<String> ?: emptyList(),
                placeId = locationMap["placeId"] as? String,
                phoneNumber = locationMap["phoneNumber"] as? String,
                website = locationMap["website"] as? String,
                openingHours = locationMap["openingHours"] as? List<String>,
                priceLevel = (locationMap["priceLevel"] as? Long)?.toInt()
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse location data: ${e.message}", e)
            null
        }
    }

    /**
     * Parses AgencyService data from Firestore.
     */
    private fun parseAgencyService(serviceData: Any?): AgencyService? {
        val serviceMap = serviceData as? Map<String, Any> ?: return null

        return try {
            AgencyService(
                id = serviceMap["id"] as? String ?: "",
                name = serviceMap["name"] as? String ?: "",
                type = serviceMap["type"] as? String ?: "",
                description = serviceMap["description"] as? String ?: "",
                price = (serviceMap["price"] as? Long)?.toInt() ?: 0,
                rating = (serviceMap["rating"] as? Double)?.toFloat() ?: 0.0f,
                location = serviceMap["location"] as? String ?: "",
                isFromApi = serviceMap["isFromApi"] as? Boolean ?: false,
                photoUrl = serviceMap["photoUrl"] as? String,
                distance = serviceMap["distance"] as? String,
                serviceCode = serviceMap["serviceCode"] as? String ?: "DEFAULT"
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse agency service data: ${e.message}", e)
            null
        }
    }

    /**
     * Safely parses enum values with fallback to default.
     */
    private inline fun <reified T : Enum<T>> parseEnum(value: String?, default: T): T {
        return try {
            value?.let { enumValueOf<T>(it) } ?: default
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Invalid enum value '$value' for ${T::class.simpleName}, using default: $default")
            default
        }
    }

    /**
     * Parses Firestore Timestamp to LocalDate with null safety.
     */
    private fun parseFirestoreDate(dateData: Any?): LocalDate? {
        return try {
            when (dateData) {
                is com.google.firebase.Timestamp -> {
                    dateData.toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
                }
                is Date -> {
                    dateData.toInstant().atZone(ZoneOffset.UTC).toLocalDate()
                }
                else -> null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse date: ${e.message}", e)
            null
        }
    }

    /**
     * Converts LocalDate to Date for Firestore storage.
     */
    private fun convertLocalDateToDate(localDate: LocalDate): Date {
        return Date.from(localDate.atStartOfDay().toInstant(ZoneOffset.UTC))
    }
}
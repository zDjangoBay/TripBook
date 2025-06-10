package com.android.tripbook.service

import android.util.Log
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.model.Location
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType // Import ItineraryType
// No direct import needed for AgencyService here if it's defined in the same package (com.android.tripbook.service)
// but explicitly importing it for clarity in case you move it later.
// import com.android.tripbook.service.AgencyService // If AgencyService was in a different file/package


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

class FirebaseTripService {

    private val db = FirebaseFirestore.getInstance()
    private val tripsCollection = db.collection("trips") // "trips" is your collection name in Firestore

    // Function to save a new trip to Firestore
    suspend fun saveTrip(trip: Trip): Result<Unit> {
        return try {
            val tripMap = hashMapOf(
                "id" to trip.id,
                "name" to trip.name,
                "startDate" to Date.from(trip.startDate.atStartOfDay().toInstant(ZoneOffset.UTC)),
                "endDate" to Date.from(trip.endDate.atStartOfDay().toInstant(ZoneOffset.UTC)),
                "destination" to trip.destination,
                "travelers" to trip.travelers,
                "budget" to trip.budget,
                "status" to trip.status.name,
                "type" to trip.type,
                "description" to trip.description,
                "activities" to trip.activities,
                "expenses" to trip.expenses,
                "travelersList" to trip.travelersList,
                "itinerary" to trip.itinerary.map { item ->
                    hashMapOf(
                        "date" to Date.from(item.date.atStartOfDay().toInstant(ZoneOffset.UTC)),
                        "time" to item.time,
                        "title" to item.title,
                        "location" to item.location,
                        "type" to item.type.name,
                        "notes" to item.notes,
                        "coordinates" to item.coordinates?.let {
                            hashMapOf(
                                "latitude" to it.latitude,
                                "longitude" to it.longitude,
                                "name" to it.name,
                                "address" to it.address,
                                "rating" to it.rating,
                                "types" to it.types,
                                "placeId" to it.placeId,
                                "phoneNumber" to it.phoneNumber,
                                "website" to it.website,
                                "openingHours" to it.openingHours,
                                "priceLevel" to it.priceLevel
                            )
                        },
                        "agencyService" to item.agencyService?.let { service ->
                            hashMapOf(
                                "id" to service.id,
                                "name" to service.name,
                                "type" to service.type,       // Mapped to 'type'
                                "description" to service.description,
                                "price" to service.price,     // Mapped to 'price'
                                "rating" to service.rating,
                                "location" to service.location,
                                "isFromApi" to service.isFromApi,
                                "photoUrl" to service.photoUrl,
                                "distance" to service.distance,
                                "serviceCode" to service.serviceCode
                                // Removed 'category', 'contact', 'website', 'pricing' as they don't exist in your AgencyService
                            )
                        }
                    )
                },
                "destinationCoordinates" to trip.destinationCoordinates?.let {
                    hashMapOf(
                        "latitude" to it.latitude,
                        "longitude" to it.longitude,
                        "name" to it.name,
                        "address" to it.address,
                        "rating" to it.rating,
                        "types" to it.types,
                        "placeId" to it.placeId,
                        "phoneNumber" to it.phoneNumber,
                        "website" to it.website,
                        "openingHours" to it.openingHours,
                        "priceLevel" to it.priceLevel
                    )
                },
                "mapCenter" to trip.mapCenter?.let {
                    hashMapOf(
                        "latitude" to it.latitude,
                        "longitude" to it.longitude,
                        "name" to it.name,
                        "address" to it.address,
                        "rating" to it.rating,
                        "types" to it.types,
                        "placeId" to it.placeId,
                        "phoneNumber" to it.phoneNumber,
                        "website" to it.website,
                        "openingHours" to it.openingHours,
                        "priceLevel" to it.priceLevel
                    )
                }
            )
            tripsCollection.document(trip.id).set(tripMap).await()
            Log.d("FirebaseTripService", "Trip saved: ${trip.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseTripService", "Error saving trip: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Function to fetch all trips from Firestore
    suspend fun getTrips(): Result<List<Trip>> {
        return try {
            val querySnapshot = tripsCollection.get().await()
            val trips = querySnapshot.documents.mapNotNull { document ->
                try {
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val startDateTimestamp = document.getTimestamp("startDate")
                    val startDate = startDateTimestamp?.toDate()?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate() ?: LocalDate.MIN

                    val endDateTimestamp = document.getTimestamp("endDate")
                    val endDate = endDateTimestamp?.toDate()?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate() ?: LocalDate.MAX

                    val destination = document.getString("destination") ?: ""
                    val travelers = document.getLong("travelers")?.toInt() ?: 0
                    val budget = document.getLong("budget")?.toInt() ?: 0
                    val status = TripStatus.valueOf(document.getString("status") ?: TripStatus.PLANNED.name)
                    val type = document.getString("type") ?: ""
                    val description = document.getString("description") ?: ""
                    val activities = document.get("activities") as? List<String> ?: emptyList()
                    val expenses = document.get("expenses") as? List<String> ?: emptyList()
                    val travelersList = document.get("travelersList") as? List<String> ?: emptyList()


                    val itineraryMapList = document.get("itinerary") as? List<Map<String, Any>> ?: emptyList()
                    val itinerary = itineraryMapList.mapNotNull { itemMap ->
                        try {
                            val itemDateTimestamp = itemMap["date"] as? com.google.firebase.Timestamp
                            val itemDate = itemDateTimestamp?.toDate()?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate() ?: LocalDate.MIN
                            val itemTime = itemMap["time"] as? String ?: ""
                            val itemTitle = itemMap["title"] as? String ?: ""
                            val itemLocation = itemMap["location"] as? String ?: ""
                            val itemType = ItineraryType.valueOf(itemMap["type"] as? String ?: ItineraryType.ACTIVITY.name)
                            val itemNotes = itemMap["notes"] as? String ?: ""

                            val coordsMap = itemMap["coordinates"] as? Map<String, Any>
                            val coordinates = coordsMap?.let {
                                Location(
                                    latitude = it["latitude"] as? Double ?: 0.0,
                                    longitude = it["longitude"] as? Double ?: 0.0,
                                    name = it["name"] as? String ?: "",
                                    address = it["address"] as? String ?: "",
                                    rating = it["rating"] as? Double ?: 0.0,
                                    types = it["types"] as? List<String> ?: emptyList(),
                                    placeId = it["placeId"] as? String,
                                    phoneNumber = it["phoneNumber"] as? String,
                                    website = it["website"] as? String,
                                    openingHours = it["openingHours"] as? List<String>,
                                    priceLevel = (it["priceLevel"] as? Long)?.toInt()
                                )
                            }
                            val agencyServiceMap = itemMap["agencyService"] as? Map<String, Any>
                            val agencyService = agencyServiceMap?.let {
                                AgencyService(
                                    id = it["id"] as? String ?: "",
                                    name = it["name"] as? String ?: "",
                                    type = it["type"] as? String ?: "", // Mapped from 'type'
                                    description = it["description"] as? String ?: "",
                                    price = (it["price"] as? Long)?.toInt() ?: 0, // Mapped from 'price' (Firestore reads Ints as Longs sometimes)
                                    rating = (it["rating"] as? Double)?.toFloat() ?: 0.0f,
                                    location = it["location"] as? String ?: "",
                                    isFromApi = it["isFromApi"] as? Boolean ?: false,
                                    photoUrl = it["photoUrl"] as? String,
                                    distance = it["distance"] as? String,
                                    serviceCode = it["serviceCode"] as? String ?: "DEFAULT"
                                )
                            }

                            ItineraryItem(itemDate, itemTime, itemTitle, itemLocation, itemType, itemNotes, agencyService, coordinates, null)
                        } catch (e: Exception) {
                            Log.e("FirebaseTripService", "Error parsing ItineraryItem: ${e.message}", e)
                            null
                        }
                    }

                    val destCoordsMap = document.get("destinationCoordinates") as? Map<String, Any>
                    val destinationCoordinates = destCoordsMap?.let {
                        Location(
                            latitude = it["latitude"] as? Double ?: 0.0,
                            longitude = it["longitude"] as? Double ?: 0.0,
                            name = it["name"] as? String ?: "",
                            address = it["address"] as? String ?: "",
                            rating = it["rating"] as? Double ?: 0.0,
                            types = it["types"] as? List<String> ?: emptyList(),
                            placeId = it["placeId"] as? String,
                            phoneNumber = it["phoneNumber"] as? String,
                            website = it["website"] as? String,
                            openingHours = it["openingHours"] as? List<String>,
                            priceLevel = (it["priceLevel"] as? Long)?.toInt()
                        )
                    }

                    val mapCenterMap = document.get("mapCenter") as? Map<String, Any>
                    val mapCenter = mapCenterMap?.let {
                        Location(
                            latitude = it["latitude"] as? Double ?: 0.0,
                            longitude = it["longitude"] as? Double ?: 0.0,
                            name = it["name"] as? String ?: "",
                            address = it["address"] as? String ?: "",
                            rating = it["rating"] as? Double ?: 0.0,
                            types = it["types"] as? List<String> ?: emptyList(),
                            placeId = it["placeId"] as? String,
                            phoneNumber = it["phoneNumber"] as? String,
                            website = it["website"] as? String,
                            openingHours = it["openingHours"] as? List<String>,
                            priceLevel = (it["priceLevel"] as? Long)?.toInt()
                        )
                    }

                    Trip(
                        id = id,
                        name = name,
                        startDate = startDate,
                        endDate = endDate,
                        destination = destination,
                        travelers = travelers,
                        budget = budget,
                        status = status,
                        type = type,
                        description = description,
                        activities = activities,
                        expenses = expenses,
                        travelersList = travelersList,
                        itinerary = itinerary,
                        destinationCoordinates = destinationCoordinates,
                        mapCenter = mapCenter
                    )
                } catch (e: Exception) {
                    Log.e("FirebaseTripService", "Error converting document to Trip: ${document.id} - ${e.message}", e)
                    null
                }
            }
            Log.d("FirebaseTripService", "Fetched ${trips.size} trips.")
            Result.success(trips)
        } catch (e: Exception) {
            Log.e("FirebaseTripService", "Error fetching trips: ${e.message}", e)
            Result.failure(e)
        }
    }
}
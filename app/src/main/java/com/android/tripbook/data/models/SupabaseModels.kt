package com.android.tripbook.data.models



import com.android.tripbook.model.Trip
import com.android.tripbook.model.TravelCompanion
import com.android.tripbook.model.TripCategory
import com.android.tripbook.model.TripStatus
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.model.Location
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Serializable models for Supabase database operations

@Serializable
data class SupabaseTravelCompanion(
    val id: String? = null,
    val trip_id: String,
    val name: String,
    val email: String = "",
    val phone: String = "",
    val created_at: String? = null
) {
    fun toTravelCompanion(): TravelCompanion {
        return TravelCompanion(
            name = name,
            email = email,
            phone = phone
        )
    }

    companion object {
        fun fromTravelCompanion(companion: TravelCompanion, tripId: String): SupabaseTravelCompanion {
            return SupabaseTravelCompanion(
                trip_id = tripId,
                name = companion.name,
                email = companion.email,
                phone = companion.phone
            )
        }
    }
}

@Serializable
data class SupabaseTrip(
    val id: String? = null,
    val name: String,
    val start_date: String, // ISO date string
    val end_date: String,   // ISO date string
    val destination: String,
    val travelers: Int,
    val budget: Int,
    val status: String,     // TripStatus as string
    val category: String,   // TripCategory as string
    val description: String = "",
    val created_at: String? = null,
    val updated_at: String? = null
) {
    fun toTrip(companions: List<TravelCompanion> = emptyList()): Trip {
        return Trip(
            id = id ?: "",
            name = name,
            startDate = parseDate(start_date) ?: LocalDate.now(),
            endDate = parseDate(end_date) ?: LocalDate.now().plusDays(1),
            destination = destination,
            travelers = travelers,
            budget = budget,
            status = parseStatus(status),
            category = parseCategory(category),
            description = description,
            companions = companions
        )
    }

    companion object {
        fun fromTrip(trip: Trip): SupabaseTrip {
            return SupabaseTrip(
                id = trip.id.takeIf { it.isNotEmpty() },
                name = trip.name,
                start_date = trip.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                end_date = trip.endDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                destination = trip.destination,
                travelers = trip.travelers,
                budget = trip.budget,
                status = trip.status.name,
                category = trip.category.name,
                description = trip.description
            )
        }

        private fun parseDate(dateString: String): LocalDate? {
            return try {
                LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: DateTimeParseException) {
                // Try alternative formats if needed
                try {
                    LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } catch (e2: DateTimeParseException) {
                    null
                }
            }
        }

        private fun parseStatus(statusString: String): TripStatus {
            return try {
                TripStatus.valueOf(statusString.uppercase())
            } catch (e: IllegalArgumentException) {
                // Fallback to a default status if parsing fails
                TripStatus.PLANNED // Assuming PLANNING is a valid default status
            }
        }

        private fun parseCategory(categoryString: String): TripCategory {
            return try {
                TripCategory.valueOf(categoryString.uppercase())
            } catch (e: IllegalArgumentException) {
                // Fallback to a default category if parsing fails
                TripCategory.ADVENTURE // Assuming LEISURE is a valid default category
            }
        }
    }
}

@Serializable
data class SupabaseItineraryItem(
    val id: String? = null,
    val trip_id: String,
    val date: String, // ISO date string
    val time: String,
    val title: String,
    val location: String,
    val type: String, // ItineraryType as string
    val notes: String = "",
    val description: String = "",
    val duration: String = "",
    val cost: Double = 0.0,
    val is_completed: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String = "",
    val place_id: String = "",
    val created_at: String? = null,
    val updated_at: String? = null
) {
    fun toItineraryItem(): ItineraryItem {
        return ItineraryItem(
            id = id ?: "",
            tripId = trip_id,
            date = parseDate(date) ?: LocalDate.now(),
            time = time,
            title = title,
            location = location,
            type = parseItineraryType(type),
            notes = notes,
            description = description,
            duration = duration,
            cost = cost,
            isCompleted = is_completed,
            coordinates = createLocation()
        )
    }

    private fun createLocation(): Location? {
        return if (latitude != null && longitude != null &&
            latitude >= -90.0 && latitude <= 90.0 &&
            longitude >= -180.0 && longitude <= 180.0) {
            Location(latitude, longitude, address, place_id)
        } else null
    }

    companion object {
        fun fromItineraryItem(item: ItineraryItem): SupabaseItineraryItem {
            return SupabaseItineraryItem(
                id = item.id.takeIf { it.isNotEmpty() },
                trip_id = item.tripId,
                date = item.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time = item.time,
                title = item.title,
                location = item.location,
                type = item.type.name,
                notes = item.notes,
                description = item.description,
                duration = item.duration,
                cost = item.cost,
                is_completed = item.isCompleted,
                latitude = item.coordinates?.latitude,
                longitude = item.coordinates?.longitude,
                address = item.coordinates?.address ?: "",
                place_id = item.coordinates?.placeId ?: ""
            )
        }

        private fun parseDate(dateString: String): LocalDate? {
            return try {
                LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: DateTimeParseException) {
                try {
                    LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } catch (e2: DateTimeParseException) {
                    null
                }
            }
        }

        private fun parseItineraryType(typeString: String): ItineraryType {
            return try {
                ItineraryType.valueOf(typeString.uppercase())
            } catch (e: IllegalArgumentException) {
                // Fallback to a default type if parsing fails
                ItineraryType.ACTIVITY // Assuming ACTIVITY is a valid default type
            }
        }
    }
}

// Response models for database operations
@Serializable
data class TripWithCompanions(
    val trip: SupabaseTrip,
    val companions: List<SupabaseTravelCompanion> = emptyList()
) {
    fun toTrip(): Trip {
        return trip.toTrip(companions.map { it.toTravelCompanion() })
    }
}

@Serializable
data class TripWithDetails(
    val trip: SupabaseTrip,
    val companions: List<SupabaseTravelCompanion> = emptyList(),
    val itinerary: List<SupabaseItineraryItem> = emptyList()
) {
    fun toTrip(): Trip {
        return trip.toTrip(companions.map { it.toTravelCompanion() })
            .copy(itinerary = itinerary.map { it.toItineraryItem() })
    }
}

// Extension functions for additional safety
fun String?.toSafeDouble(default: Double = 0.0): Double {
    return this?.toDoubleOrNull() ?: default
}

fun String?.toSafeInt(default: Int = 0): Int {
    return this?.toIntOrNull() ?: default
}

fun String?.toSafeBoolean(default: Boolean = false): Boolean {
    return when (this?.lowercase()) {
        "true", "1", "yes" -> true
        "false", "0", "no" -> false
        else -> default
    }
}
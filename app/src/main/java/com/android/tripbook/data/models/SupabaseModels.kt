package com.android.tripbook.data.models

import com.android.tripbook.model.Trip
import com.android.tripbook.model.TravelCompanion
import com.android.tripbook.model.TripCategory
import com.android.tripbook.model.TripStatus
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
            startDate = LocalDate.parse(start_date),
            endDate = LocalDate.parse(end_date),
            destination = destination,
            travelers = travelers,
            budget = budget,
            status = TripStatus.valueOf(status),
            category = TripCategory.valueOf(category),
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

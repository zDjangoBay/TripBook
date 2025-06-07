package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.models.Trip
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.models.TripCategory
import java.time.LocalDate

/**
 * Room Entity for Trip data
 * 
 * This entity represents the core trip information stored in the SQLite database.
 * It corresponds to the Trip data class used throughout the UI but is optimized
 * for database storage with Room annotations.
 * 
 * Key Features:
 * - Primary key: trip ID (String)
 * - All trip details including dates, locations, pricing
 * - Category stored as String (enum converted)
 * - Timestamps for audit trail
 * 
 * Used by:
 * - DashboardScreen for displaying available trips
 * - ReservationFlow for trip selection
 * - TripDao for database operations
 */
@Entity(
    tableName = "trips",
    indices = [
        androidx.room.Index(value = ["category"]),
        androidx.room.Index(value = ["from_location"]),
        androidx.room.Index(value = ["to_location"]),
        androidx.room.Index(value = ["base_price"]),
        androidx.room.Index(value = ["departure_date"])
    ]
)
data class TripEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "from_location")
    val fromLocation: String,
    
    @ColumnInfo(name = "to_location")
    val toLocation: String,
    
    @ColumnInfo(name = "departure_date")
    val departureDate: LocalDate,
    
    @ColumnInfo(name = "return_date")
    val returnDate: LocalDate,
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    
    @ColumnInfo(name = "base_price")
    val basePrice: Double,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "duration")
    val duration: String,
    
    @ColumnInfo(name = "category")
    val category: String, // TripCategory enum stored as String
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts this database entity to the domain model used in UI
     * This ensures clean separation between database and business logic layers
     */
    fun toDomainModel(): Trip {
        return Trip(
            id = id,
            title = title,
            fromLocation = fromLocation,
            toLocation = toLocation,
            departureDate = departureDate,
            returnDate = returnDate,
            imageUrl = imageUrl,
            basePrice = basePrice,
            description = description,
            duration = duration,
            category = TripCategory.valueOf(category)
        )
    }
    
    companion object {
        /**
         * Creates a TripEntity from the domain model
         * Used when saving Trip objects to the database
         */
        fun fromDomainModel(trip: Trip): TripEntity {
            return TripEntity(
                id = trip.id,
                title = trip.title,
                fromLocation = trip.fromLocation,
                toLocation = trip.toLocation,
                departureDate = trip.departureDate,
                returnDate = trip.returnDate,
                imageUrl = trip.imageUrl,
                basePrice = trip.basePrice,
                description = trip.description,
                duration = trip.duration,
                category = trip.category.name
            )
        }
    }
}

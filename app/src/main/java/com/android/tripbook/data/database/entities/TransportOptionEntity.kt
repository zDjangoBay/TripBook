package com.android.tripbook.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.android.tripbook.data.models.TransportOption
import com.android.tripbook.data.models.TransportType
import java.time.LocalDateTime

/**
 * Room Entity for Transport Options
 * 
 * This entity stores all available transport options for trips.
 * Each transport option is linked to a specific trip and contains
 * scheduling, pricing, and service details.
 * 
 * Key Features:
 * - Foreign key relationship with TripEntity
 * - Support for all transport types (PLANE, CAR, SHIP)
 * - Departure/arrival time tracking
 * - Pricing information
 * - Service descriptions
 * 
 * Used by:
 * - TransportSelectionStep for showing transport types
 * - TransportOptionsStep for specific transport details
 * - ReservationFlow for transport booking
 */
@Entity(
    tableName = "transport_options",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index(value = ["trip_id"]),
        androidx.room.Index(value = ["type"]),
        androidx.room.Index(value = ["price"]),
        androidx.room.Index(value = ["departure_time"])
    ]
)
data class TransportOptionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "trip_id")
    val tripId: String,
    
    @ColumnInfo(name = "type")
    val type: String, // TransportType enum stored as String
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "departure_time")
    val departureTime: LocalDateTime,
    
    @ColumnInfo(name = "arrival_time")
    val arrivalTime: LocalDateTime,
    
    @ColumnInfo(name = "price")
    val price: Double,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean = true,
    
    @ColumnInfo(name = "capacity")
    val capacity: Int? = null,
    
    @ColumnInfo(name = "booked_seats")
    val bookedSeats: Int = 0,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts this database entity to the domain model used in UI
     */
    fun toDomainModel(): TransportOption {
        return TransportOption(
            id = id,
            type = TransportType.valueOf(type),
            name = name,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
            price = price,
            description = description
        )
    }
    
    /**
     * Checks if this transport option has available capacity
     */
    fun hasAvailableCapacity(): Boolean {
        return isAvailable && (capacity == null || bookedSeats < capacity)
    }
    
    companion object {
        /**
         * Creates a TransportOptionEntity from the domain model
         */
        fun fromDomainModel(transportOption: TransportOption, tripId: String): TransportOptionEntity {
            return TransportOptionEntity(
                id = transportOption.id,
                tripId = tripId,
                type = transportOption.type.name,
                name = transportOption.name,
                departureTime = transportOption.departureTime,
                arrivalTime = transportOption.arrivalTime,
                price = transportOption.price,
                description = transportOption.description
            )
        }
    }
}

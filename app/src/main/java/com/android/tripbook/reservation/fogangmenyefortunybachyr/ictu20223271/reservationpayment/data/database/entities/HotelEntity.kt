package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.models.HotelOption

/**
 * Room Entity for Hotel Options
 * 
 * This entity stores hotel information available for booking.
 * Hotels are not tied to specific trips as they can be used
 * across multiple destinations and trip packages.
 * 
 * Key Features:
 * - Hotel details including rating and amenities
 * - Room type and pricing information
 * - Image URLs for UI display
 * - Amenities stored as comma-separated string
 * - Location information for filtering
 * 
 * Used by:
 * - HotelSelectionStep for showing available hotels
 * - SummaryStep for displaying selected hotel
 * - ReservationFlow for hotel booking
 */
@Entity(
    tableName = "hotels",
    indices = [
        androidx.room.Index(value = ["rating"]),
        androidx.room.Index(value = ["price_per_night"]),
        androidx.room.Index(value = ["location"]),
        androidx.room.Index(value = ["is_available"])
    ]
)
data class HotelEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "rating")
    val rating: Int,
    
    @ColumnInfo(name = "room_type")
    val roomType: String,
    
    @ColumnInfo(name = "price_per_night")
    val pricePerNight: Double,
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    
    @ColumnInfo(name = "amenities")
    val amenities: String, // Comma-separated list of amenities
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "location")
    val location: String,
    
    @ColumnInfo(name = "address")
    val address: String? = null,
    
    @ColumnInfo(name = "phone")
    val phone: String? = null,
    
    @ColumnInfo(name = "email")
    val email: String? = null,
    
    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean = true,
    
    @ColumnInfo(name = "total_rooms")
    val totalRooms: Int? = null,
    
    @ColumnInfo(name = "available_rooms")
    val availableRooms: Int? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts this database entity to the domain model used in UI
     */
    fun toDomainModel(): HotelOption {
        return HotelOption(
            id = id,
            name = name,
            rating = rating,
            roomType = roomType,
            pricePerNight = pricePerNight,
            imageUrl = imageUrl,
            amenities = amenities.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            description = description
        )
    }
    
    /**
     * Checks if this hotel has available rooms
     */
    fun hasAvailableRooms(): Boolean {
        return isAvailable && (availableRooms == null || availableRooms > 0)
    }
    
    /**
     * Gets the amenities as a list
     */
    fun getAmenitiesList(): List<String> {
        return amenities.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
    
    companion object {
        /**
         * Creates a HotelEntity from the domain model
         */
        fun fromDomainModel(hotelOption: HotelOption, location: String = ""): HotelEntity {
            return HotelEntity(
                id = hotelOption.id,
                name = hotelOption.name,
                rating = hotelOption.rating,
                roomType = hotelOption.roomType,
                pricePerNight = hotelOption.pricePerNight,
                imageUrl = hotelOption.imageUrl,
                amenities = hotelOption.amenities.joinToString(","),
                description = hotelOption.description,
                location = location
            )
        }
    }
}

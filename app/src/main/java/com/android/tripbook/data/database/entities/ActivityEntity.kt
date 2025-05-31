package com.android.tripbook.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.tripbook.data.models.ActivityCategory
import com.android.tripbook.data.models.ActivityOption

/**
 * Room Entity for Activity Options
 * 
 * This entity stores activity information available for booking
 * at various destinations. Activities can be added to reservations
 * during the booking process.
 * 
 * Key Features:
 * - Activity details including category and duration
 * - Pricing information
 * - Location-based filtering support
 * - Image URLs for UI display
 * - Availability tracking
 * 
 * Used by:
 * - SummaryStep for activity selection
 * - ActivitySelectionFragment (future implementation)
 * - ReservationFlow for activity booking
 */
@Entity(
    tableName = "activities",
    indices = [
        androidx.room.Index(value = ["category"]),
        androidx.room.Index(value = ["price"]),
        androidx.room.Index(value = ["location"]),
        androidx.room.Index(value = ["is_available"])
    ]
)
data class ActivityEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "price")
    val price: Double,
    
    @ColumnInfo(name = "duration")
    val duration: String,
    
    @ColumnInfo(name = "category")
    val category: String, // ActivityCategory enum stored as String
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    
    @ColumnInfo(name = "location")
    val location: String,
    
    @ColumnInfo(name = "address")
    val address: String? = null,
    
    @ColumnInfo(name = "min_participants")
    val minParticipants: Int = 1,
    
    @ColumnInfo(name = "max_participants")
    val maxParticipants: Int? = null,
    
    @ColumnInfo(name = "age_restriction")
    val ageRestriction: String? = null,
    
    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: String? = null, // Easy, Medium, Hard
    
    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean = true,
    
    @ColumnInfo(name = "requires_booking")
    val requiresBooking: Boolean = true,
    
    @ColumnInfo(name = "cancellation_policy")
    val cancellationPolicy: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts this database entity to the domain model used in UI
     */
    fun toDomainModel(): ActivityOption {
        return ActivityOption(
            id = id,
            name = name,
            description = description,
            price = price,
            duration = duration,
            category = ActivityCategory.valueOf(category),
            imageUrl = imageUrl
        )
    }
    
    /**
     * Checks if this activity is available for booking
     */
    fun isBookable(): Boolean {
        return isAvailable && requiresBooking
    }
    
    /**
     * Gets a formatted string for participant requirements
     */
    fun getParticipantInfo(): String {
        return when {
            maxParticipants != null -> "$minParticipants-$maxParticipants participants"
            minParticipants > 1 -> "Min $minParticipants participants"
            else -> "Individual activity"
        }
    }
    
    companion object {
        /**
         * Creates an ActivityEntity from the domain model
         */
        fun fromDomainModel(activityOption: ActivityOption, location: String = ""): ActivityEntity {
            return ActivityEntity(
                id = activityOption.id,
                name = activityOption.name,
                description = activityOption.description,
                price = activityOption.price,
                duration = activityOption.duration,
                category = activityOption.category.name,
                imageUrl = activityOption.imageUrl,
                location = location
            )
        }
    }
}

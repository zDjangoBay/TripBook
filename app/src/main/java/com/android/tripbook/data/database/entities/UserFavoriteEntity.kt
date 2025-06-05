package com.android.tripbook.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Room Entity for User Favorites
 *
 * This entity tracks user favorites including trips, hotels,
 * and activities. It enables personalized recommendations
 * and quick access to preferred options.
 *
 * Key Features:
 * - Multiple favorite types (TRIP, HOTEL, ACTIVITY)
 * - User-specific favorites
 * - Timestamp tracking
 * - Notes for personal reminders
 *
 * Used by:
 * - Dashboard for showing favorite trips
 * - Profile screen for managing favorites
 * - Recommendation engine
 */
@Entity(
    tableName = "user_favorites",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index(value = ["user_id"]),
        androidx.room.Index(value = ["favorite_type"]),
        androidx.room.Index(value = ["favorite_id"]),
        androidx.room.Index(value = ["user_id", "favorite_id", "favorite_type"], unique = true)
    ]
)
data class UserFavoriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "favorite_id")
    val favoriteId: String, // ID of the favorited item (trip, hotel, activity)

    @ColumnInfo(name = "favorite_type")
    val favoriteType: String, // TRIP, HOTEL, ACTIVITY

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "priority")
    val priority: Int = 0, // Higher number = higher priority

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Gets the favorite type as enum
     */
    fun getFavoriteTypeEnum(): FavoriteType {
        return try {
            FavoriteType.valueOf(favoriteType)
        } catch (e: IllegalArgumentException) {
            FavoriteType.TRIP
        }
    }

    /**
     * Checks if this favorite has notes
     */
    fun hasNotes(): Boolean {
        return !notes.isNullOrBlank()
    }

    companion object {
        /**
         * Creates a new favorite entry
         */
        fun create(
            userId: String,
            favoriteId: String,
            favoriteType: FavoriteType,
            notes: String? = null,
            priority: Int = 0
        ): UserFavoriteEntity {
            val id = "fav_${System.currentTimeMillis()}_${(1000..9999).random()}"
            return UserFavoriteEntity(
                id = id,
                userId = userId,
                favoriteId = favoriteId,
                favoriteType = favoriteType.name,
                notes = notes,
                priority = priority
            )
        }
    }
}

/**
 * Types of items that can be favorited
 */
enum class FavoriteType {
    TRIP,
    HOTEL,
    ACTIVITY
}

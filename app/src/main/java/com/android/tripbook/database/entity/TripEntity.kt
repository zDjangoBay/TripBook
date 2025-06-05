package com.android.tripbook.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.android.tripbook.model.Trip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room Entity for Trip data
 * Converts existing Trip model to database entity
 */
@Entity(tableName = "trips")
@TypeConverters(TripEntity.Converters::class)
data class TripEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val caption: String,
    val description: String,
    val imageUrl: List<String>
) {
    /**
     * Convert TripEntity to Trip model for UI layer
     */
    fun toTrip(): Trip {
        return Trip(
            id = id,
            title = title,
            caption = caption,
            description = description,
            imageUrl = imageUrl
        )
    }

    companion object {
        /**
         * Convert Trip model to TripEntity for database storage
         */
        fun fromTrip(trip: Trip): TripEntity {
            return TripEntity(
                id = trip.id,
                title = trip.title,
                caption = trip.caption,
                description = trip.description,
                imageUrl = trip.imageUrl
            )
        }
    }

    /**
     * Type converters for Room database
     */
    class Converters {
        @TypeConverter
        fun fromStringList(value: List<String>): String {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun toStringList(value: String): List<String> {
            val listType = object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(value, listType)
        }
    }
}

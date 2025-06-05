package com.android.tripbook.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.android.tripbook.Model.Review
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room Entity for Review data
 * Converts existing Review model to database entity
 */
@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tripId"])  // ✅ Index on foreign key for optimal performance
    ]
)
@TypeConverters(ReviewEntity.Converters::class)
data class ReviewEntity(
    @PrimaryKey
    val id: Int,
    val tripId: Int,
    val username: String,
    val rating: Int,
    val comment: String,
    val images: List<String>,
    val isLiked: Boolean = false,
    val isFlagged: Boolean = false,
    val likeCount: Int = 0
) {
    /**
     * Convert ReviewEntity to Review model for UI layer
     */
    fun toReview(): Review {
        return Review(
            id = id,
            tripId = tripId,
            username = username,
            rating = rating,
            comment = comment,
            images = images,
            isLiked = isLiked,
            isFlagged = isFlagged,
            likeCount = likeCount
        )
    }

    companion object {
        /**
         * Convert Review model to ReviewEntity for database storage
         */
        fun fromReview(review: Review): ReviewEntity {
            return ReviewEntity(
                id = review.id,
                tripId = review.tripId,
                username = review.username,
                rating = review.rating,
                comment = review.comment,
                images = review.images,
                isLiked = review.isLiked,
                isFlagged = review.isFlagged,
                likeCount = review.likeCount
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

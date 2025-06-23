package com.android.tripbook.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.android.tripbook.model.Comment
import com.android.tripbook.model.CommentReaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room Entity for Comment data
 * Converts existing Comment model to database entity
 */
@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = ReviewEntity::class,
            parentColumns = ["id"],
            childColumns = ["reviewId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["reviewId"])  //  Index on foreign key for optimal performance
    ]
)
@TypeConverters(CommentEntity.Converters::class)
data class CommentEntity(
    @PrimaryKey
    val id: String,
    val reviewId: Int,
    val text: String,
    val imageUri: String? = null,
    val timestamp: String,
    val authorName: String,
    val authorAvatar: String? = null,
    val reactions: Map<String, List<CommentReaction>> = emptyMap(),
    val parentId: String? = null,
    val replies: List<Comment> = emptyList()
) {
    /**
     * Convert CommentEntity to Comment model for UI layer
     */
    fun toComment(): Comment {
        return Comment(
            id = id,
            text = text,
            imageUri = imageUri,
            timestamp = timestamp,
            authorName = authorName,
            authorAvatar = authorAvatar,
            reactions = reactions.mapValues { it.value.toMutableList() }.toMutableMap(),
            parentId = parentId,
            replies = replies.toMutableList()
        )
    }

    companion object {
        /**
         * Convert Comment model to CommentEntity for database storage
         */
        fun fromComment(comment: Comment, reviewId: Int): CommentEntity {
            return CommentEntity(
                id = comment.id,
                reviewId = reviewId,
                text = comment.text,
                imageUri = comment.imageUri,
                timestamp = comment.timestamp,
                authorName = comment.authorName,
                authorAvatar = comment.authorAvatar,
                reactions = comment.reactions,
                parentId = comment.parentId,
                replies = comment.replies
            )
        }
    }

    /**
     * Type converters for Room database
     */
    class Converters {
        @TypeConverter
        fun fromReactionMap(value: Map<String, List<CommentReaction>>): String {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun toReactionMap(value: String): Map<String, List<CommentReaction>> {
            val mapType = object : TypeToken<Map<String, List<CommentReaction>>>() {}.type
            return Gson().fromJson(value, mapType) ?: emptyMap()
        }

        @TypeConverter
        fun fromCommentList(value: List<Comment>): String {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun toCommentList(value: String): List<Comment> {
            val listType = object : TypeToken<List<Comment>>() {}.type
            return Gson().fromJson(value, listType) ?: emptyList()
        }
    }
}

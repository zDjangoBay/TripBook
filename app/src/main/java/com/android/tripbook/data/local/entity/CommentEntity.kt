package com.android.tripbook.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity representing a comment on a post in the local database
 */
@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("postId"),
        Index("userId")
    ]
)
data class CommentEntity(
    @PrimaryKey
    val id: String,
    val postId: String,
    val userId: String,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date
)

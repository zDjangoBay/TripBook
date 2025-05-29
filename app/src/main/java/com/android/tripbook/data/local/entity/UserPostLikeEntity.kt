package com.android.tripbook.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

/**
 * Room entity representing the relationship between a user and a post they liked
 */
@Entity(
    tableName = "user_post_likes",
    primaryKeys = ["userId", "postId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("postId")
    ]
)
data class UserPostLikeEntity(
    val userId: String,
    val postId: String,
    val createdAt: Date
)

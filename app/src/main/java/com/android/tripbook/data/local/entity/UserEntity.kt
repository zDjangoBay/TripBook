package com.android.tripbook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity representing a user in the local database
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val profileImage: String?,
    val bio: String?,
    val createdAt: Date,
    val updatedAt: Date
)

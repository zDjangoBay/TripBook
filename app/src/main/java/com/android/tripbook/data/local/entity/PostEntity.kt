package com.android.tripbook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.android.tripbook.data.local.converter.DataConverters
import java.util.Date

/**
 * Room entity representing a post in the local database
 */
@Entity(tableName = "posts")
@TypeConverters(DataConverters::class)
data class PostEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val images: List<String>,
    val location: String,
    val latitude: Double?,
    val longitude: Double?,
    val tags: List<String>,
    val agencyId: String?,
    val likes: Int,
    val comments: Int,
    val createdAt: Date,
    val updatedAt: Date
)

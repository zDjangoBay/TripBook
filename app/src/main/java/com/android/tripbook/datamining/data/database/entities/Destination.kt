package com.android.tripbook.datamining.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a travel destination with analytics data
 */
@Entity(tableName = "destinations")
data class Destination(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val country: String,
    val region: String,
    val popularity: Float, // 0-100 scale
    val averageRating: Float, // 0-5 scale
    val visitCount: Int,
    val lastUpdated: Date,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String? = null,
    val description: String? = null,
    val tags: String? = null, // Comma-separated tags
    val seasonalityData: String? = null // JSON string with monthly popularity data
)

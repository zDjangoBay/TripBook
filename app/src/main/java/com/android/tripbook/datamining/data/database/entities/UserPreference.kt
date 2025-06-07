package com.android.tripbook.datamining.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing user preferences and behavior for personalization
 */
@Entity(tableName = "user_preferences")
data class UserPreference(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val preferenceType: String, // e.g., "destination_type", "activity", "accommodation"
    val preferenceValue: String,
    val preferenceStrength: Float, // 0-1 scale
    val lastUpdated: Date,
    val source: String, // e.g., "explicit", "implicit", "inferred"
    val confidence: Float // 0-1 scale
)

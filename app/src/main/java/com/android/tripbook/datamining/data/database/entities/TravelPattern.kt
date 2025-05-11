package com.android.tripbook.datamining.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing travel patterns and trends
 */
@Entity(tableName = "travel_patterns")
data class TravelPattern(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String, // Can be "global" for aggregate patterns
    val patternType: String, // e.g., "seasonal", "destination_correlation", "duration"
    val patternName: String,
    val patternValue: Float,
    val patternData: String, // JSON string with detailed pattern data
    val startDate: Date,
    val endDate: Date,
    val confidence: Float, // 0-1 scale indicating confidence in the pattern
    val sampleSize: Int // Number of data points used to identify this pattern
)

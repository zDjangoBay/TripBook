package com.android.tripbook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity representing a travel agency in the local database
 */
@Entity(tableName = "travel_agencies")
data class TravelAgencyEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val logo: String?,
    val website: String?,
    val contactPhone: String?,
    val contactEmail: String?,
    val createdAt: Date,
    val updatedAt: Date
)

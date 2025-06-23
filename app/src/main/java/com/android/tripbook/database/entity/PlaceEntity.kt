package com.android.tripbook.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val picUrl: String = "",
    val isFromFirebase: Boolean = true, // Track source
    val lastUpdated: Long = System.currentTimeMillis()
)

package com.android.tripbook.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "triphome")
data class TriphomeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val companyLogo: String = "",
    val companyName: String = "",
    val arriveTime: String = "",
    val date: String = "",
    val from: String = "",
    val fromshort: String = "",
    val price: Double = 0.0,
    val time: String = "",
    val to: String = "",
    val score: Double = 0.0,
    val toshort: String = "",
    val isFromFirebase: Boolean = true, // Track source
    val lastUpdated: Long = System.currentTimeMillis()
)

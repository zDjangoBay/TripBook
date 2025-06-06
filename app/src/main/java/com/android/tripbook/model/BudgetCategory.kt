 package com.android.tripbook.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budget_categories",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE // If a Trip is deleted, its budget categories are also deleted
        )
    ],
    indices = [Index(value = ["tripId"])] // Index for faster queries on tripId
)
data class BudgetCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: String, // Matches the type of Trip.id
    val name: String,
    val plannedAmount: Double // Using Double for monetary values
)
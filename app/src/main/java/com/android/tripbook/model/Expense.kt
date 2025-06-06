 package com.android.tripbook.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BudgetCategory::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE // If a category is deleted, its expenses are deleted
        )
    ],
    indices = [
        Index(value = ["tripId"]),
        Index(value = ["categoryId"])
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: String,      // Foreign key to Trip
    val categoryId: Long,    // Foreign key to BudgetCategory
    val description: String,
    val amount: Double,
    val date: Long           // Store date as Long (timestamp) for Room simplicity
)
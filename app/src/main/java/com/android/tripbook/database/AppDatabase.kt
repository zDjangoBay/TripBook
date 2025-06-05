package com.android.tripbook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import com.android.tripbook.model.Trip
import com.android.tripbook.model.converters.DateConverter
import com.android.tripbook.model.converters.TripStatusConverter

// Define DAOs later - for now, just declare them
import com.android.tripbook.dao.BudgetCategoryDao
import com.android.tripbook.dao.ExpenseDao
import com.android.tripbook.dao.TripDao

@Database(
    entities = [
        Trip::class,
        BudgetCategory::class,
        Expense::class
    ],
    version = 1, // Start with version 1. Increment if you change schema.
    exportSchema = false // Recommended to disable for non-library apps to avoid version history files
)
@TypeConverters(
    DateConverter::class,
    TripStatusConverter::class
    // Add other converters here if needed, e.g., for List<String>
)
abstract class AppDatabase : RoomDatabase() {

    // Abstract methods for DAOs - will be implemented by Room
    abstract fun tripDao(): TripDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tripbook_database" // Name of the database file
                )
                // Add migrations here if needed in the future
                // .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // Good for development, replace with proper migrations for release
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

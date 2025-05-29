package com.android.tripbook.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from schema version 1 to 2
 * This is a placeholder for future database schema changes
 */
object DatabaseMigrations {
    
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example migration: add a new column to posts table
            // database.execSQL("ALTER TABLE posts ADD COLUMN viewCount INTEGER NOT NULL DEFAULT 0")
        }
    }
    
    // Add more migrations as needed
}

package com.android.tripbook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.tripbook.database.dao.TripDao
import com.android.tripbook.database.dao.ReviewDao
import com.android.tripbook.database.dao.CommentDao
import com.android.tripbook.database.dao.TriphomeDao
import com.android.tripbook.database.dao.PlaceDao
import com.android.tripbook.database.entity.TripEntity
import com.android.tripbook.database.entity.ReviewEntity
import com.android.tripbook.database.entity.CommentEntity
import com.android.tripbook.database.entity.TriphomeEntity
import com.android.tripbook.database.entity.PlaceEntity

/**
 * Room Database for TripBook application
 * This is a PARALLEL implementation that coexists with existing static data
 * Team members can choose to use this or continue with existing SampleTrips/SampleReviews
 */
@Database(
    entities = [
        TripEntity::class,
        ReviewEntity::class,
        CommentEntity::class,
        TriphomeEntity::class,
        PlaceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class TripBookDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun reviewDao(): ReviewDao
    abstract fun commentDao(): CommentDao
    abstract fun triphomeDao(): TriphomeDao
    abstract fun placeDao(): PlaceDao
    
    companion object {
        @Volatile
        private var INSTANCE: TripBookDatabase? = null
        
        /**
         * Get database instance (Singleton pattern)
         * This is OPTIONAL - existing code continues to work without this
         */
        fun getDatabase(context: Context): TripBookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TripBookDatabase::class.java,
                    "tripbook_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

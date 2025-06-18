package com.android.tripbook.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.android.tripbook.data.dao.ReservationDao
import com.android.tripbook.data.models.Reservation
import com.android.tripbook.utils.DateConverters

@Database(
    entities = [Reservation::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class TripBookDatabase : RoomDatabase() {
    
    abstract fun reservationDao(): ReservationDao
    
    companion object {
        @Volatile
        private var INSTANCE: TripBookDatabase? = null
        
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
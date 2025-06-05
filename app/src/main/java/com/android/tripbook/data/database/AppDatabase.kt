package com.android.tripbook.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.tripbook.data.database.dao.*
import com.android.tripbook.data.database.entities.*
import com.android.tripbook.data.database.converters.DateTimeConverters

@Database(
    entities = [
        PaymentTransactionEntity::class,
        NotificationEntity::class,
        UserFavoriteEntity::class,
        ReservationEntity::class,
        TransportOptionEntity::class,
        ReservationActivityEntity::class,
        TripEntity::class,
        UserEntity::class,
        ActivityEntity::class,
        HotelEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun paymentDao(): PaymentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userFavoriteDao(): UserFavoriteDao
    abstract fun transportDao(): TransportDao
    abstract fun reservationActivityDao(): ReservationActivityDao
    abstract fun activityDao(): ActivityDao
    abstract fun reservationDao(): ReservationDao
    abstract fun tripDao(): TripDao
    abstract fun userDao(): UserDao
    abstract fun hotelDao(): HotelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
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

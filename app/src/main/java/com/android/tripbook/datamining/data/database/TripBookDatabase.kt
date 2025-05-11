package com.android.tripbook.datamining.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.tripbook.datamining.data.database.converters.DateConverter
import com.android.tripbook.datamining.data.database.dao.DestinationDao
import com.android.tripbook.datamining.data.database.dao.TravelPatternDao
import com.android.tripbook.datamining.data.database.dao.UserPreferenceDao
import com.android.tripbook.datamining.data.database.entities.Destination
import com.android.tripbook.datamining.data.database.entities.TravelPattern
import com.android.tripbook.datamining.data.database.entities.UserPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Destination::class,
        TravelPattern::class,
        UserPreference::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class TripBookDatabase : RoomDatabase() {

    abstract fun destinationDao(): DestinationDao
    abstract fun travelPatternDao(): TravelPatternDao
    abstract fun userPreferenceDao(): UserPreferenceDao

    companion object {
        @Volatile
        private var INSTANCE: TripBookDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TripBookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TripBookDatabase::class.java,
                    "tripbook_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(TripBookDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class TripBookDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: TripBookDatabase) {
            // Prepopulate with sample data for demonstration
            val destinationDao = database.destinationDao()
            val travelPatternDao = database.travelPatternDao()
            val userPreferenceDao = database.userPreferenceDao()

            // Sample data will be added in a separate implementation
        }
    }
}

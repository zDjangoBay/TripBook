import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [User::class, Trip::class, Agency::class], version = 1)
abstract class TripBookDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
    abstract fun agencyDao(): AgencyDao

    companion object {
        @Volatile
        private var INSTANCE: TripBookDatabase? = null

        fun getDatabase(context: Context): TripBookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TripBookDatabase::class.java,
                    "tripbook_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
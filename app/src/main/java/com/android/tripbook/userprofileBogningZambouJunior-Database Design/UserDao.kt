package app.src.main.java.com.android.tripbook.`userprofileBogningZambouJunior-Database Design`

import Agency
import Trip
import User
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Query("SELECT * FROM trips WHERE userId = :userId")
    suspend fun getTripsByUserId(userId: Long): List<Trip>
}

@Dao
interface AgencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgency(agency: Agency)

    @Query("SELECT * FROM agencies")
    suspend fun getAllAgencies(): List<Agency>
}
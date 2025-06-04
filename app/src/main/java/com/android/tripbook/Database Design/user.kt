import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val username: String,
    val profilePicture: String?
)

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val tripId: Long = 0,
    val userId: Long,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val tips: String?
)

@Entity(tableName = "agencies")
data class Agency(
    @PrimaryKey(autoGenerate = true) val agencyId: Long = 0,
    val name: String,
    val rating: Float,
    val description: String
)
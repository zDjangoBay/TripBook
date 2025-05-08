data class Reservation(
    val id: String,
    val userId: String,
    val busName: String,
    val departure: String,
    val arrival: String,
    val date: String, // ISO format "yyyy-MM-dd"
    val time: String,
    val seatNumber: String,
    val status: ReservationStatus,
    val ticketUrl: String?,
    val contactNumber: String,
    val locationCoordinates: Pair<Double, Double>, // (latitude, longitude)
    val createdAt: Long
)

enum class ReservationStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}

class ReservationDetailsViewModel : ViewModel() {

    private val _reservation = MutableLiveData<Reservation?>()
    val reservation: LiveData<Reservation?> = _reservation

    fun loadReservationById(reservationId: String) {
        // Replace with repository or Firebase query
        _reservation.value = mockFetchReservation(reservationId)
    }

    private fun mockFetchReservation(id: String): Reservation? {
        return Reservation(
            id = id,
            userId = "user1",
            busName = "ExpressLine",
            departure = "Yaoundé",
            arrival = "Douala",
            date = "2025-05-10",
            time = "09:00",
            seatNumber = "A12",
            status = ReservationStatus.UPCOMING,
            ticketUrl = "https://yourserver.com/tickets/$id.pdf",
            contactNumber = "+237123456789",
            locationCoordinates = Pair(3.848, 11.502),
            createdAt = System.currentTimeMillis()
        )
    }
}

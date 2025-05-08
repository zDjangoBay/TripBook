class ReservationsDashboardViewModel : ViewModel() {

    private val _allReservations = MutableLiveData<List<Reservation>>()
    val allReservations: LiveData<List<Reservation>> = _allReservations

    val upcomingReservations: LiveData<List<Reservation>> = Transformations.map(_allReservations) {
        it.filter { res -> res.status == ReservationStatus.UPCOMING }
    }

    val pastReservations: LiveData<List<Reservation>> = Transformations.map(_allReservations) {
        it.filter { res -> res.status == ReservationStatus.COMPLETED }
    }

    fun loadReservations(userId: String) {
        // Replace with repository or Firebase call
        val dummyData = listOf(
            Reservation(
                id = "res123",
                userId = userId,
                busName = "ExpressLine",
                departure = "Yaoundé",
                arrival = "Douala",
                date = "2025-05-10",
                time = "09:00",
                seatNumber = "A12",
                status = ReservationStatus.UPCOMING,
                ticketUrl = "https://yourserver.com/tickets/res123.pdf",
                contactNumber = "+237123456789",
                locationCoordinates = Pair(3.848, 11.502),
                createdAt = System.currentTimeMillis()
            )
        )
        _allReservations.value = dummyData
    }


}

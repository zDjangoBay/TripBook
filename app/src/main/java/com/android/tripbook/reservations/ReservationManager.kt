import java.time.LocalDate
import java.util.UUID
import java.math.BigDecimal

// --- Enums and Data Classes ---

enum class ReservationStatus {
    IN_PROGRESS, // Temporary state while user is building the reservation
    PENDING_CONFIRMATION, // All details added, awaiting final confirmation/payment
    CONFIRMED,
    CANCELLED,
    MODIFIED // If an existing confirmed reservation was changed
}

data class HotelBookingDetails(
    val hotelId: String,
    val roomId: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    var price: BigDecimal? = null,
    var confirmationNumber: String? = null
)

data class TransportBookingDetails(
    val transportId: String, // e.g., flight number, train ID
    val type: String, // "FLIGHT", "TRAIN", "CAR_RENTAL"
    val departureDateTime: LocalDate, // Simplified, use LocalDateTime in real app
    val arrivalDateTime: LocalDate, // Simplified
    var price: BigDecimal? = null,
    var confirmationNumber: String? = null
)

data class ActivityBookingDetails(
    val activityId: String,
    val date: LocalDate,
    val numberOfParticipants: Int,
    var price: BigDecimal? = null,
    var confirmationNumber: String? = null
)

data class Reservation(
    val id: String,
    val userId: String,
    var status: ReservationStatus,
    var hotelBooking: HotelBookingDetails? = null,
    var transportBookings: MutableList<TransportBookingDetails> = mutableListOf(),
    var activityBookings: MutableList<ActivityBookingDetails> = mutableListOf(),
    var totalPrice: BigDecimal = BigDecimal.ZERO,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)

// --- Dependency Interfaces (to be implemented elsewhere) ---

interface HotelManager {
    suspend fun checkAvailabilityAndPrice(
        hotelId: String,
        roomId: String,
        checkIn: LocalDate,
        checkOut: LocalDate
    ): Pair<Boolean, BigDecimal?> // Available, Price

    suspend fun bookHotel(details: HotelBookingDetails): String? // Confirmation number
    suspend fun cancelHotelBooking(confirmationNumber: String): Boolean
}

interface TransportManager {
    suspend fun checkAvailabilityAndPrice(
        transportId: String,
        type: String,
        date: LocalDate
    ): Pair<Boolean, BigDecimal?>

    suspend fun bookTransport(
        details: TransportBookingDetails
    ): String? // Confirmation number

    suspend fun cancelTransportBooking(confirmationNumber: String): Boolean
}

interface ActivityManager {
    suspend fun checkAvailabilityAndPrice(
        activityId: String,
        date: LocalDate,
        participants: Int
    ): Pair<Boolean, BigDecimal?>

    suspend fun bookActivity(
        details: ActivityBookingDetails
    ): String? // Confirmation number

    suspend fun cancelActivityBooking(confirmationNumber: String): Boolean
}

interface ReservationService { // Backend API
    suspend fun submitReservation(reservation: Reservation): Reservation? // Returns confirmed reservation
    suspend fun updateReservation(reservation: Reservation): Reservation?
    suspend fun cancelRemoteReservation(reservationId: String): Boolean
    suspend fun getReservation(reservationId: String): Reservation?
}

interface ReservationDao { // Local DB
    suspend fun saveDraft(reservation: Reservation)
    suspend fun getDraft(reservationId: String): Reservation?
    suspend fun deleteDraft(reservationId: String)
    suspend fun saveConfirmed(reservation: Reservation)
    suspend fun getConfirmed(reservationId: String): Reservation?
    suspend fun updateConfirmed(reservation: Reservation)
    suspend fun getAllUserReservations(userId: String): List<Reservation>
}

interface ProgressTracker {
    fun startFlow(flowName: String)
    fun updateStep(stepName: String, completed: Boolean)
    fun completeFlow(flowName: String)
    fun resetFlow(flowName: String)
}

// --- ReservationManager ---

class ReservationManager(
    private val hotelManager: HotelManager,
    private val transportManager: TransportManager,
    private val activityManager: ActivityManager,
    private val reservationService: ReservationService,
    private val reservationDao: ReservationDao,
    private val progressTracker: ProgressTracker
) {
    private var currentInProgressReservation: Reservation? = null
    private val RESERVATION_FLOW = "RESERVATION_FLOW"

    private fun generateUniqueId(): String = UUID.randomUUID().toString()

    private fun updateTotalCost() {
        currentInProgressReservation?.let { reservation ->
            var total = BigDecimal.ZERO
            reservation.hotelBooking?.price?.let { total = total.add(it) }
            reservation.transportBookings.forEach { it.price?.let { p -> total = total.add(p) } }
            reservation.activityBookings.forEach { it.price?.let { p -> total = total.add(p) } }
            reservation.totalPrice = total
            reservation.updatedAt = System.currentTimeMillis()
        }
    }

    suspend fun startNewReservationSession(userId: String): Reservation {
        val reservationId = generateUniqueId()
        currentInProgressReservation = Reservation(
            id = reservationId,
            userId = userId,
            status = ReservationStatus.IN_PROGRESS
        )
        progressTracker.startFlow(RESERVATION_FLOW)
        progressTracker.updateStep("SESSION_STARTED", true)
        // Optionally save draft immediately
        // currentInProgressReservation?.let { reservationDao.saveDraft(it) }
        return currentInProgressReservation!!
    }

    fun getCurrentReservation(): Reservation? = currentInProgressReservation

    suspend fun addHotelToCurrentReservation(
        hotelId: String,
        roomId: String,
        checkIn: LocalDate,
        checkOut: LocalDate
    ): Reservation? {
        val reservation = currentInProgressReservation ?: throw IllegalStateException(
            "No reservation in progress. Call startNewReservationSession first."
        )
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            println("Error: Check-out date must be after check-in date.")
            return null
        }

        val (available, price) = hotelManager.checkAvailabilityAndPrice(
            hotelId,
            roomId,
            checkIn,
            checkOut
        )
        return if (available && price != null) {
            reservation.hotelBooking = HotelBookingDetails(
                hotelId,
                roomId,
                checkIn,
                checkOut,
                price
            )
            updateTotalCost()
            progressTracker.updateStep("HOTEL_ADDED", true)
            reservationDao.saveDraft(reservation) // Save draft after each significant step
            reservation
        } else {
            println("Hotel $hotelId not available or price couldn't be fetched.")
            progressTracker.updateStep("HOTEL_ADDED", false)
            null
        }
    }

    suspend fun addTransportToCurrentReservation(
        transportId: String,
        type: String,
        date: LocalDate
    ): Reservation? {
        val reservation = currentInProgressReservation ?: throw IllegalStateException(
            "No reservation in progress."
        )
        val (available, price) =
            transportManager.checkAvailabilityAndPrice(transportId, type, date)

        return if (available && price != null) {
            // Simplified date handling for departure/arrival
            val details = TransportBookingDetails(
                transportId,
                type,
                date,
                date.plusDays(if (type == "FLIGHT") 0 else 1), // Dummy arrival
                price
            )
            reservation.transportBookings.add(details)
            updateTotalCost()
            progressTracker.updateStep("TRANSPORT_ADDED", true)
            reservationDao.saveDraft(reservation)
            reservation
        } else {
            println("Transport $transportId not available or price couldn't be fetched.")
            progressTracker.updateStep("TRANSPORT_ADDED", false)
            null
        }
    }

    suspend fun addActivityToCurrentReservation(
        activityId: String,
        date: LocalDate,
        participants: Int
    ): Reservation? {
        val reservation = currentInProgressReservation ?: throw IllegalStateException(
            "No reservation in progress."
        )
        val (available, price) =
            activityManager.checkAvailabilityAndPrice(activityId, date, participants)

        return if (available && price != null) {
            val details = ActivityBookingDetails(activityId, date, participants, price)
            reservation.activityBookings.add(details)
            updateTotalCost()
            progressTracker.updateStep("ACTIVITY_ADDED", true)
            reservationDao.saveDraft(reservation)
            reservation
        } else {
            println("Activity $activityId not available or price couldn't be fetched.")
            progressTracker.updateStep("ACTIVITY_ADDED", false)
            null
        }
    }

    fun validateReservationData(reservation: Reservation): Boolean {
        if (reservation.hotelBooking == null && reservation.transportBookings.isEmpty() && reservation.activityBookings.isEmpty()) {
            println("Validation Error: Reservation must include at least one item (hotel, transport, or activity).")
            return false
        }
        // Add more specific validation rules:
        // e.g., hotel check-in/out dates are logical
        // e.g., transport dates align with hotel dates if both exist
        reservation.hotelBooking?.let {
            if (it.checkOutDate.isBefore(it.checkInDate)) {
                println("Validation Error: Hotel check-out date is before check-in date.")
                return false
            }
        }
        println("Reservation data validated successfully for ID: ${reservation.id}")
        return true
    }

    suspend fun proceedToConfirmation(): Reservation? {
        val reservation = currentInProgressReservation ?: throw IllegalStateException(
            "No reservation in progress to confirm."
        )
        if (!validateReservationData(reservation)) {
            println("Reservation validation failed. Cannot proceed to confirmation.")
            return null
        }
        reservation.status = ReservationStatus.PENDING_CONFIRMATION
        reservation.updatedAt = System.currentTimeMillis()
        reservationDao.saveDraft(reservation) // Update draft with new status
        progressTracker.updateStep("VALIDATION_PASSED", true)
        return reservation
    }

    suspend fun confirmAndBookCurrentReservation(): Reservation? {
        var reservation = currentInProgressReservation ?: throw IllegalStateException(
            "No reservation in progress to confirm."
        )
        if (reservation.status != ReservationStatus.PENDING_CONFIRMATION) {
            println("Reservation is not pending confirmation. Current status: ${reservation.status}")
            val validated = proceedToConfirmation()
            if (validated == null) return null else reservation = validated
        }

        // Simulate actual booking with managers
        try {
            reservation.hotelBooking?.let {
                val conf = hotelManager.bookHotel(it)
                if (conf != null) it.confirmationNumber = conf
                else throw Exception("Failed to book hotel ${it.hotelId}")
            }
            for (transport in reservation.transportBookings) {
                val conf = transportManager.bookTransport(transport)
                if (conf != null) transport.confirmationNumber = conf
                else throw Exception("Failed to book transport ${transport.transportId}")
            }
            for (activity in reservation.activityBookings) {
                val conf = activityManager.bookActivity(activity)
                if (conf != null) activity.confirmationNumber = conf
                else throw Exception("Failed to book activity ${activity.activityId}")
            }
        } catch (e: Exception) {
            println("Error during booking individual items: ${e.message}")
            // Potentially attempt to roll back booked items - complex!
            // For now, just mark as failed and don't proceed.
            progressTracker.updateStep("FINAL_BOOKING_FAILED", true)
            return null // Or return reservation with a FAILED_BOOKING status
        }


        // Submit to backend service (which might handle payment)
        val confirmedReservation = reservationService.submitReservation(reservation)

        return if (confirmedReservation != null) {
            confirmedReservation.status = ReservationStatus.CONFIRMED
            confirmedReservation.updatedAt = System.currentTimeMillis()

            reservationDao.saveConfirmed(confirmedReservation)
            reservationDao.deleteDraft(confirmedReservation.id) // Clean up draft
            currentInProgressReservation = null // Clear current session
            progressTracker.completeFlow(RESERVATION_FLOW)
            println("Reservation ${confirmedReservation.id} confirmed successfully.")
            confirmedReservation
        } else {
            println("Failed to confirm reservation with backend service.")
            // Potentially keep as PENDING_CONFIRMATION or revert to IN_PROGRESS
            // Or add a FAILED_CONFIRMATION status
            progressTracker.updateStep("SERVICE_SUBMIT_FAILED", true)
            null
        }
    }

    suspend fun cancelReservation(reservationId: String): Boolean {
        // Try to cancel an in-progress reservation first
        if (currentInProgressReservation?.id == reservationId) {
            // Potentially inform managers if any pre-bookings/holds were made
            println("Cancelling in-progress reservation: $reservationId")
            currentInProgressReservation = null
            reservationDao.deleteDraft(reservationId)
            progressTracker.resetFlow(RESERVATION_FLOW)
            return true
        }

        // Try to cancel a confirmed reservation
        val reservation = reservationDao.getConfirmed(reservationId)
            ?: reservationService.getReservation(reservationId)

        if (reservation == null) {
            println("Reservation $reservationId not found for cancellation.")
            return false
        }

        if (reservation.status == ReservationStatus.CANCELLED) {
            println("Reservation $reservationId is already cancelled.")
            return true
        }

        // Attempt to cancel individual components
        var allCancelled = true
        reservation.hotelBooking?.confirmationNumber?.let {
            if (!hotelManager.cancelHotelBooking(it)) allCancelled = false
        }
        reservation.transportBookings.forEach {
            it.confirmationNumber?.let { cn ->
                if (!transportManager.cancelTransportBooking(cn)) allCancelled = false
            }
        }
        reservation.activityBookings.forEach {
            it.confirmationNumber?.let { cn ->
                if (!activityManager.cancelActivityBooking(cn)) allCancelled = false
            }
        }

        if (!allCancelled) {
            println("Warning: Not all items for reservation $reservationId could be cancelled with providers.")
            // Decide on policy: still mark as cancelled in our system? Or partial cancellation?
        }

        val serviceCancelSuccess = reservationService.cancelRemoteReservation(reservationId)
        if (serviceCancelSuccess) {
            reservation.status = ReservationStatus.CANCELLED
            reservation.updatedAt = System.currentTimeMillis()
            reservationDao.updateConfirmed(reservation) // Update local status
            println("Reservation $reservationId cancelled successfully.")
            return true
        } else {
            println("Failed to cancel reservation $reservationId with backend service.")
            return false
        }
    }

    // Basic modification: re-initiates the flow with existing data
    // More complex modifications (e.g., changing dates of a confirmed booking)
    // would require more sophisticated logic, potentially involving cancellation and rebooking.
    suspend fun startModificationSession(reservationId: String): Reservation? {
        val existingReservation = reservationDao.getConfirmed(reservationId)
            ?: reservationService.getReservation(reservationId)

        if (existingReservation == null) {
            println("Cannot modify: Reservation $reservationId not found.")
            return null
        }
        if (existingReservation.status == ReservationStatus.CANCELLED) {
            println("Cannot modify: Reservation $reservationId is cancelled.")
            return null
        }

        // Create a mutable copy for modification
        currentInProgressReservation = existingReservation.copy(
            // Create new lists to avoid modifying original confirmed reservation's lists directly
            // until modification is confirmed.
            transportBookings = existingReservation.transportBookings.map { it.copy() }.toMutableList(),
            activityBookings = existingReservation.activityBookings.map { it.copy() }.toMutableList(),
            status = ReservationStatus.IN_PROGRESS // Treat as a new in-progress flow
        )
        // Original ID is kept to link it back upon confirmation of modification.
        // A new ID could be generated if modifications are treated as new bookings.

        progressTracker.startFlow("RESERVATION_MODIFICATION_FLOW")
        progressTracker.updateStep("MODIFICATION_STARTED", true)
        println("Started modification session for reservation ${existingReservation.id}")
        return currentInProgressReservation
    }

    suspend fun confirmModification(): Reservation? {
        val modifiedReservation = currentInProgressReservation ?: throw IllegalStateException(
            "No reservation modification in progress."
        )

        if (!validateReservationData(modifiedReservation)) {
            println("Modified reservation validation failed.")
            return null
        }

        // Here, you'd typically call specific update methods on managers if only parts changed,
        // or cancel and rebook if major changes occurred.
        // For simplicity, we assume the ReservationService handles the diff.
        modifiedReservation.status = ReservationStatus.MODIFIED // Or PENDING_CONFIRMATION again
        modifiedReservation.updatedAt = System.currentTimeMillis()

        val updatedReservation = reservationService.updateReservation(modifiedReservation)

        return if (updatedReservation != null) {
            updatedReservation.status = ReservationStatus.CONFIRMED // Or MODIFIED, then CONFIRMED
            updatedReservation.updatedAt = System.currentTimeMillis()

            reservationDao.saveConfirmed(updatedReservation) // Overwrite or update existing
            reservationDao.deleteDraft(updatedReservation.id) // If any draft existed with this ID
            currentInProgressReservation = null
            progressTracker.completeFlow("RESERVATION_MODIFICATION_FLOW")
            println("Reservation ${updatedReservation.id} modified and confirmed successfully.")
            updatedReservation
        } else {
            println("Failed to confirm modification for reservation ${modifiedReservation.id} with backend service.")
            // Revert status or handle error
            progressTracker.updateStep("MODIFICATION_SERVICE_SUBMIT_FAILED", true)
            null
        }
    }


    fun clearCurrentReservationSession() {
        currentInProgressReservation = null
        progressTracker.resetFlow(RESERVATION_FLOW)
        println("Current reservation session cleared.")
    }

    suspend fun getReservationDetails(reservationId: String): Reservation? {
        return reservationDao.getConfirmed(reservationId)
            ?: reservationService.getReservation(reservationId)
            ?: reservationDao.getDraft(reservationId)
    }

    suspend fun listUserReservations(userId: String): List<Reservation> {
        // This might combine local drafts and confirmed reservations from service/local DB
        val drafts = reservationDao.getAllUserReservations(userId)
            .filter { it.status == ReservationStatus.IN_PROGRESS }
        val confirmed = reservationDao.getAllUserReservations(userId)
            .filter { it.status != ReservationStatus.IN_PROGRESS }
        // In a real app, you might fetch from service too and merge/deduplicate
        return drafts + confirmed
    }
}

// --- Dummy Implementations for Dependencies (for demonstration) ---

class DummyHotelManager : HotelManager {
    override suspend fun checkAvailabilityAndPrice(
        hotelId: String,
        roomId: String,
        checkIn: LocalDate,
        checkOut: LocalDate
    ): Pair<Boolean, BigDecimal?> {
        println("HotelManager: Checking hotel $hotelId, room $roomId from $checkIn to $checkOut")
        return Pair(true, BigDecimal("120.50").multiply(BigDecimal(checkOut.toEpochDay() - checkIn.toEpochDay())))
    }
    override suspend fun bookHotel(details: HotelBookingDetails): String? {
        println("HotelManager: Booking hotel ${details.hotelId} - Confirmed")
        return "HOTEL_CONF_${UUID.randomUUID().toString().take(8)}"
    }
    override suspend fun cancelHotelBooking(confirmationNumber: String): Boolean {
        println("HotelManager: Cancelling hotel booking $confirmationNumber - Success")
        return true
    }
}

class DummyTransportManager : TransportManager {
    override suspend fun checkAvailabilityAndPrice(
        transportId: String,
        type: String,
        date: LocalDate
    ): Pair<Boolean, BigDecimal?> {
        println("TransportManager: Checking $type $transportId on $date")
        return Pair(true, BigDecimal("250.00"))
    }
    override suspend fun bookTransport(details: TransportBookingDetails): String? {
        println("TransportManager: Booking ${details.type} ${details.transportId} - Confirmed")
        return "TRANS_CONF_${UUID.randomUUID().toString().take(8)}"
    }
    override suspend fun cancelTransportBooking(confirmationNumber: String): Boolean {
        println("TransportManager: Cancelling transport $confirmationNumber - Success")
        return true
    }
}

class DummyActivityManager : ActivityManager {
    override suspend fun checkAvailabilityAndPrice(
        activityId: String,
        date: LocalDate,
        participants: Int
    ): Pair<Boolean, BigDecimal?> {
        println("ActivityManager: Checking activity $activityId for $participants on $date")
        return Pair(true, BigDecimal("75.25").multiply(BigDecimal(participants)))
    }
    override suspend fun bookActivity(details: ActivityBookingDetails): String? {
        println("ActivityManager: Booking activity ${details.activityId} - Confirmed")
        return "ACT_CONF_${UUID.randomUUID().toString().take(8)}"
    }
    override suspend fun cancelActivityBooking(confirmationNumber: String): Boolean {
        println("ActivityManager: Cancelling activity $confirmationNumber - Success")
        return true
    }
}

class DummyReservationService : ReservationService {
    private val remoteReservations = mutableMapOf<String, Reservation>()
    override suspend fun submitReservation(reservation: Reservation): Reservation? {
        println("ReservationService: Submitting reservation ${reservation.id} to backend.")
        val confirmed = reservation.copy(status = ReservationStatus.CONFIRMED)
        remoteReservations[confirmed.id] = confirmed
        return confirmed
    }
    override suspend fun updateReservation(reservation: Reservation): Reservation? {
        println("ReservationService: Updating reservation ${reservation.id} on backend.")
        if (remoteReservations.containsKey(reservation.id)) {
            remoteReservations[reservation.id] = reservation.copy(status = ReservationStatus.CONFIRMED) // or MODIFIED
            return remoteReservations[reservation.id]
        }
        return null
    }
    override suspend fun cancelRemoteReservation(reservationId: String): Boolean {
        println("ReservationService: Cancelling reservation $reservationId on backend.")
        return if (remoteReservations.containsKey(reservationId)) {
            remoteReservations[reservationId]?.status = ReservationStatus.CANCELLED
            true
        } else false
    }
    override suspend fun getReservation(reservationId: String): Reservation? {
        println("ReservationService: Fetching reservation $reservationId from backend.")
        return remoteReservations[reservationId]
    }
}

class DummyReservationDao : ReservationDao {
    private val drafts = mutableMapOf<String, Reservation>()
    private val confirmed = mutableMapOf<String, Reservation>()

    override suspend fun saveDraft(reservation: Reservation) {
        println("ReservationDao: Saving draft ${reservation.id}")
        drafts[reservation.id] = reservation
    }
    override suspend fun getDraft(reservationId: String): Reservation? {
        println("ReservationDao: Getting draft ${reservationId}")
        return drafts[reservationId]
    }
    override suspend fun deleteDraft(reservationId: String) {
        println("ReservationDao: Deleting draft ${reservationId}")
        drafts.remove(reservationId)
    }
    override suspend fun saveConfirmed(reservation: Reservation) {
        println("ReservationDao: Saving confirmed ${reservation.id}")
        confirmed[reservation.id] = reservation
    }
    override suspend fun getConfirmed(reservationId: String): Reservation? {
        println("ReservationDao: Getting confirmed ${reservationId}")
        return confirmed[reservationId]
    }
    override suspend fun updateConfirmed(reservation: Reservation) {
        println("ReservationDao: Updating confirmed ${reservation.id}")
        if (confirmed.containsKey(reservation.id)) {
            confirmed[reservation.id] = reservation
        }
    }
    override suspend fun getAllUserReservations(userId: String): List<Reservation> {
        return (drafts.values + confirmed.values).filter { it.userId == userId }
    }
}

class ConsoleProgressTracker : ProgressTracker {
    private var currentFlow: String? = null
    private val steps = mutableMapOf<String, Boolean>()
    override fun startFlow(flowName: String) {
        currentFlow = flowName
        steps.clear()
        println("ProgressTracker: Started flow '$flowName'")
    }
    override fun updateStep(stepName: String, completed: Boolean) {
        steps[stepName] = completed
        println("ProgressTracker: Step '$stepName' in flow '$currentFlow' -> ${if (completed) "DONE" else "PENDING/FAILED"}")
    }
    override fun completeFlow(flowName: String) {
        println("ProgressTracker: Completed flow '$flowName'. Steps: $steps")
        currentFlow = null
        steps.clear()
    }
    override fun resetFlow(flowName: String) {
        println("ProgressTracker: Reset flow '$flowName'")
        if (currentFlow == flowName) {
            currentFlow = null
            steps.clear()
        }
    }
}

// --- Main function for demonstration ---
suspend fun main() {
    val hotelManager = DummyHotelManager()
    val transportManager = DummyTransportManager()
    val activityManager = DummyActivityManager()
    val reservationService = DummyReservationService()
    val reservationDao = DummyReservationDao()
    val progressTracker = ConsoleProgressTracker()

    val reservationManager = ReservationManager(
        hotelManager,
        transportManager,
        activityManager,
        reservationService,
        reservationDao,
        progressTracker
    )

    val userId = "user123"

    // Scenario 1: Create and confirm a reservation
    println("\n--- SCENARIO 1: NEW RESERVATION ---")
    var newReservation = reservationManager.startNewReservationSession(userId)
    println("Started new reservation: ${newReservation.id}")

    reservationManager.addHotelToCurrentReservation(
        "HiltonSF", "QueenRoom",
        LocalDate.now().plusDays(10), LocalDate.now().plusDays(12)
    )
    reservationManager.addTransportToCurrentReservation(
        "UA234", "FLIGHT",
        LocalDate.now().plusDays(10)
    )
    println("Current reservation details: ${reservationManager.getCurrentReservation()}")

    val pendingReservation = reservationManager.proceedToConfirmation()
    if (pendingReservation != null) {
        println("Reservation pending confirmation: $pendingReservation")
        val confirmed = reservationManager.confirmAndBookCurrentReservation()
        if (confirmed != null) {
            println("Reservation Confirmed: $confirmed")
        } else {
            println("Failed to confirm reservation.")
        }
    } else {
        println("Could not proceed to confirmation.")
    }


    // Scenario 2: Start a reservation and cancel it before confirmation
    println("\n--- SCENARIO 2: CANCEL IN-PROGRESS RESERVATION ---")
    val tempReservation = reservationManager.startNewReservationSession(userId)
    reservationManager.addHotelToCurrentReservation(
        "MarriottLA", "KingSuite",
        LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1).plusDays(3)
    )
    println("Temporary reservation: ${reservationManager.getCurrentReservation()}")
    reservationManager.cancelReservation(tempReservation.id)
    println("Is current session cleared? ${reservationManager.getCurrentReservation() == null}")


    // Scenario 3: Modify an existing reservation (simplified: re-book)
    println("\n--- SCENARIO 3: MODIFY EXISTING RESERVATION ---")
    // First, create and confirm one to modify
    var reservationToModify = reservationManager.startNewReservationSession(userId)
    reservationManager.addHotelToCurrentReservation(
        "HyattNYC", "StdRoom",
        LocalDate.now().plusDays(30), LocalDate.now().plusDays(32)
    )
    reservationManager.proceedToConfirmation()
    val originalConfirmed = reservationManager.confirmAndBookCurrentReservation()

    if (originalConfirmed != null) {
        println("Original confirmed reservation: $originalConfirmed")
        val modificationSession = reservationManager.startModificationSession(originalConfirmed.id)
        if (modificationSession != null) {
            println("Modification session started: $modificationSession")
            // Change hotel dates
            reservationManager.addHotelToCurrentReservation(
                "HyattNYC", "StdRoom", // Same hotel, room
                LocalDate.now().plusDays(35), LocalDate.now().plusDays(37) // New dates
            )
            // Add a transport
            reservationManager.addTransportToCurrentReservation(
                "AMTRAK007", "TRAIN",
                LocalDate.now().plusDays(35)
            )
            println("Reservation after modifications: ${reservationManager.getCurrentReservation()}")
            val confirmedModification = reservationManager.confirmModification()
            if (confirmedModification != null) {
                println("Modification Confirmed: $confirmedModification")
            } else {
                println("Failed to confirm modification.")
            }
        }
    }

    // Scenario 4: Cancel a confirmed reservation
    println("\n--- SCENARIO 4: CANCEL CONFIRMED RESERVATION ---")
    // First, create and confirm one to cancel
    var reservationToCancel = reservationManager.startNewReservationSession(userId)
    reservationManager.addHotelToCurrentReservation(
        "WestinCHI", "Deluxe",
        LocalDate.now().plusDays(60), LocalDate.now().plusDays(62)
    )
    reservationManager.proceedToConfirmation()
    val confirmedToCancel = reservationManager.confirmAndBookCurrentReservation()

    if (confirmedToCancel != null) {
        println("Confirmed reservation to cancel: $confirmedToCancel")
        val cancelSuccess = reservationManager.cancelReservation(confirmedToCancel.id)
        println("Cancellation successful: $cancelSuccess")
        val finalStatus = reservationManager.getReservationDetails(confirmedToCancel.id)
        println("Status after cancellation attempt: ${finalStatus?.status}")
    }
}


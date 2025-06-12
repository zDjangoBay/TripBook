import java.io.File

data class Reservation(val guestName: String, val amount: Double, val status: String = "On_going")

object ReservationManager {
    private val reservations = mutableListOf<Reservation>()
    private val storageFile = File("reservations.txt")

    fun addReservation(guestName: String, amount: Double) {
        val newReservation = Reservation(guestName, amount)
        reservations.add(newReservation)
        saveReservations()
    }

    private fun saveReservations() {
        storageFile.writeText("")
        reservations.forEach { reservation ->
            storageFile.appendText("${reservation.guestName};${reservation.amount};${reservation.status}\n")
        }
    }

    fun showReservations() {
        if (reservations.isEmpty()) {
            println("No reservations found.")
        } else {
            println("Current reservations:")
            reservations.forEach { res ->
                println("${res.guestName} - ${res.amount}€ [${res.status}]")
            }}}}
fun main() {
    ReservationManager.addReservation("loli", 120.00)
    ReservationManager.addReservation("kamta", 600.0)
    ReservationManager.showReservations()
}

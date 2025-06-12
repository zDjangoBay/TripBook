import java.io.File
data class Reservation(val Name: String, val amount: Double, val status: String = "in_progress")
object ReservationManager {
    private val reservations = mutableListOf<Reservation>()
    private val storageFile = File("reservations.txt")
    fun addReservation(Name: String, amount: Double) {
        val newReservation = Reservation(Name, amount)
        reservations.add(newReservation)
        saveReservations() }
        private fun saveReservations() {
        storageFile.writeText("") // Clear the file before writing
        reservations.forEach { reservation ->
            storageFile.appendText("${reservation.guestName};${reservation.amount};${reservation.status}\n") }}
        fun showReservations() {
        if (reservations.isEmpty()) {
            println("No reservations found.")} 
            else {
            println("Current reservations:")
            reservations.forEach { res ->
                println("${res.guestName} - ${res.amount}F [${res.status}]")
            }}}}
           fun main() {
    ReservationManager.addReservation("loli", 12000)
    ReservationManager.addReservation("kamta", 6000)
    ReservationManager.showReservations()
}
            

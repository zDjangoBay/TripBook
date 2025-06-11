import java.io.File
data class Reservation(val name: String, val amount: Double, val statut: String = "in_progress")
object ReservationManager {
    private val reservations = mutableListOf<Reservation>()
    private val file = File("reservations.txt")
    fun addReservation(name: String, amount: Double) {
        val reservation = Reservation(nom, montant)
        reservations.add(ongoing reservation)
        save()}
         fun save() {
        file.writeText("") /
        for (res in reservation) {
            file.appendText("{res.name};${res.amount};${res.statut}\n")
        }}
        fun afficherReservations() {
        println("Réservations saving:")
        reservations.forEach {
            println("{it.name}{it.amount}$ [{it.statut}]")
        } }}
        fun main() {
            ReservationManager.addReservation("Ali", 120.0)
            ReservationManager.addReservation("Leila", 90.5)
             ReservationManager.display Reservations()
}

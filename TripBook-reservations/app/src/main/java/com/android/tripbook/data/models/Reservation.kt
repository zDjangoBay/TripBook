

//Définition de la structure de chaque réservation
data class Reservation(
        val destination: String,
        val departureDate: String,
        val returnDate: String,
        val transportMode: String,
        val numberOfPeople: Int,
        val totalPrice: Double,
        val status: String
)
package com.android.tripbook.reservation.notifications.models

/**
 * Enum définissant les différents types de notifications dans l'application TripBook
 */
enum class NotificationType(val displayName: String, val icon: String) {

    // Notifications de réservation
    BOOKING_CONFIRMATION("Confirmation de réservation", "✅"),
    BOOKING_PENDING("Réservation en attente", "⏳"),
    BOOKING_CANCELLED("Réservation annulée", "❌"),
    BOOKING_MODIFIED("Réservation modifiée", "📝"),
    BOOKING_REMINDER("Rappel de réservation", "🔔"),

    // Notifications de transport
    FLIGHT_CONFIRMATION("Vol confirmé", "✈️"),
    FLIGHT_DELAY("Retard de vol", "⏰"),
    FLIGHT_CANCELLATION("Vol annulé", "🚫"),
    FLIGHT_GATE_CHANGE("Changement de porte", "🚪"),
    FLIGHT_BOARDING("Embarquement", "🎫"),
    CHECK_IN_AVAILABLE("Enregistrement disponible", "📱"),
    CHECK_IN_REMINDER("Rappel d'enregistrement", "⏱️"),
    BOARDING_PASS_READY("Carte d'embarquement prête", "🎟️"),

    // Notifications transport terrestre/maritime
    CAR_RENTAL_CONFIRMATION("Location de voiture confirmée", "🚗"),
    CAR_PICKUP_REMINDER("Rappel de récupération véhicule", "🔑"),
    SHIP_DEPARTURE_REMINDER("Rappel de départ bateau", "⛵"),
    TRANSPORT_DELAY("Retard de transport", "⏱️"),

    // Notifications d'hôtel
    HOTEL_CONFIRMATION("Hôtel confirmé", "🏨"),
    HOTEL_CHECK_IN_AVAILABLE("Enregistrement hôtel disponible", "🛏️"),
    HOTEL_CHECK_OUT_REMINDER("Rappel de checkout", "🧳"),
    HOTEL_ROOM_READY("Chambre prête", "🛎️"),
    HOTEL_BOOKING_MODIFIED("Réservation hôtel modifiée", "🏨"),

    // Notifications d'activités
    ACTIVITY_CONFIRMATION("Activité confirmée", "🎯"),
    ACTIVITY_REMINDER("Rappel d'activité", "📅"),
    ACTIVITY_CANCELLED("Activité annulée", "⚠️"),
    ACTIVITY_RESCHEDULED("Activité reprogrammée", "🔄"),
    TOUR_STARTING_SOON("Tour commence bientôt", "🚶"),

    // Notifications de paiement
    PAYMENT_SUCCESS("Paiement réussi", "💳"),
    PAYMENT_FAILED("Paiement échoué", "❌"),
    PAYMENT_PENDING("Paiement en attente", "⏳"),
    REFUND_PROCESSED("Remboursement traité", "💰"),

    // Notifications système
    SYSTEM_MAINTENANCE("Maintenance système", "🔧"),
    SYSTEM_UPDATE("Mise à jour disponible", "🔄"),

    // Notifications météo et voyage
    WEATHER_ALERT("Alerte météo", "🌦️"),
    TRAVEL_TIPS("Conseils de voyage", "💡"),
    DESTINATION_INFO("Informations destination", "ℹ️"),

    // Notifications d'urgence
    EMERGENCY_CONTACT("Contact d'urgence", "🚨"),
    TRAVEL_ADVISORY("Avis de voyage", "⚠️"),
    SECURITY_ALERT("Alerte sécurité", "🔒"),

    // Notifications promotionnelles
    SPECIAL_OFFER("Offre spéciale", "🎁"),
    LOYALTY_POINTS("Points de fidélité", "⭐"),

    // Notifications génériques
    GENERAL_INFO("Information générale", "ℹ️"),
    CUSTOM("Personnalisé", "📢");

    companion object {
        /**
         * Obtient le type de notification à partir de son nom
         */
        fun fromString(type: String): NotificationType {
            return entries.find { it.name.equals(type, ignoreCase = true) } ?: GENERAL_INFO
        }

        /**
         * Obtient tous les types de notifications critiques
         */
        fun getCriticalTypes(): List<NotificationType> {
            return listOf(
                FLIGHT_CANCELLATION,
                FLIGHT_BOARDING,
                EMERGENCY_CONTACT,
                SECURITY_ALERT,
                TRAVEL_ADVISORY,
                PAYMENT_FAILED
            )
        }

        /**
         * Obtient tous les types de notifications liées aux vols
         */
        fun getFlightTypes(): List<NotificationType> {
            return listOf(
                FLIGHT_CONFIRMATION,
                FLIGHT_DELAY,
                FLIGHT_CANCELLATION,
                FLIGHT_GATE_CHANGE,
                FLIGHT_BOARDING,
                CHECK_IN_AVAILABLE,
                CHECK_IN_REMINDER,
                BOARDING_PASS_READY
            )
        }
    }
}
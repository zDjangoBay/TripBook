package com.android.tripbook.reservation.notifications.models

enum class NotificationAction(
    val displayName: String,
    val buttonText: String,
    val requiresConfirmation: Boolean = false
) {

    VIEW_DETAILS("Voir les détails", "Voir détails"),
    VIEW_BOOKING("Voir la réservation", "Voir réservation"),
    VIEW_BOARDING_PASS("Voir carte d'embarquement", "Carte d'embarquement"),
    VIEW_HOTEL_DETAILS("Voir détails hôtel", "Détails hôtel"),
    VIEW_ACTIVITY_INFO("Voir infos activité", "Infos activité"),
    VIEW_ITINERARY("Voir itinéraire", "Itinéraire"),
    VIEW_RECEIPT("Voir reçu", "Reçu"),


    MODIFY_BOOKING("Modifier la réservation", "Modifier", true),
    CANCEL_BOOKING("Annuler la réservation", "Annuler", true),
    RESUME_BOOKING("Reprendre la réservation", "Reprendre"),
    COMPLETE_BOOKING("Finaliser la réservation", "Finaliser"),

    CHECK_IN_NOW("S'enregistrer maintenant", "S'enregistrer"),
    SELECT_SEAT("Choisir siège", "Choisir siège"),
    ADD_BAGGAGE("Ajouter bagages", "Ajouter bagages"),
    VIEW_FLIGHT_STATUS("Voir état du vol", "État du vol"),
    DOWNLOAD_BOARDING_PASS("Télécharger carte", "Télécharger"),

    HOTEL_CHECK_IN("Enregistrement hôtel", "Check-in"),
    HOTEL_CHECK_OUT("Checkout hôtel", "Check-out"),
    EXTEND_STAY("Prolonger séjour", "Prolonger"),
    ROOM_SERVICE("Service en chambre", "Service"),

    CONFIRM_ACTIVITY("Confirmer activité", "Confirmer"),
    RESCHEDULE_ACTIVITY("Reprogrammer activité", "Reprogrammer"),
    CANCEL_ACTIVITY("Annuler activité", "Annuler", true),
    GET_DIRECTIONS("Obtenir directions", "Directions"),

    RETRY_PAYMENT("Réessayer paiement", "Réessayer"),
    UPDATE_PAYMENT_METHOD("Mettre à jour paiement", "Mettre à jour"),
    VIEW_PAYMENT_DETAILS("Voir détails paiement", "Détails paiement"),
    REQUEST_REFUND("Demander remboursement", "Remboursement", true),

    TRACK_VEHICLE("Suivre véhicule", "Suivre"),
    CONTACT_DRIVER("Contacter chauffeur", "Contacter"),
    MODIFY_PICKUP("Modifier récupération", "Modifier"),

    CALL_SUPPORT("Appeler support", "Appeler"),
    CHAT_SUPPORT("Chat support", "Chat"),
    SEND_FEEDBACK("Envoyer feedback", "Feedback"),
    CONTACT_HOTEL("Contacter hôtel", "Contacter"),
    CONTACT_AIRLINE("Contacter compagnie", "Contacter"),

    EMERGENCY_CONTACT("Contact d'urgence", "Urgence"),
    REPORT_ISSUE("Signaler problème", "Signaler"),
    FIND_ALTERNATIVE("Trouver alternative", "Alternative"),

    UPDATE_APP("Mettre à jour app", "Mettre à jour"),
    ENABLE_NOTIFICATIONS("Activer notifications", "Activer"),
    SHARE_TRIP("Partager voyage", "Partager"),
    RATE_EXPERIENCE("Noter expérience", "Noter"),

    OPEN_MAP("Ouvrir carte", "Carte"),
    NAVIGATE_TO_LOCATION("Naviguer vers", "Naviguer"),
    FIND_NEARBY("Trouver à proximité", "À proximité"),

    SAVE_TO_CALENDAR("Sauver au calendrier", "Calendrier"),
    SAVE_TO_WALLET("Sauver au portefeuille", "Portefeuille"),
    DOWNLOAD_DOCUMENT("Télécharger document", "Télécharger"),

    VIEW_OFFER("Voir offre", "Voir offre"),
    CLAIM_DISCOUNT("Utiliser réduction", "Utiliser"),
    EARN_POINTS("Gagner points", "Gagner"),

    DISMISS("Ignorer", "Ignorer"),
    REMIND_LATER("Rappeler plus tard", "Plus tard"),
    MARK_AS_DONE("Marquer comme fait", "Fait"),
    OPEN_APP("Ouvrir app", "Ouvrir"),

    SNOOZE_NOTIFICATION("Reporter notification", "Reporter"),
    DISABLE_SIMILAR("Désactiver similaires", "Désactiver"),

    CUSTOM_ACTION("Action personnalisée", "Action");

    companion object {

        fun fromString(action: String): NotificationAction {
            return entries.find { it.name.equals(action, ignoreCase = true) } ?: CUSTOM_ACTION
        }


        fun getCriticalActions(): List<NotificationAction> {
            return entries.filter { it.requiresConfirmation }
        }


        fun getFlightActions(): List<NotificationAction> {
            return listOf(
                CHECK_IN_NOW,
                SELECT_SEAT,
                ADD_BAGGAGE,
                VIEW_FLIGHT_STATUS,
                VIEW_BOARDING_PASS,
                DOWNLOAD_BOARDING_PASS,
                CONTACT_AIRLINE
            )
        }


        fun getBookingActions(): List<NotificationAction> {
            return listOf(
                VIEW_BOOKING,
                MODIFY_BOOKING,
                CANCEL_BOOKING,
                RESUME_BOOKING,
                COMPLETE_BOOKING,
                VIEW_RECEIPT
            )
        }

        fun getHotelActions(): List<NotificationAction> {
            return listOf(
                HOTEL_CHECK_IN,
                HOTEL_CHECK_OUT,
                EXTEND_STAY,
                ROOM_SERVICE,
                VIEW_HOTEL_DETAILS,
                CONTACT_HOTEL
            )
        }

        fun getEmergencyActions(): List<NotificationAction> {
            return listOf(
                EMERGENCY_CONTACT,
                REPORT_ISSUE,
                FIND_ALTERNATIVE,
                CALL_SUPPORT
            )
        }
    }
}
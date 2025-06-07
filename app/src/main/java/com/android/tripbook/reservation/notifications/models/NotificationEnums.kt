package com.android.tripbook.reservation.notifications.models

/**
 * Types de notifications pour les réservations de VOLS
 * Version complète pour app de réservation aérienne
 * Responsable : Tchinda Martin Kevin
 */
enum class NotificationType {
    // Réservation de base
    BOOKING_CONFIRMATION,    // Confirmation de réservation
    BOOKING_REMINDER,       // Rappel de voyage
    BOOKING_CANCELLED,      // Annulation de réservation
    BOOKING_MODIFIED,       // Modification de réservation

    // Paiements
    PAYMENT_SUCCESS,        // Paiement réussi
    PAYMENT_FAILED,         // Échec de paiement
    REFUND_PROCESSED,       // Remboursement traité

    // Spécifique aux vols - ESSENTIELS
    FLIGHT_DELAYED,         // Vol retardé
    FLIGHT_CANCELLED,       // Vol annulé
    FLIGHT_RESCHEDULED,     // Vol reprogrammé
    GATE_CHANGE,           // Changement de porte
    TERMINAL_CHANGE,       // Changement de terminal
    BOARDING_STARTED,      // Embarquement commencé
    BOARDING_CLOSING,      // Fermeture embarquement

    // Check-in et sièges
    CHECK_IN_OPENED,       // Check-in ouvert (24h avant)
    CHECK_IN_REMINDER,     // Rappel check-in
    SEAT_ASSIGNMENT,       // Attribution de siège
    SEAT_UPGRADE_OFFER,    // Offre de surclassement

    // Documents et voyage
    DOCUMENT_REMINDER,     // Rappel documents (passeport, visa)
    BAGGAGE_INFO,         // Informations bagages
    WEATHER_ALERT,        // Alerte météo destination

    // Services additionnels
    MEAL_SELECTION,       // Sélection de repas
    LOYALTY_POINTS,       // Points fidélité gagnés
    PROMOTION_OFFER,      // Offres promotionnelles

    // Support
    CUSTOMER_SERVICE,     // Message du service client
    GENERAL              // Notification générale
}

/**
 * Niveaux de priorité adaptés aux vols
 */
enum class NotificationPriority {
    CRITICAL,   // Vol annulé, embarquement fermé - Action immédiate
    HIGH,       // Retard, changement de porte - Action requise
    MEDIUM,     // Check-in, rappels - Important mais pas urgent
    LOW,        // Promotions, infos générales
    INFO        // Informations pures (météo, points fidélité)
}

/**
 * Actions possibles depuis les notifications
 */
enum class NotificationAction {
    VIEW_BOOKING,        // Voir la réservation
    VIEW_BOARDING_PASS,  // Voir carte d'embarquement
    CHECK_IN_NOW,        // S'enregistrer maintenant
    SELECT_SEAT,         // Choisir un siège
    VIEW_FLIGHT_STATUS,  // Voir statut du vol
    CONTACT_SUPPORT,     // Contacter le support
    RETRY_PAYMENT,       // Réessayer le paiement
    VIEW_REFUND,         // Voir le remboursement
    UPDATE_DOCUMENTS,    // Mettre à jour documents
    BOOK_MEAL,          // Réserver un repas
    UPGRADE_SEAT,       // Surclasser le siège
    VIEW_WEATHER,       // Voir météo destination
    NONE                // Pas d'action
}

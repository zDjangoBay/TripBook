
    package com.android.tripbook.reservation.notifications.utils

    import com.android.tripbook.reservation.notifications.models.*
    import java.text.SimpleDateFormat
    import java.util.*
    import kotlin.random.Random

    /**
    * Utilitaire de formatage des notifications pour vols
    * Sépare la logique de formatage du NotificationManager
    * Responsable : Tchinda Martin Kevin
    */
    object NotificationFormatter {

    /**
    * Formate une notification selon son type - VERSION VOLS
    */
    fun formatNotification(serviceNotification: ServiceNotification): InAppNotification {
    return when (serviceNotification.type) {
    NotificationType.BOOKING_CONFIRMATION -> {
    InAppNotification(
    id = generateNotificationId(),
    title = "✈️ Vol confirmé",
    message = "Votre vol ${serviceNotification.flightNumber} ${serviceNotification.departure} → ${serviceNotification.destination} est confirmé",
    type = NotificationType.BOOKING_CONFIRMATION,
    priority = NotificationPriority.HIGH,
    timestamp = System.currentTimeMillis(),
    actionButton = "Voir billet",
    actionType = NotificationAction.VIEW_BOOKING,
    deepLink = "tripbook://booking/${serviceNotification.bookingReference}",
    data = serviceNotification.data
    )
    }

    NotificationType.FLIGHT_DELAYED -> {
    val delayMinutes = serviceNotification.data?.get("delayMinutes") as? Int ?: 0
    InAppNotification(
    id = generateNotificationId(),
    title = "⏰ Vol retardé",
    message = "Votre vol ${serviceNotification.flightNumber} est retardé de ${delayMinutes} minutes. Nouveau départ : ${formatTime(serviceNotification.newDepartureTime)}",
    type = NotificationType.FLIGHT_DELAYED,
    priority = NotificationPriority.HIGH,
    timestamp = System.currentTimeMillis(),
    actionButton = "Voir statut",
    actionType = NotificationAction.VIEW_FLIGHT_STATUS,
    deepLink = "tripbook://flight-status/${serviceNotification.flightNumber}",
    data = serviceNotification.data
    )
    }

    NotificationType.FLIGHT_CANCELLED -> {
    InAppNotification(
    id = generateNotificationId(),
    title = "❌ Vol annulé",
    message = "Votre vol ${serviceNotification.flightNumber} a été annulé. Nous vous proposons des alternatives.",
    type = NotificationType.FLIGHT_CANCELLED,
    priority = NotificationPriority.CRITICAL,
    timestamp = System.currentTimeMillis(),
    actionButton = "Voir options",
    actionType = NotificationAction.CONTACT_SUPPORT,
    deepLink = "tripbook://rebooking/${serviceNotification.bookingReference}",
    isUrgent = true,
    data = serviceNotification.data
    )
    }

    NotificationType.GATE_CHANGE -> {
    val oldGate = serviceNotification.data?.get("oldGate") as? String
    val newGate = serviceNotification.data?.get("newGate") as? String
    InAppNotification(
    id = generateNotificationId(),
    title = "🚪 Changement de porte",
    message = "Vol ${serviceNotification.flightNumber} : Nouvelle porte ${newGate} (ancienne: ${oldGate})",
    type = NotificationType.GATE_CHANGE,
    priority = NotificationPriority.HIGH,
    timestamp = System.currentTimeMillis(),
    actionButton = "Voir plan",
    actionType = NotificationAction.VIEW_BOARDING_PASS,
    isUrgent = true,
    data = serviceNotification.data
    )
    }

    NotificationType.CHECK_IN_OPENED -> {
    InAppNotification(
    id = generateNotificationId(),
    title = "📋 Enregistrement ouvert",
    message = "Vous pouvez maintenant vous enregistrer pour votre vol ${serviceNotification.flightNumber}",
    type = NotificationType.CHECK_IN_OPENED,
    priority = NotificationPriority.MEDIUM,
    timestamp = System.currentTimeMillis(),
    actionButton = "S'enregistrer",
    actionType = NotificationAction.CHECK_IN_NOW,
    deepLink = "tripbook://check-in/${serviceNotification.bookingReference}",
    data = serviceNotification.data
    )
    }

    NotificationType.CHECK_IN_REMINDER -> {
    val timeRemaining = serviceNotification.data?.get("hoursRemaining") as? Int ?: 24
    InAppNotification(
    id = generateNotificationId(),
    title = "⏱️ Rappel enregistrement",
    message = "Plus que ${timeRemaining}h pour vous enregistrer sur votre vol ${serviceNotification.flightNumber}",
    type = NotificationType.CHECK_IN_REMINDER,
    priority = NotificationPriority.MEDIUM,
    timestamp = System.currentTimeMillis(),
    actionButton = "S'enregistrer",
    actionType = NotificationAction.CHECK_IN_NOW,
    deepLink = "tripbook://check-in/${serviceNotification.bookingReference}",
    data = serviceNotification.data
    )
    }

    NotificationType.BOARDING_STARTED -> {
    InAppNotification(
    id = generateNotificationId(),
    title = "🎫 Embarquement commencé",
    message = "L'embarquement de votre vol ${serviceNotification.flightNumber} a commencé. Porte ${serviceNotification.gate}",
    type = NotificationType.BOARDING_STARTED,
    priority = NotificationPriority.CRITICAL,
    timestamp = System.currentTimeMillis(),
    actionButton = "Carte d'embarquement",
    actionType = NotificationAction.VIEW_BOARDING_PASS,
    isUrgent = true,
    data = serviceNotification.data
    )
    }

    NotificationType.BOARDING_CLOSING -> {
    InAppNotification(
    id = generateNotificationId(),
    title = "🚨 Embarquement se ferme",
    message = "Derniers appels pour l'embarquement du vol ${serviceNotification.flightNumber}. Présentez-vous immédiatement porte ${serviceNotification.gate}",
    type = NotificationType.BOARDING_CLOSING,
    priority = NotificationPriority.CRITICAL,
    timestamp = System.currentTimeMillis(),
    actionButton = "Carte d'embarquement",
    actionType = NotificationAction.VIEW_BOARDING_PASS,
    isUrgent = true,
    data = serviceNotification.data
    )
    }

    NotificationType.SEAT_ASSIGNMENT -> {
    InAppNotification(
    id = generateNotificationId(),
    title = "💺 Siège attribué",
    message = "Votre siège ${serviceNotification.seat} a été confirmé pour le vol ${serviceNotification.flightNumber}",
    type = NotificationType.SEAT_ASSIGNMENT,
    priority = NotificationPriority.MEDIUM,
    timestamp = System.currentTimeMillis(),
    actionButton = "Voir carte d'embarquement",
    actionType = NotificationAction.VIEW_BOARDING_PASS,
    data = serviceNotification.data
    )
    }

    NotificationType.SEAT_UPGRADE_OFFER -> {
    val upgradePrice = serviceNotification.data?.get("upgradePrice") as? Double
    val expirationTime = System.currentTimeMillis() + (2 * 60 * 60 * 1000) // 2h d'expiration
    InAppNotification(
    id = generateNotificationId(),
    title = "⬆️ Offre de surclassement",
    message = "Surclassement disponible pour ${upgradePrice}€ sur votre vol ${serviceNotification.flightNumber}",
    type = NotificationType.SEAT_UPGRADE_OFFER,
    priority = NotificationPriority.LOW,
    timestamp = System.currentTimeMillis(),
    actionButton = "Voir offre",
    actionType = NotificationAction.UPGRADE_SEAT,
    expiresAt = expirationTime,
    data = serviceNotification.data
    )
    }

    NotificationType.PAYMENT_SUCCESS -> {
    InAppNotification(
    id = generateNotificationId(),
    title = "💳 Paiement réussi",
    message = "Votre paiement de ${serviceNotification.amount}€ a été traité avec succès.",
    type = NotificationType.PAYMENT_SUCCESS,
    priority = NotificationPriority.MEDIUM,
    timestamp = System.currentTimeMillis(),
    actionButton = "Voir reçu",
    actionType = NotificationAction.VIEW_BOOKING,
    data = serviceNotification.data
    )
    }

    NotificationType.PAYMENT_FAILED -> {
    InAppNotification(
    id = generateNotificationId(),
    title = "❌ Paiement échoué",
    message = "Votre paiement de ${serviceNotification.amount}€ n'a pas pu être traité.",
    type = NotificationType.PAYMENT_FAILED,
    priority = NotificationPriority.HIGH,
    timestamp = System.currentTimeMillis(),
    actionButton = "Réessayer",
    actionType = NotificationAction.RETRY_PAYMENT,
    data = serviceNotification.data
    )
    }

    NotificationType.DOCUMENT_REMINDER -> {
    InAppNotification(
    id = generateNotificationId(),
    title = "📄 Vérifiez vos documents",
    message = "Assurez-vous d'avoir tous les documents requis pour votre vol vers ${serviceNotification.destination}",
    type = NotificationType.DOCUMENT_REMINDER,
    priority = NotificationPriority.MEDIUM,
    timestamp = System.currentTimeMillis(),
    actionButton = "Voir exigences",
    actionType = NotificationAction.UPDATE_DOCUMENTS,
    data = serviceNotification.data
    )
    }

    NotificationType.WEATHER_ALERT -> {
    val weatherCondition = serviceNotification.data?.get("condition") as? String
    InAppNotification(
    id = generateNotificationId(),
    title = "🌤️ Alerte météo",
    message = "Conditions météo à ${serviceNotification.destination} : ${weatherCondition}",
    type = NotificationType.WEATHER_ALERT,
    priority = NotificationPriority.INFO,
    timestamp = System.currentTimeMillis(),
    actionButton = "Voir météo",
    actionType = NotificationAction.VIEW_WEATHER,
    data = serviceNotification.data
    )
    }

    else -> {
    InAppNotification(
    id = generateNotificationId(),
    title = serviceNotification.title ?: "Notification",
    message = serviceNotification.message ?: "",
    type = NotificationType.GENERAL,
    priority = NotificationPriority.LOW,
    timestamp = System.currentTimeMillis(),
    data = serviceNotification.data
    )
    }
    }
    }

    // Méthodes utilitaires
    private fun generateNotificationId(): String {
    return "notif_${System.currentTimeMillis()}_${Random.nextInt(1000)}"
    }

    private fun formatTime(timestamp: Long?): String {
    return if (timestamp != null) {
    SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date(timestamp))
    } else {
    "Heure non disponible"
    }
    }

    fun formatDate(timestamp: Long?): String {
    return if (timestamp != null) {
    SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE).format(Date(timestamp))
    } else {
    "Date non disponible"
    }
    }
    }
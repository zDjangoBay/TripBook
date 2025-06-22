// InAppNotification.kt - VERSION VOLS
package com.tripbook.reservation.notifications.models
import com.android.tripbook.reservation.notifications.models.NotificationType
import com.android.tripbook.reservation.notifications.models.NotificationAction
import com.android.tripbook.reservation.notifications.models.NotificationPriority

/**
 * Modèle de données pour les notifications de vols
 * Version enrichie pour app de réservation aérienne
 */
data class InAppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val priority: NotificationPriority,
    val timestamp: Long,
    val isRead: Boolean = false,

    // Nouveaux champs pour les vols
    val actionButton: String? = null,
    val actionType: NotificationAction? = null,
    val deepLink: String? = null,           // Navigation dans l'app
    val expiresAt: Long? = null,            // Expiration (ex: offres limitées)
    val isUrgent: Boolean = false,          // Notification critique
    val soundEnabled: Boolean = true,        // Son activé/désactivé

    val data: Map<String, Any>? = null
)


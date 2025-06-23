// ServiceNotification.kt - VERSION CORRIGÉE
package com.android.tripbook.reservation.notifications.models

import java.util.UUID


data class ServiceNotification(
    val type: NotificationType,
    val title: String?,
    val message: String?,
    val bookingReference: String?,
    val travelDate: Long?,
    val amount: Double?,

    // NOUVELLES PROPRIÉTÉS POUR LES VOLS
    val flightNumber: String? = null,
    val departure: String? = null,
    val destination: String? = null,
    val gate: String? = null,
    val seat: String? = null,
    val newDepartureTime: Long? = null,

    val data: Map<String, Any>? = null




)
data class NotificationService(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val type: String,
    val priority: String,
    val timestamp: Long = System.currentTimeMillis(),
    val data: Map<String, Any>? = null,
    val actionRequired: Boolean = false,
    val expiresAt: Long? = null
)
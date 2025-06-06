package com.android.tripbook.ui.notifications.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.notifications.models.*
import com.android.tripbook.notifications.services.NotificationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class NotificationUiState(
    val notifications: List<NotificationDisplayItem> = emptyList(),
    val isLoading: Boolean = false,
    val filter: NotificationFilter = NotificationFilter.ALL,
    val unreadCount: Int = 0
)

data class NotificationDisplayItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val iconColor: String,
    val timeAgo: String
)

enum class NotificationFilter {
    ALL, UNREAD, BOOKINGS, PAYMENTS, REMINDERS
}

class NotificationViewModel(
    private val notificationService: NotificationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    // Simulation des données - À remplacer par vraies données
    private val mockNotifications = listOf(
        createMockNotification("1", NotificationType.BOOKING_CONFIRMED,
            "Réservation confirmée !", "Votre voyage vers Paris est confirmé",
            System.currentTimeMillis() - 300000, false),
        createMockNotification("2", NotificationType.PAYMENT_SUCCESS,
            "Paiement réussi", "Votre paiement de 350€ a été traité avec succès",
            System.currentTimeMillis() - 1800000, false),
        createMockNotification("3", NotificationType.TRIP_REMINDER,
            "Rappel de voyage", "N'oubliez pas votre voyage vers Londres demain !",
            System.currentTimeMillis() - 3600000, true),
        createMockNotification("4", NotificationType.BOOKING_MODIFIED,
            "Réservation modifiée", "Votre réservation a été mise à jour",
            System.currentTimeMillis() - 7200000, true),
        createMockNotification("5", NotificationType.REFUND_PROCESSED,
            "Remboursement traité", "Votre remboursement de 150€ est en cours",
            System.currentTimeMillis() - 86400000, false)
    )

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Simulation - remplace par l'appel à ton service
            val notifications = mockNotifications
            val unreadCount = notifications.count { !it.isRead }

            _uiState.value = _uiState.value.copy(
                notifications = notifications,
                isLoading = false,
                unreadCount = unreadCount
            )
        }
    }

    fun markAsRead(notificationId: String) {
        val currentNotifications = _uiState.value.notifications.toMutableList()
        val index = currentNotifications.indexOfFirst { it.id == notificationId }

        if (index != -1 && !currentNotifications[index].isRead) {
            currentNotifications[index] = currentNotifications[index].copy(isRead = true)
            val unreadCount = currentNotifications.count { !it.isRead }

            _uiState.value = _uiState.value.copy(
                notifications = currentNotifications,
                unreadCount = unreadCount
            )
        }
    }

    fun markAllAsRead() {
        val updatedNotifications = _uiState.value.notifications.map {
            it.copy(isRead = true)
        }

        _uiState.value = _uiState.value.copy(
            notifications = updatedNotifications,
            unreadCount = 0
        )
    }

    fun setFilter(filter: NotificationFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }

    fun getFilteredNotifications(): List<NotificationDisplayItem> {
        val notifications = _uiState.value.notifications
        return when (_uiState.value.filter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
            NotificationFilter.BOOKINGS -> notifications.filter {
                it.type in listOf(NotificationType.BOOKING_CONFIRMED,
                    NotificationType.BOOKING_MODIFIED, NotificationType.BOOKING_CANCELLED)
            }
            NotificationFilter.PAYMENTS -> notifications.filter {
                it.type in listOf(NotificationType.PAYMENT_SUCCESS, NotificationType.REFUND_PROCESSED)
            }
            NotificationFilter.REMINDERS -> notifications.filter {
                it.type == NotificationType.TRIP_REMINDER
            }
        }
    }

    private fun createMockNotification(
        id: String, type: NotificationType, title: String,
        message: String, timestamp: Long, isRead: Boolean
    ): NotificationDisplayItem {
        return NotificationDisplayItem(
            id = id,
            type = type,
            title = title,
            message = message,
            timestamp = timestamp,
            isRead = isRead,
            iconColor = getIconColor(type),
            timeAgo = formatTimeAgo(timestamp)
        )
    }

    private fun getIconColor(type: NotificationType): String {
        return when (type) {
            NotificationType.BOOKING_CONFIRMED -> "#22C55E"
            NotificationType.PAYMENT_SUCCESS -> "#3B82F6"
            NotificationType.TRIP_REMINDER -> "#F59E0B"
            NotificationType.BOOKING_MODIFIED -> "#EAB308"
            NotificationType.BOOKING_CANCELLED -> "#EF4444"
            NotificationType.REFUND_PROCESSED -> "#8B5CF6"
        }
    }

    private fun formatTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val minutes = diff / (1000 * 60)
        val hours = diff / (1000 * 60 * 60)
        val days = diff / (1000 * 60 * 60 * 24)

        return when {
            minutes < 1 -> "À l'instant"
            minutes < 60 -> "Il y a ${minutes}min"
            hours < 24 -> "Il y a ${hours}h"
            days < 7 -> "Il y a ${days}j"
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
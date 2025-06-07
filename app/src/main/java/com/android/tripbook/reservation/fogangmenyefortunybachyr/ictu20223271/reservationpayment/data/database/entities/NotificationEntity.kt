package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Room Entity for Notifications
 *
 * This entity stores all notifications sent to users including
 * payment confirmations, trip reminders, booking updates, and
 * general announcements.
 *
 * Key Features:
 * - User-specific notifications
 * - Multiple notification types
 * - Read/unread status tracking
 * - Priority levels
 * - Action URLs for deep linking
 *
 * Used by:
 * - NotificationScreen for displaying user notifications
 * - FakeNotificationDispatcher for creating notifications
 * - Push notification system
 */
@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ReservationEntity::class,
            parentColumns = ["id"],
            childColumns = ["reservation_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        androidx.room.Index(value = ["user_id"]),
        androidx.room.Index(value = ["type"]),
        androidx.room.Index(value = ["is_read"]),
        androidx.room.Index(value = ["priority"]),
        androidx.room.Index(value = ["created_at"]),
        androidx.room.Index(value = ["reservation_id"])
    ]
)
data class NotificationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "type")
    val type: String, // NotificationType enum stored as String

    @ColumnInfo(name = "priority")
    val priority: String = "NORMAL", // HIGH, NORMAL, LOW

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "reservation_id")
    val reservationId: String? = null,

    @ColumnInfo(name = "action_url")
    val actionUrl: String? = null, // Deep link URL

    @ColumnInfo(name = "action_text")
    val actionText: String? = null, // Button text for action

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "expires_at")
    val expiresAt: LocalDateTime? = null,

    @ColumnInfo(name = "sent_at")
    val sentAt: LocalDateTime? = null,

    @ColumnInfo(name = "read_at")
    val readAt: LocalDateTime? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Checks if this notification has expired
     */
    fun isExpired(): Boolean {
        return expiresAt?.isBefore(LocalDateTime.now()) == true
    }

    /**
     * Checks if this notification has an action
     */
    fun hasAction(): Boolean {
        return actionUrl != null && actionText != null
    }

    /**
     * Gets the priority level as enum
     */
    fun getPriorityLevelEnum(): NotificationPriority {
        return try {
            NotificationPriority.valueOf(priority)
        } catch (e: IllegalArgumentException) {
            NotificationPriority.NORMAL
        }
    }

    /**
     * Checks if this notification is related to a reservation
     */
    fun isReservationRelated(): Boolean {
        return reservationId != null
    }

    /**
     * Gets the notification type as enum
     */
    fun getNotificationTypeEnum(): NotificationType {
        return try {
            NotificationType.valueOf(type)
        } catch (e: IllegalArgumentException) {
            NotificationType.GENERAL
        }
    }

    companion object {
        /**
         * Creates a new notification
         */
        fun create(
            userId: String,
            title: String,
            message: String,
            type: NotificationType,
            priority: NotificationPriority = NotificationPriority.NORMAL,
            reservationId: String? = null,
            actionUrl: String? = null,
            actionText: String? = null
        ): NotificationEntity {
            val id = "notif_${System.currentTimeMillis()}_${(1000..9999).random()}"
            return NotificationEntity(
                id = id,
                userId = userId,
                title = title,
                message = message,
                type = type.name,
                priority = priority.name,
                reservationId = reservationId,
                actionUrl = actionUrl,
                actionText = actionText,
                sentAt = LocalDateTime.now()
            )
        }
    }
}

/**
 * Notification priority levels
 */
enum class NotificationPriority {
    HIGH,
    NORMAL,
    LOW
}

/**
 * Notification types matching the existing FakeNotificationDispatcher
 */
enum class NotificationType {
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    TRIP_REMINDER,
    BOOKING_CONFIRMED,
    BOOKING_CANCELLED,
    GENERAL
}

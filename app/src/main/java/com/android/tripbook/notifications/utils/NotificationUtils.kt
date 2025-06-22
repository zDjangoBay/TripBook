package com.android.tripbook.notifications.utils

import com.android.tripbook.notifications.models.NotificationRequest
import com.android.tripbook.notifications.models.NotificationType

object NotificationUtils {


    fun createBookingConfirmation(
        userId: String,
        destination: String,
        date: String,
        transport: String = "Transport"
    ): NotificationRequest {
        return NotificationRequest(
            userId = userId,
            type = NotificationType.BOOKING_CONFIRMED,
            data = mapOf(
                "destination" to destination,
                "date" to date,
                "transport" to transport
            )
        )
    }


    fun createPaymentSuccess(
        userId: String,
        amount: String,
        paymentMethod: String = "Carte bancaire"
    ): NotificationRequest {
        return NotificationRequest(
            userId = userId,
            type = NotificationType.PAYMENT_SUCCESS,
            data = mapOf(
                "amount" to amount,
                "paymentMethod" to paymentMethod
            )
        )
    }

    fun createTripReminder(
        userId: String,
        destination: String,
        time: String
    ): NotificationRequest {
        return NotificationRequest(
            userId = userId,
            type = NotificationType.TRIP_REMINDER,
            data = mapOf(
                "destination" to destination,
                "time" to time
            )
        )
    }


    fun createBookingModification(
        userId: String,
        changes: String
    ): NotificationRequest {
        return NotificationRequest(
            userId = userId,
            type = NotificationType.BOOKING_MODIFIED,
            data = mapOf(
                "changes" to changes
            )
        )
    }


    fun createBookingCancellation(
        userId: String,
        reason: String = "Demande utilisateur"
    ): NotificationRequest {
        return NotificationRequest(
            userId = userId,
            type = NotificationType.BOOKING_CANCELLED,
            data = mapOf(
                "reason" to reason
            )
        )
    }


    fun createRefundNotification(
        userId: String,
        amount: String
    ): NotificationRequest {
        return NotificationRequest(
            userId = userId,
            type = NotificationType.REFUND_PROCESSED,
            data = mapOf(
                "amount" to amount
            )
        )
    }
}
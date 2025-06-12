package com.android.tripbook

import com.android.tripbook.data.models.NotificationType
import com.android.tripbook.data.services.NotificationDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationDispatcherTest {

    @Test
    fun testSendNotification() = runBlocking {
        val dispatcher = NotificationDispatcher()

        dispatcher.sendNotification(NotificationType.PAYMENT_SUCCESS, "Payment successful!")
        dispatcher.sendNotification(NotificationType.BOOKING_CONFIRMED, "Booking confirmed!")

        val notifications = dispatcher.notifications.first()

        assertEquals(2, notifications.size)
        assertEquals(NotificationType.PAYMENT_SUCCESS, notifications[0].type)
        assertEquals("Payment successful!", notifications[0].message)
        assertEquals(NotificationType.BOOKING_CONFIRMED, notifications[1].type)
        assertEquals("Booking confirmed!", notifications[1].message)
    }

    @Test
    fun testMarkAsRead() = runBlocking {
        val dispatcher = NotificationDispatcher()

        dispatcher.sendNotification(NotificationType.GENERAL, "Welcome to TripBook!")
        val notificationId = dispatcher.notifications.first()[0].id

        dispatcher.markAsRead(notificationId)
        val updatedNotifications = dispatcher.notifications.first()

        assertEquals(true, updatedNotifications[0].isRead)
    }
}

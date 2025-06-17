package com.android.tripbook.companycatalog.data


object MockNotifications {
    val notifications = listOf(
        NotificationItem(
            id = "notif_001",
            title = "New Company Added!",
            message = "Check out Desert Trek Tours in Maroua now available.",
            timestamp = System.currentTimeMillis() - 3600000,
        ),
        NotificationItem(
            id = "notif_002",
            title = "Limited Time Offer",
            message = "Safari Adventures is offering 10% off for early bookings.",
            timestamp = System.currentTimeMillis() - 7200000,
        ),
        NotificationItem(
            id = "notif_003",
            title = "Feature Update",
            message = "You can now save companies to your favorites!",
            timestamp = System.currentTimeMillis() - 10950000,
        ),
        NotificationItem(
            id = "notif_004",
            title = "New Companies Added!",
            message = "Check out the catalog to see new conpany options !",
            timestamp = System.currentTimeMillis() - 10800000,
        ),
        NotificationItem(
            id = "notif_005",
            title = "Update your account.",
            message = "Kindly make sure all you informations are updated and secured !",
            timestamp = System.currentTimeMillis() - 10800000,
        )
    )
}

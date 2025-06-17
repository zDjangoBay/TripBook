package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NotificationCategory(val displayName: String) {
    BookingConfirmations("Booking Confirmations"),
    Reminders("Reminders"),
    Promotions("Promotions"),
    ScheduleUpdates("Schedule Updates")
}

data class NotificationItem(
    val id: Int,
    val category: NotificationCategory,
    val title: String,
    val message: String,
    val timestamp: String
)

@Composable
fun NotificationScreen() {
    val allNotifications = remember {
        listOf(
            NotificationItem(1, NotificationCategory.BookingConfirmations, "Trip Confirmed", "Your booking to Limbe is confirmed.", "Today, 9:00 AM"),
            NotificationItem(2, NotificationCategory.Promotions, "Promo Alert", "20% off all trips to Kribi this weekend!", "Yesterday"),
            NotificationItem(3, NotificationCategory.Reminders, "Reminder", "Your trip to Yaoundé departs in 2 hours.", "2h ago"),
            NotificationItem(4, NotificationCategory.ScheduleUpdates, "Schedule Update", "Trip to Buea delayed by 30 mins.", "Today, 7:30 AM"),
            NotificationItem(5, NotificationCategory.Promotions, "Promo Alert", "Buy 1 Get 1 Free on boat trips!", "Today, 10:00 AM"),
            NotificationItem(6, NotificationCategory.BookingConfirmations, "Trip Confirmed", "Your booking to Douala is confirmed.", "Yesterday, 4:00 PM"),
            NotificationItem(7, NotificationCategory.Reminders, "Reminder", "Don't forget your trip tomorrow at 8:00 AM.", "Today"),
            NotificationItem(8, NotificationCategory.ScheduleUpdates, "Schedule Update", "Train to Yaoundé now boarding.", "Today, 11:00 AM"),
        )
    }

    var selectedTab by remember { mutableStateOf(NotificationCategory.BookingConfirmations) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        // Tabs row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            NotificationCategory.values().forEachIndexed { index, category ->
                Tab(
                    selected = selectedTab == category,
                    onClick = { selectedTab = category },
                    text = { Text(category.displayName, fontSize = 12.sp) }
                )
            }
        }

        // Filter notifications based on selected tab/category
        val filteredNotifications = allNotifications.filter { it.category == selectedTab }

        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    "No notifications",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(filteredNotifications) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEFEFEF)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = notif.title, fontSize = 18.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = notif.message, fontSize = 14.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = notif.timestamp, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

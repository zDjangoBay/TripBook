// Final version with all requested features added
package com.android.tripbook.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Enums and data class
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
    val timestamp: String,
    val date: LocalDate,
    var read: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    val allNotifications = remember {
        mutableStateListOf(
                // Booking Confirmations
                NotificationItem(1, NotificationCategory.BookingConfirmations, "Trip Confirmed", "Your booking to Limbe is confirmed.", "Today, 9:00 AM", LocalDate.now()),
                NotificationItem(2, NotificationCategory.BookingConfirmations, "Booking Successful", "You’ve booked a trip to Kribi.", "Today, 1:15 PM", LocalDate.now()),
                NotificationItem(3, NotificationCategory.BookingConfirmations, "Reservation Complete", "You reserved 2 seats to Bamenda.", "Yesterday, 4:00 PM", LocalDate.now().minusDays(1)),
                NotificationItem(4, NotificationCategory.BookingConfirmations, "Trip Approved", "Your express trip to Douala is approved.", "Today, 11:00 AM", LocalDate.now()),
                NotificationItem(5, NotificationCategory.BookingConfirmations, "New Ticket Issued", "A new e-ticket for Buea has been issued.", "2 days ago", LocalDate.now().minusDays(2)),

                // Reminders
                NotificationItem(6, NotificationCategory.Reminders, "Upcoming Trip", "Don't forget your trip to Buea tomorrow.", "Today, 5:00 PM", LocalDate.now()),
                NotificationItem(7, NotificationCategory.Reminders, "Luggage Tip", "Pack light for your journey to Yaoundé.", "Yesterday, 8:00 AM", LocalDate.now().minusDays(1)),
                NotificationItem(8, NotificationCategory.Reminders, "Final Check", "Trip to Limbe leaves in 30 minutes.", "Today, 6:00 AM", LocalDate.now()),
                NotificationItem(9, NotificationCategory.Reminders, "Boarding Reminder", "Arrive early for your Bamenda trip.", "2 days ago", LocalDate.now().minusDays(2)),
                NotificationItem(10, NotificationCategory.Reminders, "ID Check", "Please carry your ID for Kribi trip.", "Today, 7:30 AM", LocalDate.now()),

                // Promotions
                NotificationItem(11, NotificationCategory.Promotions, "Promo Alert", "20% off all trips to Kribi this weekend!", "Today, 10:00 AM", LocalDate.now()),
                NotificationItem(12, NotificationCategory.Promotions, "Flash Sale", "Buy 1 Get 1 Free on boat trips!", "Yesterday, 2:00 PM", LocalDate.now().minusDays(1)),
                NotificationItem(13, NotificationCategory.Promotions, "Limited Offer", "Save up to 30% on Douala express trips.", "2 days ago", LocalDate.now().minusDays(2)),
                NotificationItem(14, NotificationCategory.Promotions, "Holiday Discount", "Travel during holidays at half price!", "Today, 8:00 AM", LocalDate.now()),
                NotificationItem(15, NotificationCategory.Promotions, "Bonus Points", "Earn double points on all bookings today.", "Yesterday, 6:00 PM", LocalDate.now().minusDays(1)),

                // Schedule Updates
                NotificationItem(16, NotificationCategory.ScheduleUpdates, "Delay Notice", "Trip to Buea delayed by 30 mins.", "Today, 7:30 AM", LocalDate.now()),
                NotificationItem(17, NotificationCategory.ScheduleUpdates, "Gate Change", "Trip to Yaoundé now boarding at Gate 3.", "Today, 11:00 AM", LocalDate.now()),
                NotificationItem(18, NotificationCategory.ScheduleUpdates, "Time Change", "Limbe express trip rescheduled to 6:30 AM.", "Yesterday", LocalDate.now().minusDays(1)),
                NotificationItem(19, NotificationCategory.ScheduleUpdates, "Rescheduled", "Your Bamenda trip has a new time: 5:45 AM.", "2 days ago", LocalDate.now().minusDays(2)),
                NotificationItem(20, NotificationCategory.ScheduleUpdates, "Canceled", "Trip to Kribi canceled due to weather.", "Today, 12:00 PM", LocalDate.now())
            )

    }

    var selectedTab by remember { mutableStateOf(NotificationCategory.BookingConfirmations) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val selectedIds = remember { mutableStateListOf<Int>() }
    var showDetail by remember { mutableStateOf<NotificationItem?>(null) }

    val filtered = allNotifications.filter {
        it.category == selectedTab &&
                (it.title.contains(searchQuery.text, true) || it.message.contains(searchQuery.text, true))
    }
    val grouped = filtered.groupBy { it.date }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Notifications") },
            actions = {
                if (filtered.isNotEmpty()) {
                    IconButton(onClick = {
                        filtered.forEach { it.read = true }
                    }) {
                        Icon(Icons.Default.DoneAll, contentDescription = "Mark All Read")
                    }
                    IconButton(onClick = {
                        allNotifications.removeAll(filtered)
                    }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All")
                    }
                }
            }
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search notifications...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )

        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            NotificationCategory.values().forEach { category ->
                Tab(
                    selected = selectedTab == category,
                    onClick = {
                        selectedTab = category
                        selectedIds.clear()
                    },
                    text = { Text(category.displayName, fontSize = 12.sp) }
                )
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notifications", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                grouped.forEach { (date, notifications) ->
                    stickyHeader {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val label = when (date) {
                                LocalDate.now() -> "Today"
                                LocalDate.now().minusDays(1) -> "Yesterday"
                                else -> date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                            }
                            Text(
                                text = label,
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    items(notifications) { notif ->
                        val isSelected = selectedIds.contains(notif.id)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(if (notif.read) Color.LightGray else Color.White)
                                .clickable {
                                    showDetail = notif
                                },
                            colors = CardDefaults.cardColors(containerColor = if (notif.read) Color(0xFFEEEEEE) else Color(0xFFFFFFFF))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = {
                                            if (isSelected) selectedIds.remove(notif.id) else selectedIds.add(notif.id)
                                        }
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(notif.title, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Text(notif.message, color = Color.DarkGray, fontSize = 14.sp)
                                Text(notif.timestamp, color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (selectedIds.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    allNotifications.forEach { if (it.id in selectedIds) it.read = true }
                    selectedIds.clear()
                }) {
                    Text("Mark Read")
                }
                Button(onClick = {
                    allNotifications.removeAll { it.id in selectedIds }
                    selectedIds.clear()
                }) {
                    Text("Delete")
                }
            }
        }
    }

    // Detail Dialog
    showDetail?.let { notif ->
        AlertDialog(
            onDismissRequest = { showDetail = null },
            confirmButton = {
                TextButton(onClick = { showDetail = null }) {
                    Text("Close")
                }
            },
            title = { Text(notif.title) },
            text = {
                Column {
                    Text(notif.message)
                    Spacer(Modifier.height(8.dp))
                    Text("Timestamp: ${notif.timestamp}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        )
    }
}
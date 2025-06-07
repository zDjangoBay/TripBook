package com.android.tripbook.ui.uis

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.manager.NotificationManager
import com.android.tripbook.model.TripNotification
import com.android.tripbook.ui.components.NotificationsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notificationManager: NotificationManager,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationClick: (TripNotification) -> Unit
) {
    val notifications by notificationManager.notifications.collectAsState()
    val unreadCount by notificationManager.unreadCount.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Notifications",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                containerColor = Color(0xFFFF4444)
                            ) {
                                Text(
                                    text = unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Mark all as read button
                    if (unreadCount > 0) {
                        IconButton(
                            onClick = { notificationManager.markAllAsRead() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Mark all as read",
                                tint = Color.White
                            )
                        }
                    }
                    
                    // Menu button
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = Color.White
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    showMenu = false
                                    onSettingsClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null
                                    )
                                }
                            )
                            
                            if (notifications.isNotEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Clear All") },
                                    onClick = {
                                        showMenu = false
                                        notificationManager.clearAllNotifications()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF667EEA),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Quick stats card
            if (notifications.isNotEmpty()) {
                NotificationStatsCard(
                    totalNotifications = notifications.size,
                    unreadCount = unreadCount,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Notifications list
            NotificationsList(
                notifications = notifications,
                onNotificationClick = { notification ->
                    notificationManager.markAsRead(notification.id)
                    onNotificationClick(notification)
                },
                onMarkAsRead = { notification ->
                    notificationManager.markAsRead(notification.id)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NotificationStatsCard(
    totalNotifications: Int,
    unreadCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Notifications,
                label = "Total",
                value = totalNotifications.toString(),
                color = Color(0xFF667EEA)
            )
            
            StatItem(
                icon = Icons.Default.FiberNew,
                label = "Unread",
                value = unreadCount.toString(),
                color = if (unreadCount > 0) Color(0xFFFF4444) else Color(0xFF999999)
            )
            
            StatItem(
                icon = Icons.Default.Done,
                label = "Read",
                value = (totalNotifications - unreadCount).toString(),
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
    }
}

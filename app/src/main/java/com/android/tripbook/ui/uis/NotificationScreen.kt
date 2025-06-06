package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.model.*
import com.android.tripbook.ui.components.*
import com.android.tripbook.viewmodel.NotificationViewModel

/**
 * Main notification screen showing notification history, settings, and scheduled notifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    onNavigateToTrip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: NotificationViewModel = viewModel { 
        NotificationViewModel(application = context.applicationContext as android.app.Application) 
    }
    val uiState by viewModel.uiState.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2),
                        Color(0xFF1565C0)
                    )
                )
            )
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = "Notifications",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    if (uiState.stats.unreadCount > 0) {
                        Text(
                            text = "${uiState.stats.unreadCount} unread",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            // Menu button
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Mark all as read") },
                        onClick = {
                            viewModel.markAllAsRead()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.DoneAll, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Clear all") },
                        onClick = {
                            viewModel.clearAllNotifications()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ClearAll, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            viewModel.toggleSettings()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Settings, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Test notification") },
                        onClick = {
                            viewModel.sendTestNotification()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.BugReport, contentDescription = null)
                        }
                    )
                }
            }
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Stats summary
                NotificationStatsSummary(
                    stats = uiState.stats,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Quick actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.toggleScheduled() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scheduled")
                    }
                    
                    OutlinedButton(
                        onClick = { viewModel.sendTestNotification() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test")
                    }
                }
                
                // Filter chips
                NotificationFilterChips(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = viewModel::setFilter,
                    stats = uiState.stats,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Show scheduled notifications if toggled
                if (uiState.showScheduled) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Scheduled Notifications",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                IconButton(
                                    onClick = { viewModel.toggleScheduled() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (uiState.scheduledNotifications.isEmpty()) {
                                Text(
                                    text = "No scheduled notifications",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                uiState.scheduledNotifications.forEach { scheduledNotification ->
                                    ScheduledNotificationCard(
                                        scheduledNotification = scheduledNotification,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Notifications list
                val filteredNotifications = viewModel.getFilteredNotifications()
                
                if (filteredNotifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "No notifications",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "No notifications",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            
                            Text(
                                text = "You're all caught up!",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredNotifications) { notification ->
                            NotificationCard(
                                notification = notification,
                                onNotificationClick = { clickedNotification ->
                                    // Mark as read when clicked
                                    if (!clickedNotification.isRead) {
                                        viewModel.markAsRead(clickedNotification.id)
                                    }
                                    
                                    // Handle navigation if there's an action
                                    val targetScreen = viewModel.handleNotificationAction(clickedNotification)
                                    if (targetScreen != null && clickedNotification.tripId != null) {
                                        onNavigateToTrip(clickedNotification.tripId)
                                    }
                                },
                                onMarkAsRead = viewModel::markAsRead,
                                onDelete = viewModel::deleteNotification
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar or dialog
            viewModel.clearError()
        }
    }
}

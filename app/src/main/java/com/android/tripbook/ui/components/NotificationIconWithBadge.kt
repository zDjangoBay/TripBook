package com.android.tripbook.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.viewmodel.NotificationViewModel

/**
 * Notification icon with badge showing unread count
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationIconWithBadge(
    onNotificationClick: () -> Unit,
    onTestNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notificationViewModel: NotificationViewModel = viewModel { 
        NotificationViewModel(application = context.applicationContext as android.app.Application) 
    }
    val uiState by notificationViewModel.uiState.collectAsState()
    
    Box(
        modifier = modifier
            .size(48.dp)
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .combinedClickable(
                onClick = onNotificationClick,
                onLongClick = onTestNotificationClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications (Long press for test)",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        
        // Badge for unread count
        if (uiState.stats.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = Color.Red,
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.stats.unreadCount > 9) "9+" else uiState.stats.unreadCount.toString(),
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

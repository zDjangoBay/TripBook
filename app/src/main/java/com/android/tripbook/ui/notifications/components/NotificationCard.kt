package com.android.tripbook.ui.notifications.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.notifications.models.NotificationType
import com.android.tripbook.ui.notifications.viewmodels.NotificationDisplayItem

@Composable
fun NotificationCard(
    notification: NotificationDisplayItem,
    onNotificationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNotificationClick(notification.id) },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                MaterialTheme.colorScheme.surface else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icône de notification
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Color(android.graphics.Color.parseColor(notification.iconColor))
                            .copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                NotificationIcon(
                    type = notification.type,
                    color = Color(android.graphics.Color.parseColor(notification.iconColor))
                )
            }

            // Contenu de la notification
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 16.sp,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = notification.timeAgo,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Indicateur non lu
            if (!notification.isRead) {
                Canvas(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.Top)
                ) {
                    drawCircle(
                        color = Color(0xFF3B82F6),
                        radius = size.minDimension / 2
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationIcon(
    type: NotificationType,
    color: Color,
    modifier: Modifier = Modifier
) {
    when (type) {
        NotificationType.BOOKING_CONFIRMED -> {
            Icon(
                painter = androidx.compose.material.icons.Icons.Default.Check,
                contentDescription = "Réservation confirmée",
                tint = color,
                modifier = modifier.size(24.dp)
            )
        }
        NotificationType.PAYMENT_SUCCESS -> {
            Icon(
                painter = androidx.compose.material.icons.Icons.Default.CreditCard,
                contentDescription = "Paiement réussi",
                tint = color,
                modifier = modifier.size(24.dp)
            )
        }
        NotificationType.TRIP_REMINDER -> {
            Icon(
                painter = androidx.compose.material.icons.Icons.Default.DateRange,
                contentDescription = "Rappel de voyage",
                tint = color,
                modifier = modifier.size(24.dp)
            )
        }
        NotificationType.BOOKING_MODIFIED -> {
            Icon(
                painter = androidx.compose.material.icons.Icons.Default.Edit,
                contentDescription = "Réservation modifiée",
                tint = color,
                modifier = modifier.size(24.dp)
            )
        }
        NotificationType.BOOKING_CANCELLED -> {
            Icon(
                painter = androidx.compose.material.icons.Icons.Default.Cancel,
                contentDescription = "Réservation annulée",
                tint = color,
                modifier = modifier.size(24.dp)
            )
        }
        NotificationType.REFUND_PROCESSED -> {
            Icon(
                painter = androidx.compose.material.icons.Icons.Default.AccountBalance,
                contentDescription = "Remboursement traité",
                tint = color,
                modifier = modifier.size(24.dp)
            )
        }
    }
}
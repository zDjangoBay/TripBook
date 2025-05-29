package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.tripbook.model.Reservation
import com.android.tripbook.ui.theme.TextPrimary
import com.android.tripbook.ui.theme.TextSecondary
import com.android.tripbook.ui.theme.TripBookPrimary
import com.android.tripbook.util.DateUtils

/**
 * Types of sharing options
 */
enum class ShareType {
    TEXT,
    CALENDAR
}

/**
 * Dialog for sharing a reservation
 */
@Composable
fun ShareOptionsDialog(
    reservation: Reservation,
    onDismiss: () -> Unit,
    onShare: (ShareType) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share Reservation",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Share options
                Column {
                    // Share as text
                    ShareOption(
                        icon = Icons.Default.Share,
                        title = "Share as Text",
                        description = "Share the reservation details as plain text",
                        onClick = { onShare(ShareType.TEXT) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Share as calendar event
                    ShareOption(
                        icon = Icons.Default.CalendarMonth,
                        title = "Share as Calendar Event",
                        description = "Share the reservation as a calendar event",
                        onClick = { onShare(ShareType.CALENDAR) }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

/**
 * Share option item
 */
@Composable
private fun ShareOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TripBookPrimary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * Build share text for a reservation
 */
fun buildShareText(reservation: Reservation): String {
    return """
        🧳 Trip to ${reservation.destination}
        
        📅 ${DateUtils.formatDateRange(reservation.startDate, reservation.endDate)}
        🏨 ${reservation.accommodationName ?: "Not specified"}
        💰 ${reservation.price} ${reservation.currency}
        🎫 Booking Reference: ${reservation.bookingReference}
        
        Status: ${reservation.status.name.lowercase().replaceFirstChar { it.uppercase() }}
        
        ${reservation.notes ?: ""}
        
        Shared via TripBook
    """.trimIndent()
}

/**
 * Build calendar text for a reservation
 */
fun buildCalendarText(reservation: Reservation): String {
    return """
        BEGIN:VCALENDAR
        VERSION:2.0
        BEGIN:VEVENT
        SUMMARY:${reservation.title} - ${reservation.destination}
        DTSTART:${formatCalendarDate(reservation.startDate)}
        DTEND:${formatCalendarDate(reservation.endDate)}
        LOCATION:${reservation.accommodationName ?: reservation.destination}
        DESCRIPTION:${buildCalendarDescription(reservation)}
        END:VEVENT
        END:VCALENDAR
    """.trimIndent()
}

/**
 * Format date for calendar
 */
private fun formatCalendarDate(date: java.time.LocalDateTime): String {
    return date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
}

/**
 * Build description for calendar event
 */
private fun buildCalendarDescription(reservation: Reservation): String {
    val description = StringBuilder()
    
    description.append("Destination: ${reservation.destination}\\n")
    description.append("Price: ${reservation.price} ${reservation.currency}\\n")
    description.append("Booking Reference: ${reservation.bookingReference}\\n")
    
    if (reservation.accommodationName != null) {
        description.append("Accommodation: ${reservation.accommodationName}\\n")
    }
    
    if (reservation.accommodationAddress != null) {
        description.append("Address: ${reservation.accommodationAddress}\\n")
    }
    
    if (reservation.transportInfo != null) {
        description.append("Transport: ${reservation.transportInfo}\\n")
    }
    
    if (reservation.notes != null) {
        description.append("Notes: ${reservation.notes}\\n")
    }
    
    description.append("Status: ${reservation.status.name.lowercase().replaceFirstChar { it.uppercase() }}\\n")
    description.append("Shared via TripBook")
    
    return description.toString()
}

package com.android.tripbook.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Reservation
import com.android.tripbook.model.ReservationStatus
import com.android.tripbook.ui.theme.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

/**
 * Week calendar view
 */
@Composable
fun WeekCalendarView(
    selectedDate: LocalDate,
    reservations: List<Reservation>,
    onDateClick: (LocalDate) -> Unit,
    onReservationClick: (Reservation) -> Unit,
    onAddReservationClick: (LocalDate) -> Unit
) {
    // Calculate the start of the week
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val startOfWeek = selectedDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
    
    // Create a list of days in the week
    val daysInWeek = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    
    // Group reservations by date
    val reservationsByDate = reservations.flatMap { reservation ->
        val startDate = LocalDate.of(
            reservation.startDate.year,
            reservation.startDate.month,
            reservation.startDate.dayOfMonth
        )
        val endDate = LocalDate.of(
            reservation.endDate.year,
            reservation.endDate.month,
            reservation.endDate.dayOfMonth
        )
        
        // Create a list of all dates in the reservation range
        var currentDate = startDate
        val dates = mutableListOf<Pair<LocalDate, Reservation>>()
        
        while (!currentDate.isAfter(endDate)) {
            dates.add(Pair(currentDate, reservation))
            currentDate = currentDate.plusDays(1)
        }
        
        dates
    }.groupBy { it.first }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Days of the week header
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(daysInWeek) { date ->
                val isToday = date.equals(LocalDate.now())
                val isSelected = date.equals(selectedDate)
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(40.dp)
                        .clickable { onDateClick(date) }
                ) {
                    // Day of week
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Date
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> TripBookPrimary
                                    isToday -> TripBookPrimary.copy(alpha = 0.1f)
                                    else -> Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = when {
                                isSelected -> Color.White
                                isToday -> TripBookPrimary
                                else -> TextPrimary
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Reservation indicator
                    val hasReservations = reservationsByDate[date]?.isNotEmpty() == true
                    if (hasReservations) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(TripBookPrimary)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Time slots (8 AM to 8 PM)
        val timeSlots = (8..20).toList()
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(timeSlots) { hour ->
                TimeSlot(
                    hour = hour,
                    date = selectedDate,
                    reservations = reservationsByDate[selectedDate]?.map { it.second } ?: emptyList(),
                    onReservationClick = onReservationClick,
                    onAddClick = { onAddReservationClick(selectedDate) }
                )
            }
        }
    }
}

/**
 * Time slot in the week view
 */
@Composable
private fun TimeSlot(
    hour: Int,
    date: LocalDate,
    reservations: List<Reservation>,
    onReservationClick: (Reservation) -> Unit,
    onAddClick: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("h a")
    val slotTime = LocalTime.of(hour, 0)
    val slotDateTime = LocalDateTime.of(date, slotTime)
    
    // Filter reservations that overlap with this time slot
    val slotReservations = reservations.filter { reservation ->
        val reservationStart = reservation.startDate
        val reservationEnd = reservation.endDate
        
        // Check if the reservation overlaps with this hour
        val slotStart = slotDateTime
        val slotEnd = slotDateTime.plusHours(1)
        
        (reservationStart.isBefore(slotEnd) || reservationStart.isEqual(slotEnd)) &&
        (reservationEnd.isAfter(slotStart) || reservationEnd.isEqual(slotStart))
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Time header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time
            Text(
                text = slotTime.format(timeFormatter),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.width(60.dp)
            )
            
            // Line
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                color = CardBorder
            )
            
            // Add button
            if (slotReservations.isEmpty()) {
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Reservation",
                        tint = TripBookPrimary
                    )
                }
            }
        }
        
        // Reservations in this time slot
        slotReservations.forEach { reservation ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 60.dp, bottom = 8.dp)
                    .clickable { onReservationClick(reservation) },
                colors = CardDefaults.cardColors(
                    containerColor = when (reservation.status) {
                        ReservationStatus.CONFIRMED -> StatusConfirmed.copy(alpha = 0.1f)
                        ReservationStatus.PENDING -> StatusPending.copy(alpha = 0.1f)
                        ReservationStatus.CANCELLED -> StatusCancelled.copy(alpha = 0.1f)
                        ReservationStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.1f)
                    }
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = when (reservation.status) {
                        ReservationStatus.CONFIRMED -> StatusConfirmed.copy(alpha = 0.3f)
                        ReservationStatus.PENDING -> StatusPending.copy(alpha = 0.3f)
                        ReservationStatus.CANCELLED -> StatusCancelled.copy(alpha = 0.3f)
                        ReservationStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.3f)
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = reservation.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = reservation.destination,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

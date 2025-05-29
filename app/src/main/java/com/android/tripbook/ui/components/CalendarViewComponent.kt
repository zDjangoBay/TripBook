package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.Reservation
import com.android.tripbook.ui.theme.*
import com.android.tripbook.util.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * Calendar view component for displaying reservations by month
 */
@Composable
fun CalendarViewComponent(
    selectedMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit,
    reservations: List<Reservation>,
    onReservationClick: (Reservation) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onMonthChanged(selectedMonth.minusMonths(1))
            }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = TripBookPrimary
                )
            }
            
            Text(
                text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
            
            IconButton(onClick = {
                onMonthChanged(selectedMonth.plusMonths(1))
            }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Month",
                    tint = TripBookPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Days of week header
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayOfWeek in DayOfWeek.values()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextSecondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar grid
        val firstDayOfMonth = selectedMonth.atDay(1)
        val lastDayOfMonth = selectedMonth.atEndOfMonth()
        val firstDayOfGrid = firstDayOfMonth.minusDays(firstDayOfMonth.dayOfWeek.value.toLong() - 1)
        
        val daysInGrid = mutableListOf<LocalDate>()
        var currentDay = firstDayOfGrid
        
        // Add days to grid (6 weeks)
        repeat(42) {
            daysInGrid.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }
        
        // Group reservations by date
        val reservationsByDate = reservations.groupBy { 
            LocalDate.of(
                it.startDate.year, 
                it.startDate.monthValue, 
                it.startDate.dayOfMonth
            )
        }
        
        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            items(daysInGrid) { date ->
                val isCurrentMonth = date.month == selectedMonth.month
                val hasReservations = reservationsByDate[date]?.isNotEmpty() == true
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                hasReservations -> TripBookPrimary.copy(alpha = 0.1f)
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = if (date == LocalDate.now()) 2.dp else 0.dp,
                            color = if (date == LocalDate.now()) TripBookPrimary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(enabled = hasReservations) {
                            reservationsByDate[date]?.firstOrNull()?.let { 
                                onReservationClick(it) 
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (hasReservations || date == LocalDate.now()) 
                                FontWeight.Bold else FontWeight.Normal
                        ),
                        color = when {
                            !isCurrentMonth -> TextDisabled
                            hasReservations -> TripBookPrimary
                            else -> TextPrimary
                        }
                    )
                    
                    if (hasReservations) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 2.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(TripBookPrimary)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Reservations for selected month
        Text(
            text = "Reservations for ${selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))}",
            style = MaterialTheme.typography.titleSmall,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (reservations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No reservations for this month",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn {
                items(reservations) { reservation ->
                    ReservationCard(
                        reservation = reservation,
                        onClick = onReservationClick
                    )
                }
            }
        }
    }
}

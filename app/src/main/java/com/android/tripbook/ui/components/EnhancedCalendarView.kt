package com.android.tripbook.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

/**
 * Calendar view modes
 */
enum class CalendarViewMode {
    MONTH,
    WEEK,
    AGENDA
}

/**
 * Enhanced calendar view with month, week, and agenda views
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnhancedCalendarView(
    reservations: List<Reservation>,
    onReservationClick: (Reservation) -> Unit,
    onAddReservationClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Calendar header with view mode selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Month/Week selector
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    when (viewMode) {
                        CalendarViewMode.MONTH -> selectedMonth = selectedMonth.minusMonths(1)
                        CalendarViewMode.WEEK -> selectedDate = selectedDate.minusWeeks(1)
                        CalendarViewMode.AGENDA -> selectedDate = selectedDate.minusDays(7)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous",
                        tint = TripBookPrimary
                    )
                }

                AnimatedContent(
                    targetState = when (viewMode) {
                        CalendarViewMode.MONTH -> selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                        CalendarViewMode.WEEK -> {
                            val weekStart = selectedDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
                            val weekEnd = weekStart.plusDays(6)
                            "${weekStart.format(DateTimeFormatter.ofPattern("MMM d"))} - ${weekEnd.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}"
                        }
                        CalendarViewMode.AGENDA -> selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                    },
                    transitionSpec = {
                        fadeIn() + slideInHorizontally() with fadeOut() + slideOutHorizontally()
                    },
                    label = "date header"
                ) { dateText ->
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                }

                IconButton(onClick = {
                    when (viewMode) {
                        CalendarViewMode.MONTH -> selectedMonth = selectedMonth.plusMonths(1)
                        CalendarViewMode.WEEK -> selectedDate = selectedDate.plusWeeks(1)
                        CalendarViewMode.AGENDA -> selectedDate = selectedDate.plusDays(7)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next",
                        tint = TripBookPrimary
                    )
                }
            }

            // View mode selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconToggleButton(
                    checked = viewMode == CalendarViewMode.MONTH,
                    onCheckedChange = { if (it) viewMode = CalendarViewMode.MONTH }
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarViewMonth,
                        contentDescription = "Month View",
                        tint = if (viewMode == CalendarViewMode.MONTH) TripBookPrimary else TextSecondary
                    )
                }

                IconToggleButton(
                    checked = viewMode == CalendarViewMode.WEEK,
                    onCheckedChange = { if (it) viewMode = CalendarViewMode.WEEK }
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarViewWeek,
                        contentDescription = "Week View",
                        tint = if (viewMode == CalendarViewMode.WEEK) TripBookPrimary else TextSecondary
                    )
                }

                IconToggleButton(
                    checked = viewMode == CalendarViewMode.AGENDA,
                    onCheckedChange = { if (it) viewMode = CalendarViewMode.AGENDA }
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewAgenda,
                        contentDescription = "Agenda View",
                        tint = if (viewMode == CalendarViewMode.AGENDA) TripBookPrimary else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar content based on view mode
        AnimatedContent(
            targetState = viewMode,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
            },
            label = "calendar view"
        ) { currentViewMode ->
            when (currentViewMode) {
                CalendarViewMode.MONTH -> MonthCalendarView(
                    selectedMonth = selectedMonth,
                    reservations = reservations,
                    onDateClick = { date ->
                        selectedDate = date
                        viewMode = CalendarViewMode.AGENDA
                    },
                    onReservationClick = onReservationClick
                )

                CalendarViewMode.WEEK -> WeekCalendarView(
                    selectedDate = selectedDate,
                    reservations = reservations,
                    onDateClick = { date ->
                        selectedDate = date
                        viewMode = CalendarViewMode.AGENDA
                    },
                    onReservationClick = onReservationClick,
                    onAddReservationClick = onAddReservationClick
                )

                CalendarViewMode.AGENDA -> AgendaView(
                    selectedDate = selectedDate,
                    reservations = reservations.filter { reservation ->
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

                        (selectedDate.isEqual(startDate) || selectedDate.isAfter(startDate)) &&
                        (selectedDate.isEqual(endDate) || selectedDate.isBefore(endDate))
                    },
                    onReservationClick = onReservationClick,
                    onAddReservationClick = { onAddReservationClick(selectedDate) }
                )
            }
        }
    }
}

/**
 * Month calendar view
 */
@Composable
private fun MonthCalendarView(
    selectedMonth: YearMonth,
    reservations: List<Reservation>,
    onDateClick: (LocalDate) -> Unit,
    onReservationClick: (Reservation) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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

        // Determine the first day to display (previous month days to fill first week)
        val firstDayOfGrid = firstDayOfMonth.minusDays(
            ((firstDayOfMonth.dayOfWeek.value - WeekFields.of(Locale.getDefault()).firstDayOfWeek.value + 7) % 7).toLong()
        )

        val daysInGrid = mutableListOf<LocalDate>()
        var currentDay = firstDayOfGrid

        // Add days to grid (6 weeks)
        repeat(42) {
            daysInGrid.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }

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

        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            items(daysInGrid) { date ->
                val isCurrentMonth = date.month == selectedMonth.month
                val isToday = date.equals(LocalDate.now())
                val hasReservations = reservationsByDate[date]?.isNotEmpty() == true

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isToday -> TripBookPrimary.copy(alpha = 0.1f)
                                !isCurrentMonth -> Color.Transparent
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                        .border(
                            width = if (isToday) 1.dp else 0.dp,
                            color = if (isToday) TripBookPrimary else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onDateClick(date) },
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        // Date number
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    !isCurrentMonth -> TextDisabled
                                    isToday -> TripBookPrimary
                                    else -> TextPrimary
                                }
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Reservation indicators
                        if (hasReservations) {
                            val reservations = reservationsByDate[date]!!
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Show up to 3 indicators
                                for (i in 0 until minOf(3, reservations.size)) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .padding(horizontal = 1.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (reservations[i].second.status) {
                                                    com.android.tripbook.model.ReservationStatus.CONFIRMED -> StatusConfirmed
                                                    com.android.tripbook.model.ReservationStatus.PENDING -> StatusPending
                                                    com.android.tripbook.model.ReservationStatus.CANCELLED -> StatusCancelled
                                                    com.android.tripbook.model.ReservationStatus.COMPLETED -> StatusCompleted
                                                }
                                            )
                                    )
                                }

                                // Show "+" if there are more than 3 reservations
                                if (reservations.size > 3) {
                                    Text(
                                        text = "+${reservations.size - 3}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 8.sp
                                        ),
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.android.tripbook.ui.uis.tripcreation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.ui.components.StepHeader
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun DateSelectionStep(
    state: TripCreationState,
    onStateChange: (TripCreationState) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var isSelectingStartDate by remember { mutableStateOf(true) }
    
    Column(modifier = modifier.fillMaxSize()) {
        StepHeader(
            title = "When?",
            subtitle = "Select your travel dates",
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Date Selection Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DateTypeButton(
                        text = "Start Date",
                        isSelected = isSelectingStartDate,
                        selectedDate = state.startDate,
                        onClick = { isSelectingStartDate = true },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    DateTypeButton(
                        text = "End Date",
                        isSelected = !isSelectingStartDate,
                        selectedDate = state.endDate,
                        onClick = { isSelectingStartDate = false },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Calendar Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentMonth = currentMonth.minusMonths(1) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Month",
                            tint = Color(0xFF6B73FF)
                        )
                    }
                    
                    Text(
                        text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    IconButton(
                        onClick = { currentMonth = currentMonth.plusMonths(1) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Month",
                            tint = Color(0xFF6B73FF)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Days of Week Header
                Row(modifier = Modifier.fillMaxWidth()) {
                    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Calendar Grid
                CalendarGrid(
                    currentMonth = currentMonth,
                    startDate = state.startDate,
                    endDate = state.endDate,
                    isSelectingStartDate = isSelectingStartDate,
                    onDateSelected = { selectedDate ->
                        if (isSelectingStartDate) {
                            onStateChange(state.copy(startDate = selectedDate))
                            if (state.endDate == null || selectedDate.isAfter(state.endDate)) {
                                isSelectingStartDate = false
                            }
                        } else {
                            if (state.startDate == null || selectedDate.isAfter(state.startDate)) {
                                onStateChange(state.copy(endDate = selectedDate))
                            }
                        }
                    }
                )
                
                // Selected Dates Summary
                if (state.startDate != null || state.endDate != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Selected Dates",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            state.startDate?.let { startDate ->
                                Text(
                                    text = "Start: ${startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }

                            state.endDate?.let { endDate ->
                                Text(
                                    text = "End: ${endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }

                            if (state.startDate != null && state.endDate != null) {
                                val duration = state.endDate.toEpochDay() - state.startDate.toEpochDay()
                                Text(
                                    text = "Duration: $duration days",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B73FF),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateTypeButton(
    text: String,
    isSelected: Boolean,
    selectedDate: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF6B73FF) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = text,
            tint = if (isSelected) Color(0xFF6B73FF) else Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF6B73FF) else Color.Gray
        )
        if (selectedDate != null) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    startDate: LocalDate?,
    endDate: LocalDate?,
    @Suppress("UNUSED_PARAMETER") isSelectingStartDate: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(240.dp)
    ) {
        // Empty cells for days before the first day of the month
        items(firstDayOfWeek) {
            Box(modifier = Modifier.size(40.dp))
        }
        
        // Days of the month
        items(daysInMonth) { dayIndex ->
            val day = dayIndex + 1
            val date = currentMonth.atDay(day)
            val isToday = date == LocalDate.now()
            val isSelected = date == startDate || date == endDate
            val isInRange = startDate != null && endDate != null && 
                           date.isAfter(startDate) && date.isBefore(endDate)
            val isPastDate = date.isBefore(LocalDate.now())
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            isSelected -> Color(0xFF6B73FF)
                            isInRange -> Color(0xFF6B73FF).copy(alpha = 0.3f)
                            isToday -> Color(0xFFE3F2FD)
                            else -> Color.Transparent
                        }
                    )
                    .clickable(enabled = !isPastDate) {
                        onDateSelected(date)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 14.sp,
                    color = when {
                        isPastDate -> Color.Gray.copy(alpha = 0.5f)
                        isSelected -> Color.White
                        isToday -> Color(0xFF6B73FF)
                        else -> Color.Black
                    },
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

package com.android.tripbook.ui.components.travel


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AvailabilityCalendar(
    availableDates: List<LocalDate>,
    selectedStartDate: LocalDate? = null,
    selectedEndDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit
) {
    val currentMonth = YearMonth.now()
    val firstDayOfMonth = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentMonth.year,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            DayOfWeek.entries.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(4.dp)
        ) {
            val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
            items(if (firstDayOfWeek == 0) 6 else firstDayOfWeek - 1) {
                Spacer(modifier = Modifier.size(40.dp))
            }

            items<Any>(1..daysInMonth) { dayOfMonth ->
                val date = firstDayOfMonth.withDayOfMonth(dayOfMonth)
                val isAvailable = availableDates.contains(date)
                val isSelectedStart = date == selectedStartDate
                val isSelectedEnd = date == selectedEndDate
                val isWithinRange = selectedStartDate != null && selectedEndDate != null &&
                        date.isAfter(selectedStartDate) && date.isBefore(selectedEndDate)

                val backgroundColor = when {
                    isSelectedStart || isSelectedEnd -> MaterialTheme.colorScheme.primary
                    isWithinRange -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    isAvailable -> MaterialTheme.colorScheme.secondaryContainer
                    else -> Color.Transparent
                }

                val textColor = when {
                    isSelectedStart || isSelectedEnd -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .border(
                            width = 1.dp,
                            color = if (isAvailable) MaterialTheme.colorScheme.secondary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(enabled = isAvailable) { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayOfMonth.toString(),
                        color = textColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun <LazyGridItemScope> Any.items(count: IntRange, itemContent: @Composable LazyGridItemScope.(index: Int) -> Unit) {

}

@Preview(showBackground = true)
@Composable
fun PreviewAvailabilityCalendar() {
    val sampleAvailableDates = listOf(
        LocalDate.now().plusDays(1),
        LocalDate.now().plusDays(3),
        LocalDate.now().plusDays(5),
        LocalDate.now().plusDays(8),
        LocalDate.now().plusDays(10)
    )
    AvailabilityCalendar(availableDates = sampleAvailableDates, onDateSelected = {})
}
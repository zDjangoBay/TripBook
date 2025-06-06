package com.android.tripbook.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionStep(
    trip: Trip?,
    selectedStartDate: LocalDate?,
    selectedEndDate: LocalDate?,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onNextStep: () -> Unit
) {
    var startDate by remember { mutableStateOf(selectedStartDate ?: LocalDate.now()) }
    var endDate by remember { mutableStateOf(selectedEndDate ?: LocalDate.now().plusDays(3)) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)  // Enable vertical scrolling
    ) {
        // Title
        Text(
            text = "Select Travel Dates",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Trip info card
        trip?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it.caption,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Date selection
        DateRangePicker(
            startDate = startDate,
            endDate = endDate,
            onStartDateChange = { startDate = it },
            onEndDateChange = { endDate = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Continue button
        Button(
            onClick = {
                onDateRangeSelected(startDate, endDate)
                onNextStep()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = startDate <= endDate
        ) {
            Text("Continue to Agency Selection")
        }
    }
}

@Composable
fun DateRangePicker(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Select your travel dates",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Start date selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Check-in:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.width(100.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onStartDateChange(startDate.minusDays(1)) },
                    enabled = startDate > LocalDate.now()
                ) {
                    Text("-", style = MaterialTheme.typography.titleLarge)
                }

                Text(
                    text = startDate.format(formatter),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { onStartDateChange(startDate.plusDays(1)) }) {
                    Text("+", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        // End date selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Check-out:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.width(100.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onEndDateChange(endDate.minusDays(1)) },
                    enabled = endDate > startDate
                ) {
                    Text("-", style = MaterialTheme.typography.titleLarge)
                }

                Text(
                    text = endDate.format(formatter),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { onEndDateChange(endDate.plusDays(1)) }) {
                    Text("+", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        // Trip duration
        val duration = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1
        Text(
            text = "Trip duration: $duration days",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
            color = if (duration > 0) Color.Unspecified else Color.Red
        )
    }
}

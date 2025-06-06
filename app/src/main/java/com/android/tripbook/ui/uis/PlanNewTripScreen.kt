package com.android.tripbook.ui.uis

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.Trip
//import androidx.lifecycle.ViewModel
import com.android.tripbook.model.TripViewModel
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DatePickerTextField(
    label: String,
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val formattedDate = remember(date) {
        date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }

    var showDialog by remember { mutableStateOf(false) }

    // Show dialog when flag is true
    if (showDialog) {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
                showDialog = false
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        ).show()
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { showDialog = true }) {

        OutlinedTextField(
            value = formattedDate,
            onValueChange = {}, // Read-only
            readOnly = true,
            enabled = false, // Disable to make sure input doesn't respond
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Pick date"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}


@Composable
fun TimePickerTextField(
    label: String,
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    var timeText by remember { mutableStateOf(time.format(DateTimeFormatter.ofPattern("hh:mm a"))) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val dialog = TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val selectedTime = LocalTime.of(hour, minute)
                        timeText = selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                        onTimeSelected(selectedTime)
                    },
                    time.hour,
                    time.minute,
                    false // 12-hour format
                )
                dialog.show()
            }
    ) {
        OutlinedTextField(
            value = timeText,
            onValueChange = {},
            readOnly = true,
            enabled = false, // disables internal click handling
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Default.Schedule, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun PlanNewTripScreen(onBackClick: () -> Unit,viewModel: TripViewModel) {

    val context = LocalContext.current

    TripBookTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6B5B95))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Back button and header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Plan New Trip",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Create your African adventure",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Content in white card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        val name = viewModel.tripInfo.name

                        // Trip Name
                        Text(text = "Trip Name", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = name,
                            onValueChange = {
                                viewModel.updateName(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        val destination = viewModel.tripInfo.destination

                        // Destination
                        Text(text = "Destination", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = destination,
                            onValueChange = { viewModel.updateDestination(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Start Date
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            TimePickerTextField(
                                label = "Start Time",
                                time = viewModel.getLocalTime(),
                                onTimeSelected = { viewModel.updateTime(it) }
                            )

                            Spacer(modifier = Modifier.height(16.dp))


                            DatePickerTextField(
                                label = "Select Date",
                                date = viewModel.getLocalDate(), // Parses from string
                                onDateSelected = { viewModel.updateDate(it) }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val travelers = viewModel.tripInfo.travelers
                        // Number of Travelers
                        Text(text = "Number of Travelers", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = viewModel.travelersInput,
                                onValueChange = {
                                    viewModel.updateTravelers(it)
                                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                    .padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        val budget = viewModel.tripInfo.budget

                        // Budget
                        Text(text = "Budget (USD)", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = viewModel.budgetInput,
                            onValueChange = {
                                viewModel.updateBudget(it)
                                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        val status = viewModel.tripInfo.status

                        // Trip Type
                        Text(text = "Trip Type", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Create Trip Button
                        Button(
                            onClick = {
                                onBackClick()
                                val Trip = Trip(
                                    name = name,
                                    destination = destination,
                                    travelers = travelers,
                                    budget = budget,
                                    startDate = viewModel.tripInfo.startDate,
                                    time = viewModel.tripInfo.time,
                                    status = status
                                )
                                viewModel.saveData(Trip = Trip, context = context)
                                      },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6B73FF)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CREATE TRIP",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) Color(0xFF6B73FF) else Color(0xFFF0F0F0)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
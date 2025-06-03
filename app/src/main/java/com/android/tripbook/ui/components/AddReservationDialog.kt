package com.android.tripbook.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.tripbook.R
import com.android.tripbook.model.Reservation
import com.android.tripbook.model.ReservationStatus
import com.android.tripbook.repository.ReservationRepository
import com.android.tripbook.ui.animation.AnimationUtils
import com.android.tripbook.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Modern dialog for adding a new reservation with a step-based approach
 */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun AddReservationDialog(
    repository: ReservationRepository,
    onDismiss: () -> Unit,
    onReservationAdded: () -> Unit
) {
    // Form data
    var title by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var accommodationName by remember { mutableStateOf("") }
    var accommodationAddress by remember { mutableStateOf("") }
    var transportInfo by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var bookingReference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(ReservationStatus.CONFIRMED) }

    // Default dates
    val now = LocalDateTime.now()
    val defaultStartDate = now.plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0)
    val defaultEndDate = now.plusDays(8).withHour(11).withMinute(0).withSecond(0).withNano(0)

    // Format dates for display
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    var startDateText by remember { mutableStateOf(defaultStartDate.format(dateFormatter)) }
    var endDateText by remember { mutableStateOf(defaultEndDate.format(dateFormatter)) }

    // Multi-step form state
    var currentStep by remember { mutableStateOf(0) }
    val totalSteps = 4

    // Form validation
    var showValidationErrors by remember { mutableStateOf(false) }

    // Validation for each step
    val isStep1Valid = title.isNotBlank() && destination.isNotBlank()
    val isStep2Valid = true // Dates are pre-filled with valid values
    val isStep3Valid = price.isNotBlank() && bookingReference.isNotBlank()
    val isStep4Valid = true // Optional fields

    // Overall form validation
    val isFormValid = isStep1Valid && isStep2Valid && isStep3Valid && isStep4Valid

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Modern gradient header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(32.dp)
                                    .then(AnimationUtils.rotateAnimation(degrees = 5f, duration = 2000))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "New Trip",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Progress indicator
                LinearProgressIndicator(
                    progress = (currentStep + 1).toFloat() / totalSteps.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )

                // Step indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (step in 0 until totalSteps) {
                        val isCurrentStep = step == currentStep
                        val isCompletedStep = step < currentStep

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrentStep -> MaterialTheme.colorScheme.primary
                                        isCompletedStep -> MaterialTheme.colorScheme.secondary
                                        else -> Color.LightGray
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompletedStep) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            } else {
                                Text(
                                    text = (step + 1).toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Validation error message
                AnimatedVisibility(
                    visible = showValidationErrors &&
                             ((currentStep == 0 && !isStep1Valid) ||
                              (currentStep == 1 && !isStep2Valid) ||
                              (currentStep == 2 && !isStep3Valid) ||
                              (currentStep == 3 && !isStep4Valid)),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Please fill in all required fields",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Form content with step-based approach
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp)
                ) {
                    // Step content with animation
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                // Moving forward
                                slideInHorizontally { width -> width } + fadeIn() with
                                slideOutHorizontally { width -> -width } + fadeOut()
                            } else {
                                // Moving backward
                                slideInHorizontally { width -> -width } + fadeIn() with
                                slideOutHorizontally { width -> width } + fadeOut()
                            }.using(
                                SizeTransform(clip = false)
                            )
                        }
                    ) { step ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 16.dp)
                        ) {
                            when (step) {
                                0 -> {
                                    // Step 1: Basic Information
                                    StepHeader(
                                        icon = Icons.Rounded.Info,
                                        title = "Basic Information",
                                        subtitle = "Let's start with the essentials"
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Title
                                    OutlinedTextField(
                                        value = title,
                                        onValueChange = { title = it },
                                        label = { Text("Trip Title *") },
                                        placeholder = { Text("e.g. Summer Vacation in Paris") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        isError = showValidationErrors && title.isBlank(),
                                        shape = RoundedCornerShape(12.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.Title,
                                                contentDescription = null,
                                                tint = if (title.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Destination
                                    OutlinedTextField(
                                        value = destination,
                                        onValueChange = { destination = it },
                                        label = { Text("Destination *") },
                                        placeholder = { Text("e.g. Paris, France") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        isError = showValidationErrors && destination.isBlank(),
                                        shape = RoundedCornerShape(12.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.LocationOn,
                                                contentDescription = null,
                                                tint = if (destination.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )

                                    // Illustration
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Flight,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .then(AnimationUtils.floatingAnimation(offsetY = 10, duration = 3000))
                                        )
                                    }
                                }

                                1 -> {
                                    // Step 2: Dates and Status
                                    StepHeader(
                                        icon = Icons.Rounded.DateRange,
                                        title = "Dates & Status",
                                        subtitle = "When is your trip and what's its status?"
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Dates
                                    Text(
                                        text = "Trip Dates",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Start Date
                                    OutlinedTextField(
                                        value = startDateText,
                                        onValueChange = { startDateText = it },
                                        label = { Text("Check-in (yyyy-MM-dd HH:mm)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.CalendarToday,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // End Date
                                    OutlinedTextField(
                                        value = endDateText,
                                        onValueChange = { endDateText = it },
                                        label = { Text("Check-out (yyyy-MM-dd HH:mm)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.CalendarToday,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Status
                                    Text(
                                        text = "Reservation Status",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Status chips
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ReservationStatus.values().forEach { status ->
                                            val statusColor = when (status) {
                                                ReservationStatus.CONFIRMED -> StatusConfirmed
                                                ReservationStatus.PENDING -> StatusPending
                                                ReservationStatus.CANCELLED -> StatusCancelled
                                                ReservationStatus.COMPLETED -> StatusCompleted
                                            }

                                            val statusIcon = when (status) {
                                                ReservationStatus.CONFIRMED -> Icons.Rounded.CheckCircle
                                                ReservationStatus.PENDING -> Icons.Rounded.HourglassTop
                                                ReservationStatus.CANCELLED -> Icons.Rounded.Cancel
                                                ReservationStatus.COMPLETED -> Icons.Rounded.Done
                                            }

                                            FilterChip(
                                                selected = selectedStatus == status,
                                                onClick = { selectedStatus = status },
                                                label = {
                                                    Text(status.name.lowercase().replaceFirstChar { it.uppercase() })
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = statusIcon,
                                                        contentDescription = null,
                                                        tint = if (selectedStatus == status) Color.White else statusColor,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = statusColor,
                                                    selectedLabelColor = Color.White,
                                                    selectedLeadingIconColor = Color.White
                                                ),
                                                border = FilterChipDefaults.filterChipBorder(
                                                    borderColor = statusColor.copy(alpha = 0.5f),
                                                    selectedBorderColor = statusColor,
                                                    borderWidth = 1.dp,
                                                    selectedBorderWidth = 0.dp
                                                )
                                            )
                                        }
                                    }
                                }

                                2 -> {
                                    // Step 3: Booking Details
                                    StepHeader(
                                        icon = Icons.Rounded.Receipt,
                                        title = "Booking Details",
                                        subtitle = "Price and booking information"
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Price
                                    OutlinedTextField(
                                        value = price,
                                        onValueChange = { price = it },
                                        label = { Text("Price *") },
                                        placeholder = { Text("e.g. 1200.00") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        isError = showValidationErrors && price.isBlank(),
                                        shape = RoundedCornerShape(12.dp),
                                        leadingIcon = {
                                            Text(
                                                text = "$",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = if (price.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Booking Reference
                                    OutlinedTextField(
                                        value = bookingReference,
                                        onValueChange = { bookingReference = it },
                                        label = { Text("Booking Reference *") },
                                        placeholder = { Text("e.g. ABC123XYZ") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        isError = showValidationErrors && bookingReference.isBlank(),
                                        shape = RoundedCornerShape(12.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.ConfirmationNumber,
                                                contentDescription = null,
                                                tint = if (bookingReference.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )

                                    // Illustration
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.CreditCard,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .then(AnimationUtils.pulseAnimation(pulseFraction = 1.05f, duration = 2000))
                                        )
                                    }
                                }

                                3 -> {
                                    // Step 4: Additional Information
                                    StepHeader(
                                        icon = Icons.Rounded.Info,
                                        title = "Additional Information",
                                        subtitle = "Optional details about your trip"
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Accommodation
                                    Text(
                                        text = "Accommodation Details",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Accommodation Name
                                    OutlinedTextField(
                                        value = accommodationName,
                                        onValueChange = { accommodationName = it },
                                        label = { Text("Accommodation Name") },
                                        placeholder = { Text("e.g. Grand Hotel Paris") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.Hotel,
                                                contentDescription = null,
                                                tint = if (accommodationName.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Accommodation Address
                                    OutlinedTextField(
                                        value = accommodationAddress,
                                        onValueChange = { accommodationAddress = it },
                                        label = { Text("Accommodation Address") },
                                        placeholder = { Text("e.g. 1 Rue de Rivoli, 75001 Paris") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.Place,
                                                contentDescription = null,
                                                tint = if (accommodationAddress.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Transport
                                    Text(
                                        text = "Transport Information",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = transportInfo,
                                        onValueChange = { transportInfo = it },
                                        label = { Text("Transport Details") },
                                        placeholder = { Text("e.g. Flight AF1234, Terminal 2E") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.FlightTakeoff,
                                                contentDescription = null,
                                                tint = if (transportInfo.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Notes
                                    Text(
                                        text = "Additional Notes",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = notes,
                                        onValueChange = { notes = it },
                                        label = { Text("Notes") },
                                        placeholder = { Text("Any additional information about your trip") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        maxLines = 5,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Navigation buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Back/Cancel button
                    OutlinedButton(
                        onClick = {
                            if (currentStep > 0) {
                                currentStep--
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (currentStep > 0) Icons.Rounded.ArrowBack else Icons.Rounded.Close,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(if (currentStep > 0) "Back" else "Cancel")
                    }

                    // Next/Submit button
                    Button(
                        onClick = {
                            when (currentStep) {
                                0 -> {
                                    if (isStep1Valid) {
                                        currentStep++
                                    } else {
                                        showValidationErrors = true
                                    }
                                }
                                1 -> {
                                    if (isStep2Valid) {
                                        currentStep++
                                    } else {
                                        showValidationErrors = true
                                    }
                                }
                                2 -> {
                                    if (isStep3Valid) {
                                        currentStep++
                                    } else {
                                        showValidationErrors = true
                                    }
                                }
                                3 -> {
                                    if (isFormValid) {
                                        try {
                                            // Parse dates
                                            val startDate = LocalDateTime.parse(startDateText, dateFormatter)
                                            val endDate = LocalDateTime.parse(endDateText, dateFormatter)

                                            // Create new reservation
                                            val newReservation = Reservation(
                                                id = UUID.randomUUID().toString(),
                                                title = title,
                                                destination = destination,
                                                startDate = startDate,
                                                endDate = endDate,
                                                status = selectedStatus,
                                                price = price.toDoubleOrNull() ?: 0.0,
                                                currency = "USD", // Default currency
                                                bookingReference = bookingReference,
                                                accommodationName = accommodationName.ifBlank { null },
                                                accommodationAddress = accommodationAddress.ifBlank { null },
                                                transportInfo = transportInfo.ifBlank { null },
                                                notes = notes.ifBlank { null }
                                            )

                                            // Add to repository
                                            repository.addReservation(newReservation)
                                            onReservationAdded()
                                            onDismiss()
                                        } catch (e: Exception) {
                                            // Handle date parsing errors
                                            showValidationErrors = true
                                        }
                                    } else {
                                        showValidationErrors = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (currentStep < totalSteps - 1) "Next" else "Add Trip")

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = if (currentStep < totalSteps - 1) Icons.Rounded.ArrowForward else Icons.Rounded.Check,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

/**
 * Header component for each step in the form
 */
@Composable
private fun StepHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title and subtitle
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

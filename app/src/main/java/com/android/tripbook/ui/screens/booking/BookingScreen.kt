package com.android.tripbook.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.Model.BookingStep
import com.android.tripbook.ViewModel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    modifier: Modifier = Modifier, // ✅ Modifier parameter
    tripId: Int,
    onBack: () -> Unit,
    onBookingComplete: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    // Initialize booking when the screen is first composed
    LaunchedEffect(tripId) {
        viewModel.initBooking(tripId)
    }

    val currentStep by viewModel.currentStep.collectAsState()
    val booking by viewModel.booking.collectAsState()
    val trip by viewModel.trip.collectAsState()
    val options by viewModel.availableOptions.collectAsState()

    var bookingComplete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Your Trip") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding) // ✅ Use passed-in modifier with Scaffold padding
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = when (currentStep) {
                    BookingStep.DATE_SELECTION -> 0.25f
                    BookingStep.TRAVELER_INFO -> 0.5f
                    BookingStep.ADDITIONAL_OPTIONS -> 0.75f
                    BookingStep.SUMMARY -> 1f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Step indicator row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StepIndicator("Dates", currentStep == BookingStep.DATE_SELECTION, currentStep.ordinal > BookingStep.DATE_SELECTION.ordinal)
                StepIndicator("Travelers", currentStep == BookingStep.TRAVELER_INFO, currentStep.ordinal > BookingStep.TRAVELER_INFO.ordinal)
                StepIndicator("Options", currentStep == BookingStep.ADDITIONAL_OPTIONS, currentStep.ordinal > BookingStep.ADDITIONAL_OPTIONS.ordinal)
                StepIndicator("Summary", currentStep == BookingStep.SUMMARY, false)
            }

            // Step-specific content
            when (currentStep) {
                BookingStep.DATE_SELECTION -> {
                    DateSelectionStep(
                        trip = trip,
                        selectedStartDate = booking?.startDate,
                        selectedEndDate = booking?.endDate,
                        onDateRangeSelected = { start, end -> viewModel.updateDates(start, end) },
                        onNextStep = { viewModel.nextStep() }
                    )
                }

                BookingStep.TRAVELER_INFO -> {
                    TravelerInfoStep(
                        adultCount = booking?.adultCount ?: 1,
                        childCount = booking?.childCount ?: 0,
                        contactName = booking?.contactName ?: "",
                        contactEmail = booking?.contactEmail ?: "",
                        contactPhone = booking?.contactPhone ?: "",
                        specialRequirements = booking?.specialRequirements ?: "",
                        onInfoUpdated = { adults, children, name, email, phone, requirements ->
                            viewModel.updateTravelerInfo(adults, children, name, email, phone, requirements)
                        },
                        onNextStep = { viewModel.nextStep() },
                        onPreviousStep = { viewModel.previousStep() }
                    )
                }

                BookingStep.ADDITIONAL_OPTIONS -> {
                    AdditionalOptionsStep(
                        options = options,
                        onOptionToggle = { viewModel.toggleOption(it) },
                        onNextStep = { viewModel.nextStep() },
                        onPreviousStep = { viewModel.previousStep() }
                    )
                }

                BookingStep.SUMMARY -> {
                    if (bookingComplete) {
                        BookingConfirmationScreen(onDone = onBookingComplete)
                    } else {
                        BookingSummaryStep(
                            trip = trip,
                            booking = booking,
                            totalPrice = viewModel.calculateTotalPrice(),
                            termsAgreed = booking?.agreedToTerms ?: false,
                            onTermsAgreedChange = { viewModel.updateTermsAgreement(it) },
                            onPreviousStep = { viewModel.previousStep() },
                            onSubmitBooking = {
                                if (viewModel.submitBooking()) {
                                    bookingComplete = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicator(
    title: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when {
                    isCompleted -> MaterialTheme.colorScheme.primary
                    isActive -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = (title.first()).toString(),
                            color = if (isActive) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive || isCompleted) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BookingConfirmationScreen(
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Booking Confirmed!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your trip has been booked successfully. You will receive a confirmation email shortly.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onDone,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Done")
        }
    }
}
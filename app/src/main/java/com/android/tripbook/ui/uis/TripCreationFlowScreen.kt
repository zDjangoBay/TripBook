package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.ui.components.ProgressIndicator
import com.android.tripbook.ui.components.StepNavigationButtons
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.ui.uis.tripcreation.*
import com.android.tripbook.viewmodel.TripViewModel

@Composable
fun TripCreationFlowScreen(
    tripViewModel: TripViewModel,
    onBackClick: () -> Unit,
    onTripCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tripState by remember { mutableStateOf(TripCreationState()) }
    val isLoading by tripViewModel.isLoading.collectAsState()
    val error by tripViewModel.error.collectAsState()

    // Handle error display
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // You could show a snackbar or toast here
            // For now, we'll just clear the error after showing it
            tripViewModel.clearError()
        }
    }

    TripBookTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF6B5B95))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with back button
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
                            text = "Create New Trip",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Step ${tripState.currentStep} of ${tripState.totalSteps}",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // Progress Indicator
                ProgressIndicator(
                    currentStep = tripState.currentStep,
                    totalSteps = tripState.totalSteps,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Step Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (tripState.currentStep) {
                        1 -> DestinationSelectionStep(
                            state = tripState,
                            onStateChange = { tripState = it }
                        )
                        2 -> DateSelectionStep(
                            state = tripState,
                            onStateChange = { tripState = it }
                        )
                        3 -> CompanionsStep(
                            state = tripState,
                            onStateChange = { tripState = it }
                        )
                        4 -> TripSettingsStep(
                            state = tripState,
                            onStateChange = { tripState = it }
                        )
                        5 -> ReviewStep(
                            state = tripState
                        )
                    }
                }
                
                // Navigation Buttons
                StepNavigationButtons(
                    currentStep = tripState.currentStep,
                    totalSteps = tripState.totalSteps,
                    canProceed = tripState.canProceedToNextStep() && !isLoading,
                    onPreviousClick = {
                        if (tripState.currentStep > 1 && !isLoading) {
                            tripState = tripState.copy(currentStep = tripState.currentStep - 1)
                        }
                    },
                    onNextClick = {
                        if (tripState.currentStep < tripState.totalSteps && tripState.canProceedToNextStep() && !isLoading) {
                            tripState = tripState.copy(currentStep = tripState.currentStep + 1)
                        }
                    },
                    onFinishClick = {
                        if (!isLoading) {
                            // Create the trip using the ViewModel
                            tripViewModel.createTrip(tripState)
                            onTripCreated()
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Loading indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

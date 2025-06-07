package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.reservation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.managers.ReservationSessionManager
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.providers.DummyTripDataProvider
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.components.StepProgressIndicator

/**
 * Multi-step reservation flow activity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationFlowActivity(
    tripId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPayment: (String) -> Unit
) {
    val sessionManager = remember { ReservationSessionManager.getInstance() }
    val trip = remember { DummyTripDataProvider.getTripById(tripId) }
    var currentStep by remember { mutableStateOf(0) }
    
    val session by sessionManager.currentSession.collectAsState()
    
    // Initialize session when component loads
    LaunchedEffect(tripId) {
        sessionManager.startNewSession(tripId)
    }
    
    if (trip == null) {
        // Handle trip not found
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Trip not found")
            Button(onClick = onNavigateBack) {
                Text("Go Back")
            }
        }
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Book ${trip.title}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Step Progress Indicator
            StepProgressIndicator(
                currentStep = currentStep,
                totalSteps = 4,
                stepLabels = listOf("Transport", "Options", "Hotel", "Summary")
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Step Content
            when (currentStep) {
                0 -> TransportSelectionStep(
                    onTransportSelected = { transport ->
                        sessionManager.selectTransport(transport)
                        currentStep = 1
                    }
                )
                1 -> TransportOptionsStep(
                    selectedTransportType = session?.selectedTransport?.type,
                    onOptionSelected = { transport ->
                        sessionManager.selectTransport(transport)
                        currentStep = 2
                    },
                    onBack = { currentStep = 0 }
                )
                2 -> HotelSelectionStep(
                    onHotelSelected = { hotel, nights ->
                        sessionManager.selectHotel(hotel, nights)
                        currentStep = 3
                    },
                    onSkip = { currentStep = 3 },
                    onBack = { currentStep = 1 }
                )
                3 -> SummaryStep(
                    session = session,
                    trip = trip,
                    onAddActivity = { activity ->
                        sessionManager.addActivity(activity)
                    },
                    onRemoveActivity = { activity ->
                        sessionManager.removeActivity(activity)
                    },
                    onProceedToPayment = {
                        val reservation = sessionManager.completeReservation()
                        reservation?.let { 
                            onNavigateToPayment(it.id)
                        }
                    },
                    onBack = { currentStep = 2 }
                )
            }
        }
    }
}

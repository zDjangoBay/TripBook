package com.android.tripbook.ui.screens.travel

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.model.TravelLocation
import com.android.tripbook.ui.components.travel.AvailabilityCalendar
import com.android.tripbook.ui.components.travel.BookingButton
import com.android.tripbook.ui.components.travel.PriceIndicator
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripBookingScreen(location: TravelLocation, onBookingConfirmed: (TravelLocation, LocalDate, LocalDate) -> Unit, onBack: () -> Unit) {
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    val price = remember(startDate, endDate) {
        if (startDate != null && endDate != null && (endDate!!.isAfter(startDate) || endDate == startDate)) {
            TravelPriceCalculator.calculatePrice(startDate!!, endDate!!, 50.0)
        } else {
            0.0
        }
    }
    val availableDates = remember {
        listOf(
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2),
            LocalDate.now().plusDays(7),
            LocalDate.now().plusDays(8),
            LocalDate.now().plusDays(15),
            LocalDate.now().plusDays(16)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Réserver pour ${location.name}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(text = "Détails du lieu: ${location.description}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Sélectionnez vos dates:", style = MaterialTheme.typography.titleMedium)
            AvailabilityCalendar(
                availableDates = availableDates,
                selectedStartDate = startDate,
                selectedEndDate = endDate,
                onDateSelected = { date ->
                    if (startDate == null || (startDate != null && endDate != null)) {
                        startDate = date
                        endDate = null
                    } else if (date.isBefore(startDate)) {
                        startDate = date
                        endDate = null
                    }
                    else if (date == startDate) {
                        endDate = null
                    }
                    else {
                        endDate = date
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (startDate != null) {
                Text("Date de début: ${startDate.toString()}")
            }
            if (endDate != null) {
                Text("Date de fin: ${endDate.toString()}")
            }
            Spacer(modifier = Modifier.height(16.dp))

            PriceIndicator(price = price)
            Spacer(modifier = Modifier.height(16.dp))

            BookingButton(
                onClick = {
                    if (startDate != null && endDate != null) {
                        onBookingConfirmed(location, startDate!!, endDate!!)
                    }
                },
                enabled = startDate != null && endDate != null && (endDate!!.isAfter(startDate) || endDate == startDate)
            )
        }
    }
}

class TravelPriceCalculator {
    companion object {
        fun calculatePrice(startDate: LocalDate, endDate: LocalDate, d: Double) {

        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewTripBookingScreen() {
    TripBookTheme {
        val sampleLocation = TravelLocation("1", "Paris", 48.85, 2.35, "La ville lumière", null)
        TripBookingScreen(
            location = sampleLocation,
            onBookingConfirmed = { loc, start, end ->
                println("Booking confirmed for ${loc.name} from $start to $end")
            },
            onBack = {}
        )
    }
}
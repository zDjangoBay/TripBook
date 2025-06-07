package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.model.Reservation
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.theme.TextPrimary
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.theme.TextSecondary

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Agenda view for a specific date
 */
@Composable
fun AgendaView(
    selectedDate: LocalDate,
    reservations: List<Reservation>,
    onReservationClick: (Reservation) -> Unit,
    onAddReservationClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Date header
        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = TextPrimary
        )

        Divider()

        if (reservations.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextSecondary.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No reservations for this date",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onAddReservationClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Add Reservation")
                    }
                }
            }
        } else {
            // Reservations list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(reservations) { reservation ->
                    ReservationCard(
                        reservation = reservation,
                        onClick = onReservationClick
                    )
                }
            }
        }
    }
}

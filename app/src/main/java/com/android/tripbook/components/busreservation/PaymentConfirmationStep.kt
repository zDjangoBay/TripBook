package com.android.tripbook.components.busreservation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
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
import com.android.tripbook.components.model.BusReservation
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Composable
fun PaymentConfirmationStep(
    reservation: BusReservation,
    isProcessing: Boolean,
    isConfirmed: Boolean,
    onConfirmPayment: () -> Unit,
    onBack: () -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf("card") }
    var passengerName by remember { mutableStateOf("") }
    var passengerEmail by remember { mutableStateOf("") }
    var passengerPhone by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var cardholderName by remember { mutableStateOf("") }
    
    if (isConfirmed) {
        ConfirmationScreen(
            reservation = reservation,
            ticketId = "BG${Random.nextInt(1000, 9999)}"
        )
    } else {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Payment & Confirmation",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Complete your booking by making payment",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Trip Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${formatCityName(reservation.from)} to ${formatCityName(reservation.to)}",
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                Icons.Default.DirectionsBus,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${reservation.date?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))} • ${reservation.time?.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Seat ${reservation.selectedSeat}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${reservation.passengers} Passenger${if (reservation.passengers > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                // Passenger Details
                Text(
                    text = "Passenger Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                OutlinedTextField(
                    value = passengerName,
                    onValueChange = { passengerName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = passengerEmail,
                    onValueChange = { passengerEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = passengerPhone,
                    onValueChange = { passengerPhone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Payment Method
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = { selectedPaymentMethod = "card" },
                        label = { Text("Credit Card") },
                        selected = selectedPaymentMethod == "card",
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        onClick = { selectedPaymentMethod = "paypal" },
                        label = { Text("PayPal") },
                        selected = selectedPaymentMethod == "paypal",
                        modifier = Modifier.weight(1f)
                    )
                }
                
                if (selectedPaymentMethod == "card") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text("Card Number") },
                            placeholder = { Text("1234 5678 9012 3456") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { expiryDate = it },
                                label = { Text("MM/YY") },
                                placeholder = { Text("12/25") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cvc,
                                onValueChange = { cvc = it },
                                label = { Text("CVC") },
                                placeholder = { Text("123") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        OutlinedTextField(
                            value = cardholderName,
                            onValueChange = { cardholderName = it },
                            label = { Text("Cardholder Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "You'll be redirected to PayPal to complete payment",
                                textAlign = TextAlign.Center
                            )
                            OutlinedButton(
                                onClick = { /* Handle PayPal */ },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Continue with PayPal")
                            }
                        }
                    }
                }
                
                // Price Breakdown
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ticket Price", style = MaterialTheme.typography.bodyMedium)
                            Text("$45.00", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Service Fee", style = MaterialTheme.typography.bodyMedium)
                            Text("$2.50", style = MaterialTheme.typography.bodyMedium)
                        }
                        Divider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text
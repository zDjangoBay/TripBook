package com.android.tripbook.ui.uis.tripcreation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.Agency
import com.android.tripbook.model.Bus
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.ui.components.StepHeader
import com.android.tripbook.viewmodel.AgencyViewModel
import java.time.format.DateTimeFormatter

@Composable
fun AgencySelectionStep(
    state: TripCreationState,
    onStateChange: (TripCreationState) -> Unit,
    agencyViewModel: AgencyViewModel,
    modifier: Modifier = Modifier
) {
    val filteredAgencies by agencyViewModel.filteredAgencies.collectAsState()
    val busesByAgency by agencyViewModel.busesByAgency.collectAsState()
    val isLoading by agencyViewModel.isLoading.collectAsState()
    val error by agencyViewModel.error.collectAsState()

    LaunchedEffect(state.destination) {
        if (state.destination.isNotBlank()) {
            agencyViewModel.loadAgenciesForDestination(state.destination)
            agencyViewModel.loadBusesForDestination(state.destination)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        StepHeader(
            title = "Travel Agency",
            subtitle = "Choose a travel agency for ${state.destination} (Optional)",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Info section
                InfoCard()

                // Skip agency option
                SkipAgencyCard(
                    isSelected = state.selectedAgency == null && state.selectedBus == null,
                    onClick = {
                        onStateChange(state.copy(selectedAgency = null, selectedBus = null))
                    }
                )

                when {
                    isLoading -> LoadingIndicator()
                    error != null -> ErrorCard(error!!, retryAction = {
                        agencyViewModel.loadAgenciesForDestination(state.destination)
                    })
                    filteredAgencies.isEmpty() -> NoAgenciesFound(state.destination)
                    else -> {
                        Text(
                            text = "Available Agencies (${filteredAgencies.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredAgencies) { agency ->
                                AgencyWithBusesCard(
                                    agency = agency,
                                    buses = busesByAgency[agency.agencyId] ?: emptyList(),
                                    selectedBus = state.selectedBus,
                                    onBusSelected = { bus ->
                                        onStateChange(state.copy(selectedAgency = agency, selectedBus = bus))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Helper Composables ---

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = Color(0xFF0EA5E9),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "We found travel agencies that offer trips to your destination. You can select one or skip this step.",
                fontSize = 14.sp,
                color = Color(0xFF0F172A)
            )
        }
    }
}

@Composable
fun SkipAgencyCard(isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) Color(0xFF6366F1) else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .border(2.dp, borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text(
                    text = "Skip Agency Selection",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Continue without selecting a travel agency",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun ErrorCard(message: String, retryAction: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Error loading agencies",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFDC2626)
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFF991B1B),
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = retryAction,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Text("Retry", color = Color.White)
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF6366F1))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Finding travel agencies...", fontSize = 14.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun NoAgenciesFound(destination: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🏢", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("No agencies found", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "No travel agencies offer trips to $destination",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun AgencyWithBusesCard(
    agency: Agency,
    buses: List<Bus>,
    selectedBus: Bus?,
    onBusSelected: (Bus) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(agency.agencyName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    agency.agencyDescription?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "${buses.size} bus${if (buses.size != 1) "es" else ""} available",
                        fontSize = 12.sp,
                        color = Color(0xFF6366F1)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand/Collapse",
                    tint = Color(0xFF6366F1)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                if (buses.isEmpty()) {
                    Text("No buses available for this agency", fontSize = 14.sp, color = Color(0xFF64748B))
                } else {
                    buses.forEach { bus ->
                        BusSelectionCard(
                            bus = bus,
                            isSelected = selectedBus?.busId == bus.busId,
                            onClick = { onBusSelected(bus) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BusSelectionCard(
    bus: Bus,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
    val borderColor = if (isSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF6366F1) else Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(bus.busName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${bus.timeOfDeparture.format(dateFormatter)} at ${bus.timeOfDeparture.format(timeFormatter)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                if (bus.destinationName.isNotEmpty()) {
                    Text("To: ${bus.destinationName}", fontSize = 12.sp, color = Color.Gray)
                }
            }
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
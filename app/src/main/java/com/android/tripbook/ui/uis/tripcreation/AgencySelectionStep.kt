package com.android.tripbook.ui.uis.tripcreation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
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
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.ui.components.StepHeader
import com.android.tripbook.viewmodel.AgencyViewModel

@Composable
fun AgencySelectionStep(
    state: TripCreationState,
    onStateChange: (TripCreationState) -> Unit,
    agencyViewModel: AgencyViewModel,
    modifier: Modifier = Modifier
) {
    val filteredAgencies by agencyViewModel.filteredAgencies.collectAsState()
    val isLoading by agencyViewModel.isLoading.collectAsState()
    val error by agencyViewModel.error.collectAsState()

    // Load agencies for the selected destination when the step is displayed
    LaunchedEffect(state.destination) {
        if (state.destination.isNotBlank()) {
            agencyViewModel.loadAgenciesForDestination(state.destination)
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

                // Skip option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable {
                            onStateChange(state.copy(selectedAgency = null))
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.selectedAgency == null) Color(0xFFEEF2FF) else Color(0xFFF8FAFC)
                    ),
                    border = if (state.selectedAgency == null) {
                        androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF6366F1))
                    } else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.selectedAgency == null) {
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

                // Loading state
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF6366F1),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Finding travel agencies...",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                // Error state
                error?.let { errorMessage ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Error loading agencies",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFDC2626)
                            )
                            Text(
                                text = errorMessage,
                                fontSize = 14.sp,
                                color = Color(0xFF991B1B),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { agencyViewModel.loadAgenciesForDestination(state.destination) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFDC2626)
                                )
                            ) {
                                Text("Retry", color = Color.White)
                            }
                        }
                    }
                }

                // Agencies list
                if (!isLoading && error == null) {
                    if (filteredAgencies.isEmpty()) {
                        // No agencies found
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🏢",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No agencies found",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "No travel agencies offer trips to ${state.destination}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        // Display agencies
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
                                AgencySelectionCard(
                                    agency = agency,
                                    isSelected = state.selectedAgency?.agencyId == agency.agencyId,
                                    onClick = {
                                        onStateChange(state.copy(selectedAgency = agency))
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

@Composable
fun AgencySelectionCard(
    agency: Agency,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEEF2FF) else Color.White
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF6366F1))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = agency.agencyName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
                
                agency.agencyDescription?.let { description ->
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                agency.agencyAddress?.let { address ->
                    Text(
                        text = "📍 $address",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

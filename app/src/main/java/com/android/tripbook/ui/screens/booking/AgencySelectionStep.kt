package com.android.tripbook.ui.screens.booking
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.components.AgencyCard
import com.android.tripbook.viewmodel.BookingViewModel
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.tripbook.model.Agency


@Composable
fun AgencySelectionStep(
    viewModel: BookingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    selectedAgency: Int?,
    agencies: List<Agency>
) {
    // State collection
    val agencies by viewModel.availableAgencies.collectAsStateWithLifecycle()
    val selectedAgency by viewModel.selectedAgency.collectAsStateWithLifecycle()
    var departureTime by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Added vertical scroll
    ) {
        // Compact Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 8.dp) // Reduced padding
        ) {
            Text(
                text = "AGENCY SELECTION",
                style = MaterialTheme.typography.titleMedium.copy( // Smaller text
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Current location - made more compact
        Text(
            text = "Current location: Yaoundé",
            style = MaterialTheme.typography.bodySmall, // Smaller text
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Agency List
        if (agencies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No agencies available", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(agencies) { agency ->
                    AgencyCard(
                        agency = agency,
                        isSelected = selectedAgency?.id == agency.id,
                        onSelect = {
                            viewModel.selectAgency(agency)
                            if (departureTime.isEmpty()) {
                                departureTime = "MORNING"
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp) // Tighter spacing
                    )
                }
            }
        }

        // Horizontal Departure Time Selection
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp) // Reduced padding
                .fillMaxWidth()
        ) {
            Text(
                "Departure time:",
                style = MaterialTheme.typography.labelLarge, // Smaller label
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val options = listOf("MORNING", "AFTERNOON", "EVENING")
                options.forEach { time ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .height(40.dp) // Compact height
                            .clickable { departureTime = time }
                            .border(
                                width = 1.dp,
                                color = if (departureTime == time)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RadioButton(
                            selected = departureTime == time,
                            onClick = { departureTime = time },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.size(20.dp) // Smaller radio button
                        )
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelMedium, // Smaller text
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        // Bottom Buttons - made more compact
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp) // Reduced padding
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("BACK", style = MaterialTheme.typography.labelLarge)
            }

            Button(
                onClick = {
                    viewModel.updateDepartureTime(departureTime)
                    onContinue()
                },
                enabled = selectedAgency != null && departureTime.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("CONTINUE", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
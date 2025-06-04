package com.android.tripbook.ui.screens.booking
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
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
    // Proper state collection with lifecycle awareness
    val agencies by viewModel.availableAgencies.collectAsStateWithLifecycle()
    val selectedAgency by viewModel.selectedAgency.collectAsStateWithLifecycle()
    var departureTime by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        // Header with improved styling
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "AGENCY SELECTION",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(16.dp)
            )
        }

        // Current location with better visual hierarchy
        Surface(
            tonalElevation = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Current location: Yaoundé",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Agency List with empty state handling
        if (agencies.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No agencies available", style = MaterialTheme.typography.bodyLarge)
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
                                departureTime = "MORNING" // Default selection
                            }
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        // Departure Time Selection with improved UX
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Departure time",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val options = listOf("MORNING", "AFTERNOON", "EVENING")
            options.forEach { time ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { departureTime = time }
                ) {
                    RadioButton(
                        selected = departureTime == time,
                        onClick = { departureTime = time },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = time,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Bottom Buttons with better spacing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("BACK")
            }

            Button(
                onClick = {
                    viewModel.updateDepartureTime(departureTime)
                    onContinue()
                },
                enabled = selectedAgency != null && departureTime.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("CONTINUE")
            }
        }
    }
}

//private fun LazyItemScope.AgencyCard(
//    agency: Int,
//    isSelected: Boolean,
//    onSelect: () -> Unit,
//    modifier: Modifier
//) {
//}

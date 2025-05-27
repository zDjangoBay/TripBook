package com.android.tripbook.ui.catalog.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class SortOption(val displayName: String) {
    NONE("None"),
    PRICE_ASC("Price: Low to High"),
    PRICE_DESC("Price: High to Low"),
    RATING_DESC("Rating: High to Low"),
    TITLE_ASC("Title: A to Z")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    locations: List<String>,
    selectedLocation: String,
    onLocationChange: (String) -> Unit,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Search & Filter Trips",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Search by keyword (e.g., beach, hiking, city)") },
            placeholder = { Text("Try 'waterfall' or 'Lomé'") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        var locationExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = locationExpanded,
            onExpandedChange = { locationExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedLocation.ifBlank { "Select a location" },
                onValueChange = { },
                readOnly = true,
                label = { Text("Destination") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = locationExpanded,
                onDismissRequest = { locationExpanded = false }
            ) {
                locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(text = location) },
                        onClick = {
                            onLocationChange(location)
                            locationExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var sortExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = sortExpanded,
            onExpandedChange = { sortExpanded = it }
        ) {
            OutlinedTextField(
                value = sortOption.displayName.ifBlank { "Choose sorting option" },
                onValueChange = { },
                readOnly = true,
                label = { Text("Sort trips by") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = sortExpanded,
                onDismissRequest = { sortExpanded = false }
            ) {
                SortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option.displayName) },
                        onClick = {
                            onSortChange(option)
                            sortExpanded = false
                        }
                    )
                }
            }
        }
    }
}

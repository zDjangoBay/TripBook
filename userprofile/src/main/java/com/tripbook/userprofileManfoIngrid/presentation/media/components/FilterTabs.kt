package com.tripbook.userprofileManfoIngrid.presentation.media.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterTabs(
    currentFilter: String,
    onFilterChange: (String) -> Unit
) {
    val filters = listOf("All", "Images", "Videos")
    TabRow(
        selectedTabIndex = filters.indexOf(currentFilter)
    ) {
        filters.forEach { filter ->
            Tab(
                selected = currentFilter == filter,
                onClick = { onFilterChange(filter) },
                text = { Text(filter) }
            )
        }
    }
}

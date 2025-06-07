
/*
- Company catalogs are displayed on this screen
- it integrates search functionalities and intuitive dynamic UI
 */
package com.android.tripbook.companycatalog.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.companycatalog.model.Company
import com.android.tripbook.companycatalog.model.CompanyRepository
import com.android.tripbook.companycatalog.ui.components.ViewModeToggleButtons

enum class ViewMode {
    LIST, GRID
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyCatalogScreen(
    repository: CompanyRepository,
    onCompanyClick: (String) -> Unit
) {
    val companies by repository.filteredCompanies.collectAsState()
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    
    val categories = remember { repository.getCategories() }
    
    // Update search when query changes
    LaunchedEffect(searchQuery) {
        repository.searchCompanies(searchQuery)
    }
    
    // Update filter when category changes
    LaunchedEffect(selectedCategory) {
        repository.filterByCategory(selectedCategory)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "TripBook Company Catalog",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        // Category Filter and View Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Dropdown
            var expanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // View Mode Toggle
            ViewModeToggleButtons(
                currentViewMode = viewMode,
                onViewModeChange = { viewMode = it }
            )
        }
        
        // Companies List/Grid
        when (viewMode) {
            ViewMode.LIST -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(companies) { company ->
                        CompanyListCard(
                            company = company,
                            onClick = { onCompanyClick(company.id) },
                            onLikeClick = { repository.toggleLike(company.id) }
                        )
                    }
                }
            }
            ViewMode.GRID -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(companies) { company ->
                        CompanyCard(
                            company = company,
                            onClick = { onCompanyClick(company.id) },
                            onLikeClick = { repository.toggleLike(company.id) }
                        )
                    }
                }
            }
        }
        
        // Empty state
        if (companies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No companies found",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Try adjusting your search or filters",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


package com.android.tripbook.companycatalog.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.companycatalog.data.Company
import com.android.tripbook.companycatalog.data.MockData
import com.android.tripbook.companycatalog.ui.components.TopBar
import com.android.tripbook.companycatalog.ui.components.ViewModeToggleButtons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyCatalogScreen(
    onCompanyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    
    val companies = remember { MockData.companies }
    val categories = remember {
        listOf("All") + companies.map { it.category }.distinct()
    }
    
    val filteredCompanies = remember(searchQuery, selectedCategory) {
        companies.filter { company ->
            val matchesSearch = searchQuery.isEmpty() || 
                company.name.contains(searchQuery, ignoreCase = true) ||
                company.description.contains(searchQuery, ignoreCase = true) ||
                company.location.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = selectedCategory == "All" || company.category == selectedCategory
            
            matchesSearch && matchesCategory
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        TopBar(title = "Travel Companies")
        
        // Search and Filter Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search companies, locations...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Filter
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Category: $selectedCategory",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // View Mode Toggle
                    ViewModeToggleButtons(
                        isGridView = isGridView,
                        onViewModeChange = { isGridView = it }
                    )
                }
                
                // Category Chips
                if (showFilters || selectedCategory != "All") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.take(3).forEach { category ->
                            FilterChip(
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                selected = selectedCategory == category
                            )
                        }
                        if (categories.size > 3) {
                            TextButton(
                                onClick = { showFilters = !showFilters }
                            ) {
                                Text(if (showFilters) "Less" else "More")
                            }
                        }
                    }
                    
                    if (showFilters) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.drop(3).forEach { category ->
                                FilterChip(
                                    onClick = { selectedCategory = category },
                                    label = { Text(category) },
                                    selected = selectedCategory == category
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Results Count
        Text(
            text = "${filteredCompanies.size} companies found",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // Companies List/Grid
        if (filteredCompanies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
        } else {
            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCompanies) { company ->
                        CompanyGridCard(
                            company = company,
                            onClick = { onCompanyClick(company.id) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredCompanies) { company ->
                        CompanyListCard(
                            company = company,
                            onClick = { onCompanyClick(company.id) },
                            onFavoriteClick = { /* Handle favorite */ },
                            onShareClick = { /* Handle share */ },
                            onBookmarkClick = { /* Handle bookmark */ },
                            onReportClick = { /* Handle report */ },
                            onFollowClick = { /* Handle follow */ }
                        )
                    }
                }
            }
        }
    }
}

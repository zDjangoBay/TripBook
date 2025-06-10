
package com.android.tripbook.Abdoukarimuakande.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.Abdoukarimuakande.data.Company
import com.android.tripbook.Abdoukarimuakande.data.MockData
import com.android.tripbook.Abdoukarimuakande.ui.components.SearchBar
import com.android.tripbook.Abdoukarimuakande.ui.components.TopBar
import com.android.tripbook.Abdoukarimuakande.ui.components.ViewModeToggleButtons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyCatalogScreen(
    onCompanyClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(true) }
    
    val filteredCompanies = remember(searchQuery, selectedCategory) {
        MockData.companies.filter { company ->
            val matchesSearch = company.name.contains(searchQuery, ignoreCase = true) ||
                    company.description.contains(searchQuery, ignoreCase = true) ||
                    company.location.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || company.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }
    
    val categories = remember {
        listOf("All") + MockData.companies.map { it.category }.distinct()
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            title = "Abdoukarimuakande Company Catalog",
            onBackClick = onBackClick
        )
        
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search companies, locations..."
        )
        
        ViewModeToggleButtons(
            isGridView = isGridView,
            onViewModeChange = { isGridView = it }
        )
        
        // Category filter
        LazyColumn {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            selected = selectedCategory == category
                        )
                    }
                }
            }
        }
        
        // Companies list/grid
        if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCompanies) { company ->
                    CompanyListCard(
                        company = company,
                        onClick = { onCompanyClick(company.id) }
                    )
                }
            }
        }
    }
}

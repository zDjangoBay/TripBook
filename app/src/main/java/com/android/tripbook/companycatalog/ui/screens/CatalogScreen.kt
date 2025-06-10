package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.companycatalog.data.MockCompanyData
import com.android.tripbook.companycatalog.ui.components.CompanyCard
import com.android.tripbook.companycatalog.ui.components.GridCompanyCard
import com.android.tripbook.companycatalog.ui.components.SearchHistoryList
import com.android.tripbook.companycatalog.viewmodel.CatalogViewModel
import com.android.tripbook.ui.theme.TripBookTheme
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavHostController,
    viewModel: CatalogViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(false) }
    var showSearchHistory by remember { mutableStateOf(false) }
    var isSearchPerformed by remember { mutableStateOf(false) }

    val allCompanies = MockCompanyData.companies
    val searchHistory by viewModel.searchHistory.collectAsState(initial = emptyList())

    val companiesToDisplay = allCompanies.filter { company ->
        (selectedFilter == "All" || company.servicesOffered.any {
            it.contains(selectedFilter, ignoreCase = true)
        }) && (company.name.contains(searchQuery, ignoreCase = true)
                || company.description.contains(searchQuery, ignoreCase = true))
    }

    fun performSearch() {
        if (searchQuery.isNotEmpty()) {
            viewModel.addSearchQuery(searchQuery, companiesToDisplay.size)
            isSearchPerformed = true
            showSearchHistory = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3EDF7))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Company Catalog",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            actions = {
                IconButton(onClick = { isGridView = !isGridView }) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = if (isGridView) "Switch to List View" else "Switch to Grid View"
                    )
                }
                IconButton(onClick = { /* TODO: Navigate to saved companies */ }) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Saved Companies"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                showSearchHistory = it.isEmpty()
                isSearchPerformed = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search companies...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        showSearchHistory = true
                        isSearchPerformed = false
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { performSearch() }
            )
        )

        // Quick access to company features
        if (!showSearchHistory && searchQuery.isEmpty()) {
            CompanyFeaturesQuickAccess(navController)
        }

        if (showSearchHistory) {
            SearchHistoryList(
                searchHistory = searchHistory,
                onSearchQueryClick = { query ->
                    searchQuery = query
                    showSearchHistory = false
                    performSearch()
                },
                onRemoveQuery = { query ->
                    viewModel.removeSearchQuery(query)
                },
                onClearHistory = {
                    viewModel.clearSearchHistory()
                }
            )
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "All", "Top Rated", "Safari Tours", "Beach Holidays",
                    "City Tours", "Adventure Sports", "Eco-Tourism"
                )
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (companiesToDisplay.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No companies found matching your criteria.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(companiesToDisplay) { company ->
                            GridCompanyCard(company = company) {
                                navController.navigate("companyDetail/${company.id}")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(companiesToDisplay) { company ->
                            CompanyCard(company = company) {
                                navController.navigate("companyDetail/${company.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompanyFeaturesQuickAccess(navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "✨ Explore Company Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Try our new interactive company profile features",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val features = listOf(
                    Triple("Reviews", "⭐", "companyDetail/mock_001?tab=2"),
                    Triple("Services", "🛠️", "companyDetail/mock_001?tab=1"),
                    Triple("Contact", "📞", "companyDetail/mock_001?tab=3"),
                    Triple("Analytics", "📊", "companyDetail/mock_001?tab=4"),
                    Triple("Portfolio", "🎨", "companyDetail/mock_001?tab=5"),
                    Triple("Compare", "⚖️", "companyDetail/mock_001?tab=6")
                )

                items(features) { (title, icon, route) ->
                    AssistChip(
                        onClick = { navController.navigate(route) },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(icon)
                                Text(title)
                            }
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyListScreenPreview() {
    TripBookTheme {
        CatalogScreen(rememberNavController())
    }
}

/*
- Company catalogs are displayed on this screen
- it integrates search functionalities and intuitive dynamic UI
 */
package com.android.tripbook.companycatalog.ui.catalog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.android.tripbook.companycatalog.model.CompanyRepository
import com.android.tripbook.companycatalog.model.Company
import com.android.tripbook.companycatalog.ui.components.ViewModeToggleButtons
import com.android.tripbook.companycatalog.ui.components.TopBar
import com.android.tripbook.companycatalog.ui.components.EmptyState
// Import EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyCatalogScreen(
    onCompanyClick: (Company) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var isListView by remember { mutableStateOf(false) }

    val allCompanies = CompanyRepository.companies

    val filteredCompanies = allCompanies.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
    }

    Scaffold(
        topBar = { TopBar(title = "Company Catalogs") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            SearchBar(query = query, onQueryChanged = { query = it })

            ViewModeToggleButtons(
                isListView = isListView,
                onToggleView = { newMode -> isListView = newMode }
            )

            // Conditional display based on filteredCompanies
            if (filteredCompanies.isEmpty() && query.isNotBlank()) {
                // Show specific message if search query is active but no results
                EmptyState(message = "No company found for \"$query\"")
            } else if (filteredCompanies.isEmpty()) {
                // Show generic message if no companies at all (e.g., empty repository)
                EmptyState(message = "No companies available.")
            } else {
                // Display companies if there are results
                LazyColumn {
                    items(filteredCompanies, key = { it.id }) { company ->
                        if (isListView) {
                            CompanyListCard(
                                company = company,
                                onClick = { onCompanyClick(company) }
                            )
                        } else {
                            CompanyCard(
                                company = company,
                                onClick = { onCompanyClick(company) }
                            )
                        }
                    }
                }
            }
        }
    }
}

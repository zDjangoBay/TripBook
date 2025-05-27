package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

// Imports from your new files
import com.android.tripbook.R // Import R for drawable resources (for mock data)
import com.android.tripbook.data.SortOrder
import com.android.tripbook.data.TripItem
import com.android.tripbook.ui.components.TripItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(modifier: Modifier = Modifier) {
    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // --- Full Mock Data (NOW 100 ITEMS, same as the last MainActivity version) ---
    val allTripItems = remember {
        mutableStateListOf<TripItem>().apply {
            val cityNames = listOf("Buea", "Douala", "Yaounde", "Limbe", "Kribi", "Garoua", "Sanaga", "Bamenda")
            val imageResources = listOf(R.drawable.buea, R.drawable.douala, R.drawable.yaounde, R.drawable.limbe, R.drawable.kribi, R.drawable.garoua, R.drawable.sanaga, R.drawable.bamenda)
            for (i in 0 until 100) { // Create 100 mock items
                val city = cityNames[i % cityNames.size] // Cycle through city names
                val image = imageResources[i % imageResources.size] // Cycle through images
                add(TripItem(i, image, "$city City ${i / cityNames.size + 1}", "Month ${i % 12 + 1} Day ${i % 28 + 1} - ${i % 28 + 5}"))
            }
        }.shuffled() // Shuffle to make it less predictable
    }

    // --- Pagination States ---
    val paginatedItems = remember { mutableStateListOf<TripItem>() } // Items currently loaded and displayed
    var currentPage by remember { mutableStateOf(0) }
    val pageSize = 10 // Number of items per page
    var isLoadingMore by remember { mutableStateOf(false) }
    var hasMoreData by remember { mutableStateOf(true) } // True if there might be more data to load

    val lazyListState = rememberLazyListState() // State to observe LazyColumn scroll

    // --- Pagination Loading Logic ---
    val loadNextPage: () -> Unit = {
        if (!isLoadingMore && hasMoreData) {
            isLoadingMore = true
            coroutineScope.launch {
                delay(1.seconds) // Simulate network delay for fetching data

                val startIndex = currentPage * pageSize
                val endIndex = min((currentPage + 1) * pageSize, allTripItems.size)

                if (startIndex < allTripItems.size) {
                    val newItems = allTripItems.subList(startIndex, endIndex)
                    paginatedItems.addAll(newItems)
                    currentPage++
                    hasMoreData = paginatedItems.size < allTripItems.size
                } else {
                    hasMoreData = false
                }
                isLoadingMore = false
            }
        }
    }

    // Initial data load when the screen first composes
    LaunchedEffect(Unit) {
        loadNextPage()
    }

    // Observe scroll position to trigger loading more data
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= paginatedItems.size - 5) {
                    loadNextPage()
                }
            }
    }

    // --- Search/Sort States ---
    var searchText by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(SortOrder.NONE) }
    var expanded by remember { mutableStateOf(false) }

    // Derived state for filtered and sorted items from the *paginated* list
    val displayedTripItems by remember {
        derivedStateOf {
            val filteredList = paginatedItems.filter {
                it.cityName.contains(searchText, ignoreCase = true) ||
                        it.dates.contains(searchText, ignoreCase = true)
            }
            when (sortOrder) {
                SortOrder.NONE -> filteredList
                SortOrder.CITY_ASC -> filteredList.sortedBy { it.cityName }
                SortOrder.CITY_DESC -> filteredList.sortedByDescending { it.cityName }
                SortOrder.DATE_ASC -> filteredList.sortedBy { it.dates }
                SortOrder.DATE_DESC -> filteredList.sortedByDescending { it.dates }
            }
        }
    }

    // --- Pull-to-Refresh Logic ---
    val onPullRefresh: () -> Unit = {
        coroutineScope.launch {
            isRefreshing = true
            delay(2.seconds)
            paginatedItems.clear()
            currentPage = 0
            hasMoreData = true
            isLoadingMore = false
            loadNextPage()
            isRefreshing = false
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search trips...") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Sort Dropdown
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Text("Sort by:")
            Spacer(Modifier.width(8.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.width(150.dp)
            ) {
                OutlinedTextField(
                    value = sortOrder.label,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    label = { Text("Option") }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    SortOrder.entries.forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order.label) },
                            onClick = {
                                sortOrder = order
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = onPullRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 8.dp)
            ) {
                if (displayedTripItems.isEmpty() && !isRefreshing && !isLoadingMore) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No trips found matching your criteria.")
                        }
                    }
                } else {
                    items(
                        items = displayedTripItems,
                        key = { it.id }
                    ) { item ->
                        TripItemCard(item = item)
                    }
                }
                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
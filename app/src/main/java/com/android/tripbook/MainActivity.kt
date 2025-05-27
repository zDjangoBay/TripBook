package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds



// Enum for sorting options
enum class SortOrder(val label: String) {
    NONE("None"),
    CITY_ASC("City A-Z"),
    CITY_DESC("City Z-A"),
    DATE_ASC("Date Asc"),
    DATE_DESC("Date Desc")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val refreshState = rememberPullToRefreshState()
            var isRefreshing by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            // --- Full Mock Data (your original list, potentially larger) ---
            val allTripItems = remember {
                mutableStateListOf( // Use mutableStateListOf if you dynamically modify this base list
                    TripItem(0, R.drawable.buea, "Buea", "May 10 - 15"),
                    TripItem(1, R.drawable.douala, "Douala", "March 2 - 7"),
                    TripItem(2, R.drawable.yaounde, "Yaounde", "January 18 - 25"),
                    TripItem(3, R.drawable.limbe, "Limbe", "June 1 - 5"),
                    TripItem(4, R.drawable.kribi, "Kribi", "July 12 - 19"),
                    TripItem(5, R.drawable.garoua, "Garoua", "August 3 - 8"),
                    TripItem(6, R.drawable.sanaga, "Sanaga", "September 1 - 6"),
                    TripItem(7, R.drawable.bamenda, "Bamenda", "October 10 - 15"),
                    // Adding more mock data for pagination demonstration
                    TripItem(8, R.drawable.buea, "Buea City 2", "Nov 1 - 5"),
                    TripItem(9, R.drawable.douala, "Douala City 2", "Dec 1 - 6"),
                    TripItem(10, R.drawable.yaounde, "Yaounde City 2", "Feb 20 - 25"),
                    TripItem(11, R.drawable.limbe, "Limbe City 2", "Apr 1 - 7"),
                    TripItem(12, R.drawable.kribi, "Kribi City 2", "July 1 - 6"),
                    TripItem(13, R.drawable.garoua, "Garoua City 2", "Sept 1 - 7"),
                    TripItem(14, R.drawable.sanaga, "Sanaga City 2", "Oct 1 - 5"),
                    TripItem(15, R.drawable.bamenda, "Bamenda City 2", "Nov 1 - 7"),
                    TripItem(16, R.drawable.buea, "Buea City 3", "Dec 1 - 5"),
                    TripItem(17, R.drawable.douala, "Douala City 3", "Jan 1 - 6"),
                    TripItem(18, R.drawable.yaounde, "Yaounde City 3", "Feb 1 - 7"),
                    TripItem(19, R.drawable.limbe, "Limbe City 3", "Mar 1 - 5"),
                    TripItem(20, R.drawable.kribi, "Kribi City 3", "Apr 1 - 7"),
                    TripItem(21, R.drawable.garoua, "Garoua City 3", "May 1 - 5"),
                    TripItem(22, R.drawable.sanaga, "Sanaga City 3", "June 1 - 7"),
                    TripItem(23, R.drawable.bamenda, "Bamenda City 3", "July 1 - 5"),
                    TripItem(24, R.drawable.buea, "Buea City 4", "Aug 1 - 7"),
                    TripItem(25, R.drawable.douala, "Douala City 4", "Sept 1 - 5"),
                ).shuffled() // Shuffle to make it less predictable
            }

            // --- Pagination States ---
            val paginatedItems = remember { mutableStateListOf<TripItem>() } // Items currently loaded and displayed
            var currentPage by remember { mutableStateOf(0) }
            val pageSize = 10 // Number of items per page
            var isLoadingMore by remember { mutableStateOf(false) }
            var hasMoreData by remember { mutableStateOf(true) } // True if there might be more data to load

            val lazyListState = rememberLazyListState() // State to observe LazyColumn scroll

            // --- Pagination Loading Logic ---
            val loadNextPage: () -> Unit = { // Define as a lambda
                // FIX: Replaced 'return' with an 'if' condition to avoid non-local returns
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
                            hasMoreData = paginatedItems.size < allTripItems.size // Check if all available mock data has been loaded
                        } else {
                            hasMoreData = false // No more data in our mock list
                        }
                        isLoadingMore = false
                    }
                }
            }

            // Initial data load when the screen first composes
            LaunchedEffect(Unit) {
                loadNextPage() // Call the lambda
            }

            // Observe scroll position to trigger loading more data
            LaunchedEffect(lazyListState) {
                snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { lastVisibleIndex ->
                        // Load more when user scrolls near the end of the list (e.g., last 5 items)
                        if (lastVisibleIndex != null && lastVisibleIndex >= paginatedItems.size - 5) {
                            // Call loadNextPage; it will handle its internal isLoadingMore/hasMoreData checks
                            loadNextPage()
                        }
                    }
            }

            // --- Existing Search/Sort States ---
            var searchText by remember { mutableStateOf("") }
            var sortOrder by remember { mutableStateOf(SortOrder.NONE) }
            var expanded by remember { mutableStateOf(false) }

            // Derived state for filtered and sorted items from the *paginated* list
            val displayedTripItems by remember {
                derivedStateOf {
                    // 1. Filter first from the currently paginated items
                    val filteredList = paginatedItems.filter {
                        it.cityName.contains(searchText, ignoreCase = true) ||
                                it.dates.contains(searchText, ignoreCase = true)
                    }

                    // 2. Then sort the filtered list
                    when (sortOrder) {
                        SortOrder.NONE -> filteredList
                        SortOrder.CITY_ASC -> filteredList.sortedBy { it.cityName }
                        SortOrder.CITY_DESC -> filteredList.sortedByDescending { it.cityName }
                        // Note: For robust date sorting, parse `dates` into real Date objects
                        SortOrder.DATE_ASC -> filteredList.sortedBy { it.dates }
                        SortOrder.DATE_DESC -> filteredList.sortedByDescending { it.dates }
                    }
                }
            }

            // --- Pull-to-Refresh Logic (resets pagination) ---
            val onPullRefresh: () -> Unit = { // Define as a lambda
                coroutineScope.launch {
                    isRefreshing = true
                    delay(2.seconds) // Simulate refresh delay
                    paginatedItems.clear() // Clear current items
                    currentPage = 0 // Reset page count
                    hasMoreData = true // Assume more data after refresh
                    isLoadingMore = false // Reset loading state
                    loadNextPage() // Call the lambda to reload first page
                    isRefreshing = false
                }
            }

            TripBookTheme {
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

                Scaffold(
                    modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            title = { Text("My Trips") },
                            actions = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 8.dp)
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
                            },
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Search Bar
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                // OPTIONAL: If you want search to re-trigger pagination from page 0
                                // whenever search text changes, uncomment and adjust:
                                // paginatedItems.clear()
                                // currentPage = 0
                                // hasMoreData = true
                                // isLoadingMore = false
                                // loadNextPage()
                            },
                            label = { Text("Search trips...") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        PullToRefreshBox(
                            state = refreshState,
                            isRefreshing = isRefreshing,
                            onRefresh = onPullRefresh,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                state = lazyListState, // Assign the state to LazyColumn
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 8.dp)
                            ) {
                                if (displayedTripItems.isEmpty() && !isRefreshing && !isLoadingMore) {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillParentMaxSize() // Correctly fill space in LazyColumn
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
                                // Loading indicator at the bottom of the list
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
            }
        }
    }
}

// TripItemCard composable remains the same
@Composable
fun TripItemCard(item: TripItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = item.imageUrl),
                contentDescription = item.cityName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = item.cityName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.dates,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TripBookTheme {
        TripItemCard(
            item = TripItem(
                id = 0,
                imageUrl = R.drawable.buea,
                cityName = "Sample City",
                dates = "Jan 01 - Jan 07"
            )
        )
    }
}
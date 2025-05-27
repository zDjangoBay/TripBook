package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown // Keep this import
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults // Important for trailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton // Keep for potential other actions, but not for dropdown anchor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField // For the visible dropdown field
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

// TripItem data class (assuming it's in the same file or imported)


// Enum for sorting options
enum class SortOrder(val label: String) {
    NONE("None"),
    CITY_ASC("City A-Z"),
    CITY_DESC("City Z-A"),
    DATE_ASC("Date Asc"), // Note: For robust date sorting, parse `dates` into real Date objects
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

            val allTripItems = remember {
                listOf(
                    TripItem(0, R.drawable.buea, "Buea", "May 10 - 15"),
                    TripItem(1, R.drawable.douala, "Douala", "March 2 - 7"),
                    TripItem(2, R.drawable.yaounde, "Yaounde", "January 18 - 25"),
                    TripItem(3, R.drawable.limbe, "Limbe", "June 1 - 5"),
                    TripItem(4, R.drawable.kribi, "Kribi", "July 12 - 19"),
                    TripItem(5, R.drawable.garoua, "Garoua", "August 3 - 8"),
                    TripItem(6, R.drawable.sanaga, "Sanaga", "September 1 - 6"),
                    TripItem(7, R.drawable.bamenda, "Bamenda", "October 10 - 15"),
                )
            }

            var sortOrder by remember { mutableStateOf(SortOrder.NONE) }
            var expanded by remember { mutableStateOf(false) } // For dropdown menu

            val displayedTripItems by remember {
                derivedStateOf {
                    val list = allTripItems
                    when (sortOrder) {
                        SortOrder.NONE -> list
                        SortOrder.CITY_ASC -> list.sortedBy { it.cityName }
                        SortOrder.CITY_DESC -> list.sortedByDescending { it.cityName }
                        // For robust date sorting, consider parsing dates into comparable objects
                        SortOrder.DATE_ASC -> list.sortedBy { it.dates }
                        SortOrder.DATE_DESC -> list.sortedByDescending { it.dates }
                    }
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
                                    // Give some padding to the right for the dropdown
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("Sort by:")
                                    Spacer(Modifier.width(8.dp)) // Use width for horizontal spacing

                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = !expanded },
                                        modifier = Modifier.width(150.dp) // Give it a fixed width or use weight
                                    ) {
                                        OutlinedTextField(
                                            value = sortOrder.label,
                                            onValueChange = {}, // Read-only, no direct typing
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                            modifier = Modifier
                                                .menuAnchor() // Crucial for dropdown to anchor to this TextField
                                                .fillMaxWidth(),
                                            singleLine = true,
                                            textStyle = MaterialTheme.typography.bodyLarge, // Adjust text style
                                            label = { Text("Option") } // A label for the text field
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
                        PullToRefreshBox(
                            state = refreshState,
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                coroutineScope.launch {
                                    isRefreshing = true
                                    delay(5.seconds)
                                    isRefreshing = false
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 8.dp)
                            ) {
                                if (displayedTripItems.isEmpty() && !isRefreshing) {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillParentMaxSize()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text("No trips found.")
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
                            }
                        }
                    }
                }
            }
        }
    }
}

// TripItemCard and Preview remain unchanged

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
package com.android.tripbook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.model.Trip
import com.android.tripbook.model.User
import com.android.tripbook.ui.components.MiniProfileTruncated
import com.android.tripbook.ui.components.TripCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Main Trip Catalog Screen combining:
 * - Search bar (sticky)
 * - Scrollable, paginated trip list
 * -Add a place button
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun TripCatalogScreen(
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit,
    onAddClick: () -> Unit = {}
)
{
    var showAddScreen by remember { mutableStateOf(false) }

    if (showAddScreen) {
        AddPlaceScreen(
            onBack = { showAddScreen = false }
        )
        return
    }

    val context = LocalContext.current
    var allTrips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    var isLoadingTrips by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = showAddScreen) { // Re-launch when returning from AddPlaceScreen
        if (!showAddScreen) {
            isLoadingTrips = true
        try {
                // FIX: Use applicationContext to prevent crashes
                val database = TripBookDatabase.getDatabase(context.applicationContext)
            val tripEntities = withContext(Dispatchers.IO) {
                database.tripDao().getAllTripsOnce()
            }

            val roomTrips = tripEntities.map { entity ->
                Trip(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                        imageUrl = entity.imageUrl,
                    caption = entity.caption
                )
            }

                val sampleTrips = SampleTrips.get().filter { sample -> roomTrips.none { it.title == sample.title } }
                allTrips = (roomTrips + sampleTrips).sortedByDescending { it.id }
                android.util.Log.d("TripCatalog", "✅ Loaded ${allTrips.size} total trips.")

        } catch (e: Exception) {
                android.util.Log.e("TripCatalog", "❌ Error loading trips: ${e.message}", e)
                allTrips = SampleTrips.get() // Fallback to sample data
            } finally {
                isLoadingTrips = false
            }
        }
    }

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val pageSize = 5
    var currentPage by remember { mutableStateOf(1) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val displayedTrips = remember(searchQuery.text, currentPage, allTrips) {
        val filteredList = if (searchQuery.text.isEmpty()) {
            allTrips
        } else {
            allTrips.filter { trip ->
                trip.title.contains(searchQuery.text, ignoreCase = true) ||
                        trip.description.contains(searchQuery.text, ignoreCase = true) ||
                        trip.caption.contains(searchQuery.text, ignoreCase = true)
                }
        }
        filteredList.take(currentPage * pageSize)
    }

    var isLoadingMore by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = modifier.fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    currentPage = 1
                },
                label = { Text("Search by location") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )

            if (isLoadingTrips) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                itemsIndexed(displayedTrips) { index, trip ->
                    val users = listOf(
                        User("alice", "https://randomuser.me/api/portraits/women/1.jpg", "Alice"),
                        User("bob", "https://randomuser.me/api/portraits/men/2.jpg", "Bob"),
                        User("carol", "https://randomuser.me/api/portraits/women/3.jpg", "Carol"),
                        User("dan", "https://randomuser.me/api/portraits/men/4.jpg", "Dan"),
                        User("eve", "https://randomuser.me/api/portraits/women/5.jpg", "Eve")
                    )
                    TripCard(
                        trip = trip,
                        onClick = { onTripClick(trip.id) },
                        miniProfileContent = { MiniProfileTruncated(users = users) }
                    )

                        if (index == displayedTrips.lastIndex && !isLoadingMore && displayedTrips.size < allTrips.size) {
                        LaunchedEffect(index) {
                                isLoadingMore = true
                                delay(1000)
                            currentPage += 1
                                isLoadingMore = false
                        }
                    }
                }

                    if (isLoadingMore) {
                    item {
                        CircularProgressIndicator(
                                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                        )
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                onAddClick()
                showAddScreen = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .graphicsLayer {
                    scaleX = if (isPressed) 0.9f else 1f
                    scaleY = if (isPressed) 0.9f else 1f
                },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add place",
                tint = Color.White
            )
        }
    }
}

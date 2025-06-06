package com.android.tripbook.ui.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import com.android.tripbook.viewmodel.TripViewModel
import com.android.tripbook.database.AppDatabase
import com.android.tripbook.repository.BudgetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(
    tripViewModel: TripViewModel,
    onNavigateToTripDetail: (String) -> Unit,
    onNavigateToCreateTrip: () -> Unit
) {
    val trips by tripViewModel.allTrips.observeAsState(initial = emptyList())
    val currentTrips = trips

    // Add sample data if no trips exist
    LaunchedEffect(currentTrips) {
        if (currentTrips.isEmpty()) {
            // Create sample trips for Cameroonian destinations
            val sampleTrip1 = Trip(
                id = "sample_trip_123",
                name = "Kribi Beach Getaway",
                destination = "Kribi Beach, Littoral Region",
                startDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L, // 7 days from now
                endDate = System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L, // 14 days from now
                budget = 150000, // 150,000 CFA Franc (~$250 USD)
                travelers = 2,
                description = "Relaxing beach vacation at Cameroon's beautiful coast"
            )
            val sampleTrip2 = Trip(
                id = "sample_trip_456",
                name = "Safari Adventure", 
                destination = "Waza National Park, Far North",
                startDate = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L, // 30 days from now
                endDate = System.currentTimeMillis() + 45 * 24 * 60 * 60 * 1000L, // 45 days from now
                budget = 200000, // 200,000 CFA Franc (~$330 USD)
                travelers = 3,
                description = "Wildlife safari in Cameroon's premier national park"
            )
            val sampleTrip3 = Trip(
                id = "sample_trip_789",
                name = "Business Trip",
                destination = "Douala Business District",
                startDate = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L, // 3 days from now
                endDate = System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000L, // 5 days from now
                budget = 75000, // 75,000 CFA Franc (~$125 USD)
                travelers = 1,
                description = "Business meetings in Cameroon's economic capital"
            )
            
            tripViewModel.insert(sampleTrip1)
            tripViewModel.insert(sampleTrip2)
            tripViewModel.insert(sampleTrip3)
            
            // Add comprehensive test data for expense tracker
            addSampleBudgetData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Trips") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateTrip) {
                Icon(Icons.Filled.Add, contentDescription = "Test Budget Tracker")
            }
        }
    ) { paddingValues ->
        if (currentTrips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading sample Cameroonian destinations...")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tip: Click the + button to test Budget Tracker!")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(currentTrips) { trip ->
                    TripListItem(trip = trip, onClick = { onNavigateToTripDetail(trip.id) })
                }
            }
        }
    }
}

// Function to add comprehensive sample budget data for testing
suspend fun addSampleBudgetData() {
    // Note: In a real app, this would be called from a context where we have access to the database
    // For now, this serves as documentation of the test data structure
    
    // This data can be manually added via the UI or database initialization
    // Here's the comprehensive test data structure:
    
    /*
    KRIBI BEACH TRIP (sample_trip_123) - Budget: 150,000 CFA
    Categories & Expenses:
    
    1. Accommodation (Planned: 60,000 CFA)
       - Hotel Ilomba Kribi: 25,000 CFA (2 nights)
       - Beachfront Resort: 35,000 CFA (3 nights)
    
    2. Food & Dining (Planned: 40,000 CFA)
       - Restaurant Le Phare: 8,500 CFA (seafood dinner)
       - Local Market Food: 3,200 CFA (breakfast & lunch)
       - Beach Bar Drinks: 6,800 CFA (cocktails)
       - Grilled Fish at Beach: 4,500 CFA (local specialty)
       - Hotel Breakfast: 7,000 CFA (3 days)
    
    3. Transportation (Planned: 35,000 CFA)
       - Taxi to Kribi: 15,000 CFA (from Douala)
       - Local Transport: 8,000 CFA (motorcycle taxis)
       - Return Transport: 12,000 CFA (bus to Yaoundé)
    
    4. Activities & Entertainment (Planned: 15,000 CFA)
       - Beach Excursion: 7,500 CFA (boat trip)
       - Nightclub Entry: 2,500 CFA (Kribi nightlife)
       - Souvenir Shopping: 5,000 CFA (local crafts)
    
    WAZA SAFARI (sample_trip_456) - Budget: 200,000 CFA
    Categories & Expenses:
    
    1. Park & Tour Fees (Planned: 80,000 CFA)
       - Waza Park Entry: 15,000 CFA (3 people)
       - Safari Guide: 45,000 CFA (5 days)
       - Photography Permit: 5,000 CFA
       - Wildlife Viewing Equipment: 15,000 CFA
    
    2. Accommodation (Planned: 70,000 CFA)
       - Safari Lodge Maroua: 30,000 CFA (2 nights)
       - Camping at Waza: 20,000 CFA (3 nights)
       - Hotel in Garoua: 20,000 CFA (2 nights)
    
    3. Food & Supplies (Planned: 30,000 CFA)
       - Safari Meals: 18,000 CFA (packed lunches)
       - Local Restaurant: 7,500 CFA (traditional northern cuisine)
       - Water & Snacks: 4,500 CFA
    
    4. Transportation (Planned: 20,000 CFA)
       - Flight Yaoundé-Garoua: 85,000 CFA (not in budget - separate)
       - 4WD Vehicle Rental: 12,000 CFA (fuel for safari)
       - Local Transport: 8,000 CFA
    
    DOUALA BUSINESS TRIP (sample_trip_789) - Budget: 75,000 CFA
    Categories & Expenses:
    
    1. Accommodation (Planned: 35,000 CFA)
       - Hotel Akwa Palace: 18,000 CFA (1 night)
       - Business Hotel Bonanjo: 17,000 CFA (1 night)
    
    2. Meals (Planned: 20,000 CFA)
       - Business Lunch: 8,000 CFA (Restaurant La Fourchette)
       - Airport Meal: 4,500 CFA
       - Hotel Breakfast: 7,500 CFA (2 days)
    
    3. Transportation (Planned: 15,000 CFA)
       - Airport Taxi: 6,000 CFA (round trip)
       - Business Meetings Transport: 9,000 CFA
    
    4. Business Expenses (Planned: 5,000 CFA)
       - Meeting Materials: 2,500 CFA
       - Communication: 2,500 CFA (mobile credit)
    */
}

@Composable
fun TripListItem(trip: Trip, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = trip.destination, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val startDate = dateFormat.format(Date(trip.startDate))
            val endDate = dateFormat.format(Date(trip.endDate))
            Text(text = "$startDate - $endDate", style = MaterialTheme.typography.bodySmall)
        }
    }
} 
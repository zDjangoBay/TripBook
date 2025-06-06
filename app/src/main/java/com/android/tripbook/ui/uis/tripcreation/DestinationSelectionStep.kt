package com.android.tripbook.ui.uis.tripcreation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.ui.components.StepHeader

@Composable
fun DestinationSelectionStep(
    state: TripCreationState,
    onStateChange: (TripCreationState) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf(state.destination) }
    var showSuggestions by remember { mutableStateOf(false) }
    
    // Sample African destinations for autocomplete
    val africanDestinations = remember {
        listOf(
           "Douala",
           "Mount Cameroon",
           "Kribi Beach",
           "Yaoundé",
           "Buea",
           "Waza National Park",
           //Add more as needed
        )
    }
    
    val filteredDestinations = remember(searchQuery) {
        if (searchQuery.length >= 2) {
            africanDestinations.filter { 
                it.contains(searchQuery, ignoreCase = true) 
            }.take(5)
        } else {
            emptyList()
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        StepHeader(
            title = "Where to?",
            subtitle = "Choose your African destination",
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Destination",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Search Field
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { newValue ->
                                searchQuery = newValue
                                showSuggestions = newValue.isNotEmpty()
                                onStateChange(state.copy(destination = newValue))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        ) { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search for cities, countries...",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                }
                
                // Suggestions List
                if (showSuggestions && filteredDestinations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
                    ) {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp)
                        ) {
                            items(filteredDestinations) { destination ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchQuery = destination
                                            showSuggestions = false
                                            onStateChange(state.copy(destination = destination))
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = Color(0xFF6B73FF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = destination,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                }
                                if (destination != filteredDestinations.last()) {
                                    Divider(color = Color(0xFFE0E0E0))
                                }
                            }
                        }
                    }
                }
                
                // Popular Destinations
                if (!showSuggestions || filteredDestinations.isEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Popular African Destinations",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    LazyColumn {
                        items(africanDestinations.take(8)) { destination ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        searchQuery = destination
                                        onStateChange(state.copy(destination = destination))
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color(0xFF6B73FF),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = destination,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

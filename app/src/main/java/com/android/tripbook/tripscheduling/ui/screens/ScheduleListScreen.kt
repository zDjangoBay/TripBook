package com.android.tripbook.tripscheduling.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.tripscheduling.data.model.Schedule
import com.android.tripbook.tripscheduling.ui.components.ScheduleCard
import com.android.tripbook.tripscheduling.viewmodel.ScheduleListViewModel
import androidx.compose.foundation.background
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.IconButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
    onNavigateToCreation: () -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    val viewModel: ScheduleListViewModel = viewModel()
    val schedules: List<Schedule> by viewModel.schedules.observeAsState(emptyList())

    // State for bottom navigation
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf(
        Icons.Filled.Home    to "Home",
        Icons.Filled.Search  to "Search",
        Icons.Filled.Bookmark to "Saved",
        Icons.Filled.Person  to "Profile"
    )

    Scaffold(
        topBar = {
            // Make the whole top‐bar area taller
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 17.dp)  // extra vertical padding
            ) {
                // Title row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)                       // take up 1 flex unit
                ) {
                    Icon(
                        imageVector       = Icons.Filled.Event,
                        contentDescription = "Trip icon",
                        tint              = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier          = Modifier.size(32.dp)  // slightly larger
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text  = "Schedule Next Trip",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Search bar row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)                             // take up the remaining space
                ) {
                    TextField(
                        value           = searchQuery,
                        onValueChange   = { searchQuery = it },
                        placeholder     = { Text("Search events…") },
                        singleLine      = true,
                        trailingIcon    = {
                            IconButton(onClick = { /* performSearch(searchQuery) */ }) {
                                Icon(
                                    imageVector      = Icons.Filled.Search,
                                    contentDescription = "Search"
                                )
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor        = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height( 60.dp )            // slightly taller field
                    )
                }
            }
        }

        ,
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreation) {
                Icon(Icons.Default.Add, contentDescription = "Create Schedule")
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, (icon, label) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = (selectedTab == index),
                        onClick = { selectedTab = index }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            if (schedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No schedules yet.\nTap + to create one.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding() + 16.dp,
                        bottom = innerPadding.calculateBottomPadding() + 70.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(schedules, key = { it.id }) { sched ->
                        ScheduleCard(
                            schedule = sched,
                            onClick = { onNavigateToDetails(sched.id) }
                        )
                    }
                }
            }
        }
    }
}

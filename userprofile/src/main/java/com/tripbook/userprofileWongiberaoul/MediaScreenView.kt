package com.tripbook.userprofileWongiberaoul

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView  //This icon represents a Grid layout, commonly used to indicate a Grid view option displayed in a button
import androidx.compose.material.icons.filled.List //This icon represents a list layout, commonly used to indicate a list view option
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tripbook.userprofileWongiberaoul.Itemmodels.MediaItem
import com.tripbook.userprofileWongiberaoul.modelview.ViewModels


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreenView(
    navController: NavController,
    viewModel: ViewModels = viewModel()
) {
    val mediaItems by viewModel.filteredAndSortedMedia.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }


    // New state for toggling views
    var isGridView by remember { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Optional top app bar here
            // TopAppBar(title = { Text("") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFEEE6F3))
        ) {
            // Profile Header
            ProfileHeader()

            // Filter Tabs
            FilterTabs(
                currentFilter = currentFilter,
                onFilterChange = viewModel::setFilter
            )

            // Toggle Row: Grid / List buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { isGridView = true }) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Grid View",
                        tint = if (isGridView) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
                IconButton(onClick = { isGridView = false }) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "List View",
                        tint = if (!isGridView) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }

            // Conditionally show grid or list
            if (isGridView) {
                MediaGrid(
                    mediaItems = mediaItems,
                    onMediaClick = { media ->
                        selectedMedia = media

                    }
                )
            } else {
                MediaList(
                    mediaItems = mediaItems,
                    onMediaClick = { media ->
                        selectedMedia = media

                    }
                )
            }
        }
    }
}

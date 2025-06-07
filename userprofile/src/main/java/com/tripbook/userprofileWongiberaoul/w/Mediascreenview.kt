package com.tripbook.userprofileWongiberaoul.w

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tripbook.userprofileWongiberaoul.w.Itemmodels.MediaItem
import com.tripbook.userprofileWongiberaoul.w.modelview.ViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Mediascreenview(
    navController: NavController,
    viewModel: ViewModel = viewModel()
) {
    val mediaItems by viewModel.filteredAndSortedMedia.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    var showActionDialog by remember { mutableStateOf(false) }
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    // New state for toggling views
    var isGridView by remember { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Optional top app bar here
            // TopAppBar(title = { Text("Mes Médias") })
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
                        showActionDialog = true
                    }
                )
            } else {
                MediaList(
                    mediaItems = mediaItems,
                    onMediaClick = { media ->
                        selectedMedia = media
                        showActionDialog = true
                    }
                )
            }
        }
    }
}

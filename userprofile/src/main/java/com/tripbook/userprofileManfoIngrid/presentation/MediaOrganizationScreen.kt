//package com.tripbook.userprofileManfoIngrid.presentation.media
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.tripbook.userprofileManfoIngrid.presentation.media.components.MediaGrid
//import com.tripbook.userprofileManfoIngrid.presentation.media.components.FilterTabs
//import com.tripbook.userprofileManfoIngrid.presentation.media.components.ProfileHeader
//import com.tripbook.userprofileManfoIngrid.presentation.media.components.dialogs.MediaActionDialog
//import com.tripbook.userprofileManfoIngrid.presentation.media.components.dialogs.EditMediaDialog
//import com.tripbook.userprofileManfoIngrid.presentation.media.components.dialogs.ShareDialog
//import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaItem
//import com.tripbook.userprofileManfoIngrid.presentation.media.viewmodels.MediaViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MediaOrganizationScreen(
//    navController: NavController,
//    viewModel: MediaViewModel = viewModel()
//) {
//    val mediaItems by viewModel.filteredAndSortedMedia.collectAsState()
//    val currentFilter by viewModel.currentFilter.collectAsState()
//
//    var showActionDialog by remember { mutableStateOf(false) }
//    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }
//    var showEditDialog by remember { mutableStateOf(false) }
//    var showShareDialog by remember { mutableStateOf(false) }
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        topBar = {
//            // You can add a top app bar here if needed
//            // TopAppBar(title = { Text("Mes Médias") })
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//                .background(Color(0xFFF8F9FA))
//        ) {
//            // Profile Header
//            ProfileHeader()
//
//            // Filter Tabs
//            FilterTabs(
//                currentFilter = currentFilter,
//                onFilterChange = viewModel::setFilter
//            )
//
//            // Media Grid
//            MediaGrid(
//                mediaItems = mediaItems,
//                onMediaClick = { media ->
//                    selectedMedia = media
//                    showActionDialog = true
//                }
//            )
//        }
//    }
//
//    // Dialogs
//    if (showActionDialog && selectedMedia != null) {
//        MediaActionDialog(
//            media = selectedMedia!!,
//            onDismiss = {
//                showActionDialog = false
//                selectedMedia = null
//            },
//            onDelete = { media ->
//                viewModel.deleteMedia(media.id)
//                showActionDialog = false
//                selectedMedia = null
//            },
//            onEdit = {
//                showActionDialog = false
//                showEditDialog = true
//            },
//            onShare = {
//                showActionDialog = false
//                showShareDialog = true
//            }
//        )
//    }
//
//    if (showEditDialog && selectedMedia != null) {
//        EditMediaDialog(
//            media = selectedMedia!!,
//            onDismiss = {
//                showEditDialog = false
//                selectedMedia = null
//            },
//            onSave = { newName ->
//                viewModel.updateMediaName(selectedMedia!!.id, newName)
//                showEditDialog = false
//                selectedMedia = null
//            }
//        )
//    }
//
//    if (showShareDialog && selectedMedia != null) {
//        ShareDialog(
//            media = selectedMedia!!,
//            onDismiss = {
//                showShareDialog = false
//                selectedMedia = null
//            }
//        )
//    }
//}
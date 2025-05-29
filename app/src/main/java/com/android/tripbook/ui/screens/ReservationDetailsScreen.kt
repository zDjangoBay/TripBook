package com.android.tripbook.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.android.tripbook.R
import com.android.tripbook.model.Reservation
import com.android.tripbook.model.ReservationStatus
import com.android.tripbook.repository.ReservationRepository
import com.android.tripbook.ui.animation.AnimationUtils
import com.android.tripbook.ui.components.StatusIndicator
import com.android.tripbook.ui.theme.*
import com.android.tripbook.util.DateUtils
import java.time.format.DateTimeFormatter
import android.content.Intent
import androidx.compose.ui.platform.LocalUriHandler

/**
 * Screen showing detailed information about a reservation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailsScreen(
    reservationId: String,
    repository: ReservationRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    
    // Get the reservation from the repository
    val allReservations by repository.reservations.collectAsState()
    val reservation = remember(allReservations, reservationId) {
        allReservations.find { it.id == reservationId }
    }
    
    // State for edit mode
    var isEditMode by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showShareOptions by remember { mutableStateOf(false) }
    
    // Animated visibility for the screen
    AnimatedVisibility(
        visible = reservation != null,
        enter = fadeIn(animationSpec = tween(300)) + 
                slideInVertically(animationSpec = tween(300)) { it / 2 },
        exit = fadeOut(animationSpec = tween(300)) + 
               slideOutVertically(animationSpec = tween(300)) { -it / 2 }
    ) {
        reservation?.let { res ->
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Reservation Details",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        actions = {
                            // Share button
                            IconButton(onClick = { showShareOptions = true }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = TripBookPrimary
                                )
                            }
                            
                            // Edit button
                            IconButton(onClick = { isEditMode = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = TripBookPrimary
                                )
                            }
                            
                            // Delete button
                            IconButton(onClick = { showDeleteConfirmation = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = TripBookError
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            ) { paddingValues ->
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header with destination image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        // Background image
                        Image(
                            painter = painterResource(
                                id = when (res.destination.lowercase()) {
                                    "zanzibar, tanzania", "serengeti national park, tanzania" -> R.drawable.travel_illustration_1
                                    "cape town, south africa", "cairo, egypt" -> R.drawable.travel_illustration_2
                                    "atlas mountains, morocco", "sahara desert, morocco" -> R.drawable.travel_illustration_3
                                    else -> R.drawable.travel_illustration_4
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )
                        
                        // Destination and status
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = res.destination,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusIndicator(status = res.status)
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = res.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                    
                    // Details section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Date and time
                        DetailSection(
                            title = "Date & Time",
                            icon = Icons.Default.DateRange
                        ) {
                            Text(
                                text = DateUtils.formatDateRange(res.startDate, res.endDate),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "Check-in: ${DateUtils.formatTime(res.startDate)} • Check-out: ${DateUtils.formatTime(res.endDate)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        
                        // Accommodation
                        if (res.accommodationName != null) {
                            DetailSection(
                                title = "Accommodation",
                                icon = Icons.Default.Hotel
                            ) {
                                Text(
                                    text = res.accommodationName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary
                                )
                                
                                if (res.accommodationAddress != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = res.accommodationAddress,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                        }
                        
                        // Transport
                        if (res.transportInfo != null) {
                            DetailSection(
                                title = "Transport",
                                icon = Icons.Default.FlightTakeoff
                            ) {
                                Text(
                                    text = res.transportInfo,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary
                                )
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                        }
                        
                        // Price
                        DetailSection(
                            title = "Price",
                            icon = Icons.Default.AttachMoney
                        ) {
                            Text(
                                text = "${res.price} ${res.currency}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        
                        // Booking reference
                        DetailSection(
                            title = "Booking Reference",
                            icon = Icons.Default.ConfirmationNumber
                        ) {
                            Text(
                                text = res.bookingReference,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                        }
                        
                        // Notes
                        if (res.notes != null) {
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            DetailSection(
                                title = "Notes",
                                icon = Icons.Default.Notes
                            ) {
                                Text(
                                    text = res.notes,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
            
            // Delete confirmation dialog
            if (showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text("Delete Reservation") },
                    text = { Text("Are you sure you want to delete this reservation?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                repository.deleteReservation(res.id)
                                showDeleteConfirmation = false
                                onNavigateBack()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TripBookError
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmation = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            // Share options dialog
            if (showShareOptions) {
                ShareOptionsDialog(
                    reservation = res,
                    onDismiss = { showShareOptions = false },
                    onShare = { shareType ->
                        val shareText = when (shareType) {
                            ShareType.TEXT -> buildShareText(res)
                            ShareType.CALENDAR -> buildCalendarText(res)
                        }
                        
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        
                        context.startActivity(Intent.createChooser(sendIntent, "Share Reservation"))
                        showShareOptions = false
                    }
                )
            }
            
            // Edit mode dialog
            if (isEditMode) {
                EditReservationDialog(
                    reservation = res,
                    onDismiss = { isEditMode = false },
                    onSave = { updatedReservation ->
                        repository.updateReservation(updatedReservation)
                        isEditMode = false
                    }
                )
            }
        } ?: run {
            // Reservation not found
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Reservation not found",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

/**
 * Section for displaying a detail with an icon and title
 */
@Composable
private fun DetailSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TripBookPrimary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(modifier = Modifier.padding(start = 32.dp)) {
            content()
        }
    }
}

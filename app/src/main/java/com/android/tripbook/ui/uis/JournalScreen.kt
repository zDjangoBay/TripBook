package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.JournalEntry
import com.android.tripbook.model.Mood
import com.android.tripbook.model.Privacy
import com.android.tripbook.model.Trip
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    trip: Trip,
    onBackClick: () -> Unit,
    onJournalUpdated: (List<JournalEntry>) -> Unit
) {
    var journalEntries by remember { mutableStateOf(trip.journalEntries) }
    var showAddEntryDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    var filteredEntries by remember(journalEntries, searchQuery) {
        mutableStateOf(
            if (searchQuery.isEmpty()) journalEntries
            else journalEntries.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Travel Journal: ${trip.name}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            // Main content card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search journal entries...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF667EEA)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667EEA),
                            focusedLabelColor = Color(0xFF667EEA),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Journal entries list
                    if (filteredEntries.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty()) 
                                    "No journal entries yet. Add your first entry!" 
                                else 
                                    "No entries match your search.",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredEntries) { entry ->
                                JournalEntryCard(
                                    entry = entry,
                                    onShareClick = { 
                                        val shareIntent = createShareIntent(entry)
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Journal Entry"))
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add entry button
                    Button(
                        onClick = { showAddEntryDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667EEA)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Entry"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Journal Entry",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Add Entry Dialog
    if (showAddEntryDialog) {
        AddJournalEntryDialog(
            onDismiss = { showAddEntryDialog = false },
            onEntryAdded = { newEntry ->
                val updatedEntries = journalEntries + newEntry
                journalEntries = updatedEntries
                onJournalUpdated(updatedEntries)
                showAddEntryDialog = false
            }
        )
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with date, mood, and privacy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    fontSize = 14.sp,
                    color = Color(0xFF667EEA)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.mood.icon,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = when (entry.privacy) {
                            Privacy.PUBLIC -> Icons.Default.Public
                            Privacy.FRIENDS -> Icons.Default.Group
                            Privacy.PRIVATE -> Icons.Default.Lock
                        },
                        contentDescription = "Privacy: ${entry.privacy}",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = entry.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content
            Text(
                text = entry.content,
                fontSize = 14.sp,
                color = Color(0xFF4A5568),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Photos if available
            if (entry.photoUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entry.photoUrls.take(3).forEach { photoUrl ->
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Journal photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    if (entry.photoUrls.size > 3) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .background(
                                    Color(0xFF667EEA).copy(alpha = 0.7f),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${entry.photoUrls.size - 3}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Tags
            if (entry.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entry.tags.take(3).forEach { tag ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(text = "#$tag", fontSize = 12.sp) }
                        )
                    }
                    if (entry.tags.size > 3) {
                        Text(
                            text = "+${entry.tags.size - 3} more",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            // Share button
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onShareClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF667EEA)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJournalEntryDialog(
    onDismiss: () -> Unit,
    onEntryAdded: (JournalEntry) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(Mood.HAPPY) }
    var selectedPrivacy by remember { mutableStateOf(Privacy.PRIVATE) }
    var tags by remember { mutableStateOf("") }
    var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    val context = LocalContext.current
    
    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            photoUris = photoUris + uris
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Journal Entry") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mood selection
                Text("Mood", fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Mood.values().forEach { mood ->
                        Text(
                            text = mood.icon,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .clickable { selectedMood = mood }
                                .background(
                                    if (selectedMood == mood) Color(0xFFEEF2FF) else Color.Transparent,
                                    CircleShape
                                )
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Privacy selection
                Text("Privacy", fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Privacy.values().forEach { privacy ->
                        FilterChip(
                            selected = selectedPrivacy == privacy,
                            onClick = { selectedPrivacy = privacy },
                            label = { Text(privacy.name) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tags
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Photos
                Text("Photos", fontWeight = FontWeight.Medium)
                
                // Display selected photos
                if (photoUris.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(photoUris.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = photoUris[index],
                                    contentDescription = "Selected photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                
                                // Delete button
                                IconButton(
                                    onClick = {
                                        photoUris = photoUris.filterIndexed { i, _ -> i != index }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Add photo button
                Button(
                    onClick = {
                        photoPickerLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Add Photo"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Photos")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && content.isNotEmpty()) {
                        // Convert Uri to string paths for storage
                        val photoUrlStrings = photoUris.map { it.toString() }
                        
                        val newEntry = JournalEntry(
                            id = UUID.randomUUID().toString(),
                            date = LocalDate.now(),
                            title = title,
                            content = content,
                            mood = selectedMood,
                            privacy = selectedPrivacy,
                            tags = if (tags.isEmpty()) emptyList() else tags.split(",").map { it.trim() },
                            photoUrls = photoUrlStrings
                        )
                        onEntryAdded(newEntry)
                    }
                }
            ) {
                Text("Add Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Function to create share intent
private fun createShareIntent(entry: JournalEntry): Intent {
    return Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "My Travel Journal: ${entry.title}")
        
        val shareText = buildString {
            append("${entry.title}\n\n")
            append("${entry.date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}\n")
            append("Mood: ${entry.mood.icon}\n\n")
            append(entry.content)
            if (entry.tags.isNotEmpty()) {
                append("\n\nTags: ")
                append(entry.tags.joinToString(", ") { "#$it" })
            }
            append("\n\nShared from TripBook App")
        }
        
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
}

package com.android.tripbook.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.viewmodel.MockReviewViewModel
import com.android.tripbook.viewmodel.MockTripViewModel
import com.android.tripbook.viewmodel.MockCommentViewModel
import com.android.tripbook.ui.components.ImageCarousel
import com.android.tripbook.ui.components.ReviewCard
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel

// Shared comment data class - make sure this matches the one in AllReviewsScreen
data class SharedComment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val imageUri: String? = null,
    val timestamp: String = java.text.SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
    val authorName: String = "You",
    val tripId: Int // Add tripId to associate comments with specific trips
)

// Global comment storage (in a real app, this would be in a proper state management solution)
object CommentStorage {
    private val _comments = mutableStateListOf<SharedComment>()
    val comments: List<SharedComment> get() = _comments.toList()

    fun addComment(comment: SharedComment) {
        _comments.add(0, comment) // Add to beginning so newest appears first
    }

    fun getCommentsForTrip(tripId: Int): List<SharedComment> {
        return _comments.filter { it.tripId == tripId }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(tripId: Int, onBack: () -> Unit, onSeeAllReviews: (Int) -> Unit) {
    val tripViewModel = remember { MockTripViewModel() }
    val trip = remember { tripViewModel.getTripById(tripId) }
    val reviewViewModel = remember { MockReviewViewModel() }
    val commentViewModel: MockCommentViewModel = viewModel()
    val allReviews by reviewViewModel.reviews.collectAsState()
    val reviewsForTrip = allReviews.filter { it.tripId == tripId }

    if (trip == null) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("Trip Not Found") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Sorry, this trip does not exist.")
            }
        }
        return
    }

    var reviewText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    ImageCarousel(images = trip.imageUrl, modifier = Modifier.fillMaxSize())
                }
            }

            item {
                Text(trip.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(trip.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray, thickness = 1.dp)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("User Reviews", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = { onSeeAllReviews(tripId) }) {
                        Text("See All", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (reviewsForTrip.isEmpty()) {
                    Text("No reviews yet for this trip.", color = Color.Gray)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(reviewsForTrip.take(5)) { review ->
                            ReviewCard(
                                review = review,
                                modifier = Modifier
                                    .width(280.dp)
                                    .padding(vertical = 4.dp),
                                onLikeClicked = {
                                    reviewViewModel.toggleLike(it.tripId, it.username)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray, thickness = 1.dp)
            }

            item {
                Text("Add Your Review", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Your Comment") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 6,
                    singleLine = false,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp, horizontal = 24.dp)
                ) {
                    Text("Upload Images")
                }

                selectedImageUri?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column {
                        Button(
                            onClick = {
                                if (reviewText.text.isNotBlank()) {
                                    // Add to shared comment storage
                                    CommentStorage.addComment(
                                        SharedComment(
                                            text = reviewText.text,
                                            imageUri = selectedImageUri?.toString(),
                                            tripId = tripId
                                        )
                                    )

                                    // Also add to the existing comment view model
                                    commentViewModel.addComment(
                                        tripId = tripId,
                                        content = reviewText.text,
                                        username = "Anonymous",
                                        imageUri = selectedImageUri?.toString()
                                    )

                                    // Clear the form
                                    reviewText = TextFieldValue("")
                                    selectedImageUri = null
                                }
                            },
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 24.dp),
                            enabled = reviewText.text.isNotBlank()
                        ) {
                            Text("Submit")
                        }

                        Text(
                            text = "See more reviews",
                            color = Color.Blue,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable { onSeeAllReviews(tripId) }
                        )
                    }
                }
            }
        }
    }
}
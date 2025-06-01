package com.android.tripbook.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.viewmodel.MockReviewViewModel
import com.android.tripbook.model.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailsScreen(
    reviewId: Int,
    onBack: () -> Unit
) {
    val viewModel = remember { MockReviewViewModel() }
    val reviews by viewModel.reviews.collectAsState()
    val review = reviews.find { it.id == reviewId } ?: return

    val comments = remember { mutableStateListOf<String>() }
    var newComment by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Image carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                items(review.images) { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .width(300.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating and review text
            Text(text = "Rating: ${review.rating}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = review.comment, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Comments section
            Text("Comments", style = MaterialTheme.typography.titleMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(comments) { comment ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.widthIn(min = 100.dp)
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Text(comment)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add comment area
            Text("Add a Comment", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .height(56.dp),
                    singleLine = true
                )
                IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Image, contentDescription = "Pick Image")
                }
            }

            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Button(
                onClick = {
                    if (newComment.isNotBlank()) {
                        comments.add(newComment)
                        newComment = ""
                        selectedImageUri = null
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Text("Submit")
            }
        }
    }
}

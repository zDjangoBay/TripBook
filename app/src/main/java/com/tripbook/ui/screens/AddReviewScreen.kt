package com.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tripbook.models.Review
import java.util.*

@Composable
fun AddReviewScreen(
    tripId: String,
    userId: String,
    onReviewSubmitted: (Review) -> Unit,
    onBackPressed: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Write a Review") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rating Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    IconButton(
                        onClick = { rating = (index + 1).toFloat() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating ${index + 1}",
                            tint = if (index < rating) MaterialTheme.colors.primary
                                  else MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Review Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Content Input
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Your Review") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10
            )

            // Submit Button
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank() && rating > 0) {
                        isSubmitting = true
                        val review = Review(
                            id = UUID.randomUUID().toString(),
                            tripId = tripId,
                            userId = userId,
                            rating = rating,
                            title = title,
                            content = content,
                            createdAt = Date(),
                            updatedAt = Date()
                        )
                        onReviewSubmitted(review)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting && title.isNotBlank() && content.isNotBlank() && rating > 0
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colors.onPrimary
                    )
                } else {
                    Text("Submit Review")
                }
            }
        }
    }
} 
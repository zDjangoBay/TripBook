// Create this as a NEW file
package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    tripId: Int,
    onReviewAdded: () -> Unit,
    reviewViewModel: ReviewViewModel = viewModel()
) {
    // ... (the complete AddReviewScreen code from the artifact)
    var userName by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(0f) }
    var isSubmitting by remember { mutableStateOf(false) }

    val error by reviewViewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Your Review",
            style = MaterialTheme.typography.headlineMedium
        )

        // User Name Input
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Rating Input
        Text("Rating", style = MaterialTheme.typography.bodyLarge)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) { index ->
                IconButton(
                    onClick = { rating = (index + 1).toFloat() }
                ) {
                    Icon(
                        imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Star ${index + 1}",
                        tint = if (index < rating) Color(0xFFFFD700) else Color.Gray
                    )
                }
            }
            Text(
                text = if (rating > 0) "${rating.toInt()}/5" else "No rating",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Comment Input
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Your Review") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4
        )

        // Error Display
        error?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Submit Button
        Button(
            onClick = {
                if (userName.isNotBlank() && comment.isNotBlank() && rating > 0) {
                    isSubmitting = true
                    reviewViewModel.addReview(
                        tripId = tripId,
                        userName = userName.trim(),
                        comment = comment.trim(),
                        rating = rating
                    )
                    isSubmitting = false
                    onReviewAdded()
                }
            },
            enabled = !isSubmitting && userName.isNotBlank() && comment.isNotBlank() && rating > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Submit Review")
        }
    }
}
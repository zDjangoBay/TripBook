package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.components.ImageCarousel
import com.android.tripbook.ui.components.ReviewCard
import com.android.tripbook.model.SampleReviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(tripId: Int, onBack: () -> Unit) {
    val tripImages = listOf(
        "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
        "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
        "https://source.unsplash.com/800x600/?mountains"
    )

    var reviewText by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
            verticalArrangement = Arrangement.spacedBy(24.dp) // More space between sections
        ) {
            item {
                // Image carousel full width, taller height
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp) // increased height
                ) {
                    ImageCarousel(images = tripImages, modifier = Modifier.fillMaxSize())
                }
            }

            item {
                Text("Trip to Bali", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Experience the tropical paradise with stunning beaches, rich culture, and exotic food. Bali offers an unforgettable experience.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray, thickness = 1.dp)
            }

            item {
                Text("User Reviews", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
                SampleReviews.reviews.forEach { review ->
                    ReviewCard(
                        review = review,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
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
                        .height(140.dp), // taller for longer text
                    maxLines = 6,
                    singleLine = false,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { /* Simulate image picker */ },
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp, horizontal = 24.dp)
                ) {
                    Text("Upload Images")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { /* Submit Review */ },
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 24.dp)
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}

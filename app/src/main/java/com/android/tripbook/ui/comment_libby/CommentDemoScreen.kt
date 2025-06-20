package com.android.tripbook.ui.components.comment_libby

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.ui.theme.TripBookTheme

/**
 * Preview screen to demonstrate the CommentPopup component
 * Created by Libby
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDemoScreen() {
    var showCommentPopup by remember { mutableStateOf(false) }
    var selectedTripId by remember { mutableStateOf("trip_001") }

    TripBookTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Comment Popup Demo",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "This demo shows the CommentPopup component created by Libby",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Current Trip ID: $selectedTripId",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { showCommentPopup = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Show Comments")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = {
                                selectedTripId = when(selectedTripId) {
                                    "trip_001" -> "trip_002"
                                    "trip_002" -> "trip_003"
                                    else -> "trip_001"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change Trip ID")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Features:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val features = listOf(
                            "• Slide-up popup design",
                            "• Material 3 design components",
                            "• Dummy data for demonstration",
                            "• Interactive like buttons",
                            "• Comment input with send functionality",
                            "• Responsive layout",
                            "• Easy to integrate with trip ID"
                        )

                        features.forEach { feature ->
                            Text(
                                text = feature,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Comment Popup
            CommentPopup(
                isVisible = showCommentPopup,
                tripId = selectedTripId,
                onDismiss = { showCommentPopup = false },
                onCommentSubmit = { comment ->
                    // Handle comment submission here
                    println("New comment for trip $selectedTripId: $comment")
                }
            )
        }
    }
}

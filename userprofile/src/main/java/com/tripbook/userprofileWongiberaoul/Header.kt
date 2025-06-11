package com.tripbook.userprofileWongiberaoul

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ProfileHeader() {
    var userName by remember { mutableStateOf("Joe") }
    var userStatus by remember { mutableStateOf("Passionate about travelling and photography") }
    var photoCount by remember { mutableStateOf(6) }
    var videoCount by remember { mutableStateOf(1) }

    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = userName,
            currentStatus = userStatus,
            currentPhotos = photoCount,
            currentVideos = videoCount,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newStatus, newPhotos, newVideos ->
                userName = newName
                userStatus = newStatus
                photoCount = newPhotos
                videoCount = newVideos
                showEditDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                // Top cover background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFFCCC2DC))
                )

                Spacer(modifier = Modifier.height(40.dp)) // To offset profile picture

                // User name and status
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Text(
                        text = userStatus,
                        fontSize = 14.sp,
                        color = Color(0xFF7F8C8D)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Media statistics
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        MediaStat(label = "Photos", count = photoCount)
                        MediaStat(label = "Videos", count = videoCount)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Edit icon (top left)
            IconButton(
                onClick = { showEditDialog = true },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White
                )
            }

            // Settings icon (top right)
            IconButton(
                onClick = { /* Handle settings click */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }

            // Real profile picture
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 50.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://www.sott.net/image/s5/100781/full/pygmies_1.jpg",
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun MediaStat(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF1F2D3D)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF7F8C8D)
        )
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentStatus: String,
    currentPhotos: Int,
    currentVideos: Int,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Int) -> Unit
) {
    // State variables for the dialog inputs
    var name by remember { mutableStateOf(currentName) }
    var status by remember { mutableStateOf(currentStatus) }
    var photos by remember { mutableStateOf(currentPhotos) }
    var videos by remember { mutableStateOf(currentVideos) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                TextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status") })
                TextField(value = photos.toString(), onValueChange = {
                    photos = it.toIntOrNull() ?: 0
                }, label = { Text("Photos") })
                TextField(value = videos.toString(), onValueChange = {
                    videos = it.toIntOrNull() ?: 0
                }, label = { Text("Videos") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(name, status, photos, videos)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
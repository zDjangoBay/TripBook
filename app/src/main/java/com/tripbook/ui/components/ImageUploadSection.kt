package com.tripbook.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ImageUploadSection(
    modifier: Modifier = Modifier,
    onImagesSelected: (List<Uri>) -> Unit,
    maxImages: Int = 5
) {
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showError by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (selectedImages.size + uris.size <= maxImages) {
            selectedImages = selectedImages + uris
            onImagesSelected(selectedImages)
            showError = false
        } else {
            showError = true
        }
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Handle camera image
        }
    }
    
    Column(modifier = modifier) {
        // Image preview section
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedImages) { uri ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .size(120.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Upload buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Select from Gallery")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = { /* Handle camera launch */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Take Photo")
            }
        }
        
        if (showError) {
            Text(
                text = "Maximum $maxImages images allowed",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
} 
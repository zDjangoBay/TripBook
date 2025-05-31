package com.tripbook.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tripbook.ui.components.ImageUploadSection
import com.tripbook.ui.theme.TripBookTheme

class TestImageUploadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestImageUploadScreen()
                }
            }
        }
    }
}

@Composable
fun TestImageUploadScreen() {
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Image Upload Test",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ImageUploadSection(
            modifier = Modifier.fillMaxWidth(),
            onImagesSelected = { uris ->
                selectedImages = uris
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Selected Images: ${selectedImages.size}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
} 
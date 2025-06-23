package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.R
import com.android.tripbook.viewmodel.MockTripViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import android.net.Uri
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.platform.LocalContext
import com.android.tripbook.model.Trip
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.data.EnhancedSampleTrips
import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.database.entity.TripEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import android.content.Context



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    var title by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            selectedImages = uris.take(2)
        }
    )

    val isFormValid by remember {
        derivedStateOf {
            title.isNotBlank() &&
                    description.isNotBlank() &&
                    selectedImages.isNotEmpty()
        }
    }

//    val viewModel: MockTripViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Place") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Place Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Short Caption") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                Column {
                    Text(
                        text = "Add Photos (required)",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Button(
                        onClick = {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Add photos"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Images")
                    }

                    // Show selected images preview
                    if (selectedImages.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedImages) { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Selected image",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Please select at least 1 photo",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(.1f))

                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            // Copy images to internal storage and get their new paths
                            val permanentImagePaths = selectedImages.map { uri ->
                                saveImageToInternalStorage(context, uri)
                            }.filterNotNull()

                            if (permanentImagePaths.isNotEmpty()) {
                                val newTripEntity = TripEntity(
                                    id = 0, // Use 0 to let Room auto-generate the ID
                                    title = title,
                                    caption = caption,
                                    description = description,
                                    imageUrl = permanentImagePaths
                                )
                                val database = TripBookDatabase.getDatabase(context.applicationContext)
                                database.tripDao().insertTrip(newTripEntity)

                                // Navigate back on the main thread after the insert is complete
                                withContext(Dispatchers.Main) {
                                    onBack()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = title.isNotBlank() && description.isNotBlank()
                ) {
                    Text("Save Place")
                }
            }
        }
    )
}

private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "${UUID.randomUUID()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
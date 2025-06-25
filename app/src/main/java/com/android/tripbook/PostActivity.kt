package com.example.tripbook

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.Profilescreen
import com.android.tripbook.ui.theme.Pink40
import com.android.tripbook.ui.theme.Purple40
import com.android.tripbook.ui.theme.TripBookTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*

class PostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            TripBookTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PostScreen()
                }
            }
        }
    }
}

// Convert image URI to Base64 with max 2MB size limit
fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val byteArray = inputStream?.readBytes()
        if (byteArray != null && byteArray.size <= 2 * 1024 * 1024) {
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } else null
    } catch (e: Exception) {
        null
    }
}

// Check if network is available
fun isNetworkAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var caption by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isPosting by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    // Animation: slide X offset between 0 and 50 dp repeatedly
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    // Alpha fades between 0.6 and 1
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    context.startActivity(Intent(context, PostActivity::class.java))
                },
                containerColor = Pink40,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Post", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Purple40),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 64.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        context.startActivity(Intent(context, Profilescreen::class.java))
                    }) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Profile", color = Color.White)
                    }

                    TextButton(onClick = {
                        context.startActivity(Intent(context, FeedActivity::class.java))
                    }) {
                        Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Home", color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TopAppBar(
                title = { Text("Create Post") },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Purple40)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { launcher.launch("image/*") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isPosting) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        if (caption.isBlank() || imageUri == null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please add a caption and select an image.")
                            }
                            return@Button
                        }

                        if (!isNetworkAvailable(context)) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("No internet connection.")
                            }
                            return@Button
                        }

                        isPosting = true
                        val imageBase64 = uriToBase64(context, imageUri!!)
                        if (imageBase64 == null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Image too large (Max 2MB) or failed to load.")
                            }
                            isPosting = false
                            return@Button
                        }

                        val post = hashMapOf(
                            "caption" to caption,
                            "imageBase64" to imageBase64,
                            "timestamp" to Date()
                        )

                        FirebaseFirestore.getInstance().collection("post")
                            .add(post)
                            .addOnSuccessListener {
                                caption = ""
                                imageUri = null
                                isPosting = false
                                context.startActivity(Intent(context, FeedActivity::class.java))
                            }
                            .addOnFailureListener {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Failed to upload post.")
                                }
                                isPosting = false
                            }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Post", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated vehicle icon sliding left-right in the gap
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Moving Vehicle",
                    tint = Purple40.copy(alpha = alphaAnim),
                    modifier = Modifier
                        .offset(x = offsetX.dp)
                        .size(64.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostScreenPreview() {
    TripBookTheme {
        PostScreen()
    }
}

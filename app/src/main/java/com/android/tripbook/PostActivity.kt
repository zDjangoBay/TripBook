package com.example.tripbook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.ui.theme.TripBookTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.android.tripbook.Profilescreen
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.Date



class PostActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            TripBookTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PostScreen()
                }
            }
        }
    }
}

fun uriToBase64(context: android.content.Context, uri: Uri): String? {
    return try {
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen() {
    val context = LocalContext.current

    var caption by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Create Post") },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Image")
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

        Button(
            onClick = {
                if (caption.isNotBlank() && imageUri != null) {
                    val imageBase64 = uriToBase64(context, imageUri!!)
                    if (imageBase64 != null) {
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
                                Toast.makeText(context, "Post uploaded!", Toast.LENGTH_SHORT).show()


                                val intent = Intent(context, FeedActivity::class.java)
                                context.startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to upload post", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Failed to convert image", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please add a caption and select an image", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Post", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

            FloatingActionButton(
                onClick = {
                    context.startActivity(Intent(context, PostActivity::class.java))
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.offset(y = (-28).dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Post")
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
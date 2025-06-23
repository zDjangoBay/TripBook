package com.android.tripbook


import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Added from your project's MainActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.tripbook.ui.theme.TripBookTheme // Your app's theme
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.screens.CreatePostScreen
import com.android.tripbook.posts.screens.PostDetailScreen
import com.android.tripbook.posts.screens.PostListScreen
import com.android.tripbook.posts.viewmodel.PostEvent
import com.android.tripbook.posts.viewmodel.PostViewModel
import com.android.tripbook.posts.viewmodel.PostViewModelFactory
import com.android.tripbook.posts.repository.FakePostRepository
import com.android.tripbook.posts.utils.PostValidator
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Incorporated from your project's MainActivity
        setContent {
            TripBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This MainActivity is now responsible for the "posts" feature UI
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Create the ViewModel instance ONCE here, scoped to the AppNavigation (and thus the NavHost)
    val postViewModel: PostViewModel = viewModel(
        factory = PostViewModelFactory(
            repository = FakePostRepository(), // Replace with your actual repository
            validator = PostValidator()
        )
    )

    NavHost(navController = navController, startDestination = Screen.PostList.route) {
        composable(Screen.PostList.route) {
            PostListScreen(
                viewModel = postViewModel, // Pass the shared instance
                onNavigateToCreatePost = {
                    // Reset form state in the shared ViewModel before navigating to CreatePost
                    postViewModel.handleEvent(PostEvent.ResetForm)
                    navController.navigate(Screen.CreatePost.route)
                },
                onNavigateToPostDetail = { postId ->
                    // Ensure the ViewModel selects the post *before* navigating
                    // This helps if PostDetailScreen relies on selectedPost being immediately available
                    postViewModel.handleEvent(PostEvent.SelectPost(postId))
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                }
            )
        }
        composable(Screen.CreatePost.route) {
            val context = LocalContext.current
            var tempImageUri by remember { mutableStateOf<Uri?>(null) }

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri: Uri? ->
                uri?.let {
                    val imageModel = ImageModel(uri = it.toString(), path = it.path)
                    postViewModel.handleEvent(PostEvent.ImageAdded(imageModel)) // Use shared ViewModel
                }
            }

            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture()
            ) { success: Boolean ->
                if (success) {
                    tempImageUri?.let { uri ->
                        val imageModel = ImageModel(uri = uri.toString(), path = uri.path)
                        postViewModel.handleEvent(PostEvent.ImageAdded(imageModel)) // Use shared ViewModel
                    }
                }
            }

            CreatePostScreen(
                viewModel = postViewModel, // Pass the shared instance
                onNavigateBack = { navController.popBackStack() },
                onNavigateToImagePicker = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                    Log.d("ImagePicker", "Image picker (gallery) triggered.")
                    /* // Camera Implementation Placeholder
                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                    val storageDir: File? = context.getExternalFilesDir("Pictures") // Standard directory
                    try {
                        val tempFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
                        tempImageUri = FileProvider.getUriForFile(
                            Objects.requireNonNull(context.applicationContext),
                            "${context.packageName}.provider", // Ensure this matches your FileProvider authority
                            tempFile
                        )
                        cameraLauncher.launch(tempImageUri)
                    } catch (e: Exception) {
                        Log.e("CameraLauncher", "Error creating temp file for camera", e)
                    }
                    */
                }
            )
        }
        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            if (postId != null) {
                PostDetailScreen(
                    viewModel = postViewModel, // Pass the shared instance
                    postId = postId,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                Log.e("AppNavigation", "PostId is null, navigating back.")
                navController.popBackStack() // Avoid crash if postId is somehow null
            }
        }
    }
}

// Sealed class for Navigation Routes
sealed class Screen(val route: String) {
    object PostList : Screen("postList")
    object CreatePost : Screen("createPost")
    object PostDetail : Screen("postDetail/{postId}") {
        fun createRoute(postId: String) = "postDetail/$postId"
    }
}

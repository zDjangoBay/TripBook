// package com.android.tripbook

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.result.contract.ActivityResultContracts
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Surface
// import androidx.compose.ui.Modifier
// import androidx.lifecycle.viewmodel.compose.viewModel
// import com.android.tripbook.navigation.AppNavigation
// import com.android.tripbook.posts.repository.PostRepository
// import com.android.tripbook.posts.utils.PostValidator
// import com.android.tripbook.posts.viewmodel.PostViewModel
// import com.android.tripbook.posts.viewmodel.PostViewModelFactory
// import com.android.tripbook.ui.theme.TripBookTheme

// class MainActivity : ComponentActivity() {
    
//     private val requestPermissionLauncher = registerForActivityResult(
//         ActivityResultContracts.RequestPermission()
//     ) { isGranted: Boolean ->
//         if (isGranted) {
//             // Permission granted - handle camera/storage access
//         } else {
//             // Permission denied - show explanation
//         }
//     }

//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
        
//         setContent {
//             TripBookTheme {
//                 Surface(
//                     modifier = Modifier.fillMaxSize(),
//                     color = MaterialTheme.colorScheme.background
//                 ) {
//                     val repository = PostRepository()
//                     val validator = PostValidator()
//                     val viewModelFactory = PostViewModelFactory(repository, validator)
//                     val postViewModel: PostViewModel = viewModel(factory = viewModelFactory)
                    
//                     AppNavigation(postViewModel = postViewModel)
//                 }
//             }
//         }
//     }
// }


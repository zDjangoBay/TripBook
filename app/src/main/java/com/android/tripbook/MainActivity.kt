// package com.android.tripbook

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.padding
// import androidx.compose.material3.Scaffold
// import androidx.compose.material3.Text
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.tooling.preview.Preview
// import com.android.tripbook.ui.theme.TripBookTheme

// class MainActivity : ComponentActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         enableEdgeToEdge()
//         setContent {
//             TripBookTheme {
//                 Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                     Greeting(
//                         name = "Android",
//                         modifier = Modifier.padding(innerPadding)
//                     )
//                 }
//             }
//         }
//     }
// }


// @Composable
// fun Greeting(name: String, modifier: Modifier = Modifier) {
//     Text(
//         text = "Hello $name!",
//         modifier = modifier
//     )
// }

// @Preview
// @Composable
// fun GreetingPreview() {
//     TripBookTheme {
//         Greeting("Android")
//     }
// }
// package com.android.tripbook

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.padding
// import androidx.compose.material3.Scaffold
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.tooling.preview.Preview
// import com.android.tripbook.ui.screens.CreatePostScreen
// import com.android.tripbook.ui.theme.TripBookTheme

// class MainActivity : ComponentActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         enableEdgeToEdge()
//         setContent {
//             TripBookTheme {
//                 Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                     CreatePostScreen(modifier = Modifier.padding(innerPadding))
//                 }
//             }
//         }
//     }
// }

// @Preview(showBackground = true)
// @Composable
// fun PreviewCreatePostScreen() {
//     TripBookTheme {
//         CreatePostScreen()
//     }
// }

package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.PostModel
import com.android.tripbook.ui.components.PostCard
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // Dummy post data
    val dummyPost = PostModel(
        title = "Trip to Bali",
        description = "A beautiful and relaxing trip to Bali with friends. Beaches, sunsets, and more!",
        imageUrl = "https://picsum.photos/600/300", // You can use this for testing
        hashtags = listOf("beach", "sunset", "vacation")
    )

    Column(modifier = modifier) {
        PostCard(post = dummyPost, onClick = {
            // Action when the card is clicked (can be left empty for now)
        })
    }
}

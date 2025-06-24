package com.android.tripbook

import FakePostRepository
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.android.tripbook.posts.controller.PostListController
import com.android.tripbook.posts.ui.screen.PostListScreen



@Composable
fun MainScreen() {
    val controller = remember { PostListController(FakePostRepository()) }
    PostListScreen(controller)
}



// This function serves as the main entry point for the app's UI, where we set up the PostListScreen
// with the PostListViewModel. It uses the viewModel() function to obtain an instance of the
// PostListViewModel, which manages the state and logic for displaying a list of posts.
// The PostListScreen is then composed, passing the viewModel as a parameter to connect the UI with
// the underlying data and actions. This structure allows for a clean separation of concerns,
// where the UI is responsible for rendering the data and the ViewModel handles the business logic
// and state management. This approach is consistent with the MVVM (Model-View-ViewModel) architecture
// commonly used in Android development with Jetpack Compose.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.android.tripbook.posts.viewmodel.PostEvent
import com.android.tripbook.posts.viewmodel.PostViewModel

// Assuming R class is correctly imported from your app module
// import com.android.tripbook.R
// Placeholder R class if not resolved by IDE:




object R {
    object drawable {
        const val ic_avatar_placeholder = 0 // Replace with actual if you have one
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    viewModel: PostViewModel, // Accepts the shared ViewModel
    postId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(postId, viewModel) { // Re-select post if postId or ViewModel instance changes
        viewModel.handleEvent(PostEvent.SelectPost(postId))
    }

    val uiState by viewModel.uiState.collectAsState()
    val post = uiState.selectedPost // This will be updated reactively by the ViewModel
    val focusManager = LocalFocusManager.current

    var commentText by remember { mutableStateOf("") }
    var replyingToCommentId by remember { mutableStateOf<String?>(null) }
    var currentReplyText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = post?.title ?: "Post Detail",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (post?.userId == "current_user_simulated") { // Match ViewModel's currentUserId
                        IconButton(onClick = {
                            viewModel.handleEvent(PostEvent.DeletePost(postId))
                            onNavigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Post"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
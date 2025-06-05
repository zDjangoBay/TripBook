package com.android.tripbook.posts


import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.*

@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier
    ) {
        content()
    }
}

//In the post details section, you can use the PullToRefreshContainer like this to make it work:
// Wrap the lazycolumn or lazy list in the PullToRefreshContainer with the code below:
// PullToRefreshContainer(
//     isRefreshing = viewModel.isRefreshing,
//     onRefresh = { viewModel.handleEvent(PostEvent.RefreshPosts) },
//     modifier = Modifier.fillMaxSize()
// ) {
//     LazyColumn(.......)
//         // Your LazyColumn content here
// }

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.viewmodel.MockReviewViewModel
import com.android.tripbook.ui.components.ReviewCard
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReviewsScreen(tripId: Int, onBack: () -> Unit) {
    val viewModel = remember { MockReviewViewModel() }

    // Collect reviews StateFlow as Compose state
    val reviews by viewModel.reviews.collectAsState()

    // Filter reviews locally by tripId
    val reviewsForTrip = reviews.filter { it.tripId == tripId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Reviews") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (reviewsForTrip.isEmpty()) {
                item {
                    Text("No reviews to display.", modifier = Modifier.padding(8.dp))
                }
            } else {
                items(reviewsForTrip) { review ->
                    ReviewCard(
                        review = review,
                        onLikeClicked = { viewModel.toggleLike(review.tripId, review.username) },
                        onFlagClicked = { viewModel.toggleFlag(review.tripId, review.username) }
                    )
                }
            }
        }
    }
}

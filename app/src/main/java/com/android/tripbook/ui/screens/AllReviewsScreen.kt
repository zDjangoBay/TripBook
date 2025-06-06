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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.viewmodel.MockReviewViewModel
import com.android.tripbook.ui.components.ReviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReviewsScreen(tripId: Int, onBack: () -> Unit) {
    val reviews = remember { MockReviewViewModel().getReviewsForTrip(tripId) }

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
            if (reviews.isEmpty()) {
                item {
                    Text("No reviews to display.", modifier = Modifier.padding(8.dp))
                }
            } else {
                items(reviews) { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }
}
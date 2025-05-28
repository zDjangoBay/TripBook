import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.catalog.components.FilterSection
import com.android.tripbook.ui.catalog.components.TripCard

@Composable
fun TripCatalogScreen(
    viewModel: TripCatalogViewModel,
    modifier: Modifier = Modifier,
) {
    val searchQuery by remember { derivedStateOf { viewModel.searchQuery } }
    val selectedLocation by remember { derivedStateOf { viewModel.selectedLocation } }
    val sortOption by remember { derivedStateOf { viewModel.sortOption } }
    val filteredTrips by remember { derivedStateOf { viewModel.filteredTrips } }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val columns = if (screenWidthDp < 600) 1 else 2

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Filter header spanning full width
        item(span = { GridItemSpan(maxLineSpan) }) {
            FilterSection(
                searchQuery = searchQuery,
                onSearchChange = { viewModel.searchQuery = it },
                locations = viewModel.locations,
                selectedLocation = selectedLocation,
                onLocationChange = { viewModel.selectedLocation = it },
                sortOption = sortOption,
                onSortChange = { viewModel.sortOption = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }

        items(filteredTrips) { trip ->
            TripCard(
                trip = trip,
                isLiked = viewModel.isLiked(trip.id),
                onLikeClicked = { viewModel.toggleLike(trip.id) },
                onCardClicked = { /* TODO */ }
            )
        }
    }
}

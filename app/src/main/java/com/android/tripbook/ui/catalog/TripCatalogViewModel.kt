import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.android.tripbook.data.TripRepository
import com.android.tripbook.ui.catalog.components.SortOption

class TripCatalogViewModel : ViewModel() {

    var allTrips = TripRepository.getSampleTrips()
        private set

    var searchQuery by mutableStateOf("")
    var selectedLocation by mutableStateOf("All")
    var sortOption by mutableStateOf(SortOption.NONE)

    // Use mutableStateOf with immutable Set
    var likedTrips by mutableStateOf(setOf<String>())
        private set

    val locations = listOf("All") + allTrips.map { it.location }.distinct()

    val filteredTrips by derivedStateOf {
        val filtered = allTrips.filter { trip ->
            (selectedLocation == "All" || trip.location == selectedLocation) &&
                    trip.title.contains(searchQuery, ignoreCase = true)
        }
        when (sortOption) {
            SortOption.NONE -> filtered
            SortOption.PRICE_ASC -> filtered.sortedBy { it.price }
            SortOption.PRICE_DESC -> filtered.sortedByDescending { it.price }
            SortOption.RATING_DESC -> filtered.sortedByDescending { it.rating }
            SortOption.TITLE_ASC -> filtered.sortedBy { it.title }
        }
    }

    fun toggleLike(tripId: String) {
        likedTrips = if (likedTrips.contains(tripId)) {
            likedTrips - tripId
        } else {
            likedTrips + tripId
        }
    }

    fun isLiked(tripId: String): Boolean {
        return likedTrips.contains(tripId)
    }
}

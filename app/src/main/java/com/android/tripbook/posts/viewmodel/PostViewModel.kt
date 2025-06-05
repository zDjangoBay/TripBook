package com.android.tripbook.posts.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.posts.model.Category
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Location // L'import de la classe Location locale
import com.android.tripbook.posts.model.Coordinates // L'import de la classe Coordinates locale
import com.android.tripbook.posts.model.TagModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class PostViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PostUIState())
    val uiState: StateFlow<PostUIState> = _uiState.asStateFlow()

    val availableTags = listOf(
        TagModel("t1", "Beach", Category.DESTINATION),
        TagModel("t2", "Mountains", Category.DESTINATION),
        TagModel("t3", "City", Category.DESTINATION),
        TagModel("t4", "Hiking", Category.ACTIVITY),
        TagModel("t5", "Photography", Category.ACTIVITY),
        TagModel("t6", "Foodie", Category.FOOD),
        TagModel("t7", "History", Category.THEME),
        TagModel("t8", "Adventure", Category.ADVENTURE),
        TagModel("t9", "Culture", Category.CULTURE),
        TagModel("t10", "Wildlife", Category.NATURE)
    )

    fun handleEvent(event: PostEvent) {
        when (event) {
            is PostEvent.UpdateTitle -> _uiState.update { it.copy(title = event.title, titleError = null) }
            is PostEvent.UpdateDescription -> _uiState.update { it.copy(description = event.description, descriptionError = null) }
            is PostEvent.AddImage -> {
                val newImage = ImageModel(id = UUID.randomUUID().toString(), uri = Uri.parse(event.imageUriString))
                _uiState.update {
                    it.copy(
                        images = it.images + newImage,
                        imagesError = null
                    )
                }
            }
            is PostEvent.RemoveImage -> _uiState.update {
                it.copy(images = it.images.filter { img -> img.id != event.imageId })
            }
            is PostEvent.SelectLocation -> _uiState.update {
                it.copy(selectedLocation = event.location, locationError = null)
            }
            is PostEvent.SearchLocation -> searchLocation(event.query)
            is PostEvent.ClearLocationSearch -> _uiState.update {
                it.copy(locationSearchResults = emptyList(), locationSearchError = null, isSearchingLocation = false)
            }
            is PostEvent.ToggleTag -> {
                _uiState.update { currentState ->
                    val newSelectedTags = if (currentState.selectedTags.any { it.id == event.tag.id }) {
                        currentState.selectedTags.filter { it.id != event.tag.id }
                    } else {
                        currentState.selectedTags + event.tag
                    }
                    currentState.copy(selectedTags = newSelectedTags, tagsError = null)
                }
            }
            is PostEvent.UpdateHashtags -> _uiState.update { it.copy(hashtagsInput = event.hashtagsInput) }
        }
    }

    private fun searchLocation(query: String) {
        _uiState.update { it.copy(isSearchingLocation = true, locationSearchError = null) }
        viewModelScope.launch {
            delay(500)
            val results = if (query.isBlank()) {
                emptyList()
            } else {
                // CORRECTION ICI : Assurez-vous que city, country et Coordinates sont toujours non-null.
                // Si les coordonnées sont nulles, passez 'null' explicitement au paramètre 'coordinates'
                listOf(
                    Location("loc1", "Eiffel Tower", "Paris", "France", Coordinates(48.8584, 2.2945)),
                    Location("loc2", "Louvre Museum", "Paris", "France", Coordinates(48.8606, 2.3376)),
                    Location("loc3", "Central Park", "New York", "USA", Coordinates(40.7829, -73.9654)),
                    Location("loc4", "Big Ben", "London", "UK", Coordinates(51.5007, -0.1246)),
                    Location("loc5", "Mount Cameroon", "Buea", "Cameroon", Coordinates(4.1667, 9.2500)),
                    Location("loc6", "Limbe Botanic Garden", "Limbe", "Cameroon", Coordinates(4.0200, 9.2200))
                ).filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.city.contains(query, ignoreCase = true) ||
                            it.country.contains(query, ignoreCase = true)
                }
            }
            if (results.isEmpty() && query.isNotBlank()) {
                _uiState.update { it.copy(isSearchingLocation = false, locationSearchResults = emptyList(), locationSearchError = "No matching locations found.") }
            } else {
                _uiState.update { it.copy(isSearchingLocation = false, locationSearchResults = results, locationSearchError = null) }
            }
        }
    }

    fun validateAndPublishPost(): Boolean {
        var isValid = true
        _uiState.update { currentState ->
            var newState = currentState.copy(
                titleError = null,
                descriptionError = null,
                imagesError = null,
                locationError = null,
                tagsError = null
            )

            if (currentState.title.isBlank()) {
                newState = newState.copy(titleError = "Title cannot be empty.")
                isValid = false
            } else if (currentState.title.length > 100) {
                newState = newState.copy(titleError = "Title is too long (max 100 characters).")
                isValid = false
            }

            if (currentState.description.isBlank()) {
                newState = newState.copy(descriptionError = "Description cannot be empty.")
                isValid = false
            } else if (currentState.description.length > 1000) {
                newState = newState.copy(descriptionError = "Description is too long (max 1000 characters).")
                isValid = false
            }

            if (currentState.images.isEmpty()) {
                newState = newState.copy(imagesError = "At least one image is required.")
                isValid = false
            }

            if (currentState.selectedLocation == null) {
                newState = newState.copy(locationError = "Location must be selected.")
                isValid = false
            }

            if (currentState.selectedTags.isEmpty()) {
                newState = newState.copy(tagsError = "At least one tag must be selected.")
                isValid = false
            }

            newState
        }
        return isValid
    }

    fun publishPost() {
        if (!validateAndPublishPost()) {
            return
        }

        _uiState.update { it.copy(isPublishing = true) }
        viewModelScope.launch {
            delay(2000)
            println("Post published successfully (mock)!")
            _uiState.update { it.copy(isPublishing = false, title = "", description = "", images = emptyList(), selectedLocation = null, selectedTags = emptyList(), hashtagsInput = "") }
        }
    }
}
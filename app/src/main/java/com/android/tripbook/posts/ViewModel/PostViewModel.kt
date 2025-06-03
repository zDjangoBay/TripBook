package com.android.tripbook.posts.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.tripbook.posts.model.Category
import com.android.tripbook.posts.model.Comment
import com.android.tripbook.posts.model.Coordinates
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Location
import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.TagModel
import com.android.tripbook.posts.repository.PostRepository
import com.android.tripbook.posts.utils.PostValidator
// Make sure PostUIState and PostEvent are imported from here if they are in the same package
// If they are in a different sub-package, the import will reflect that, e.g.:
// import com.android.tripbook.posts.viewmodel.PostEvent
// import com.android.tripbook.posts.viewmodel.PostUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class PostViewModel(
    private val repository: PostRepository,
    private val validator: PostValidator
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUIState())
    val uiState: StateFlow<PostUIState> = _uiState.asStateFlow()

    private val currentUserId = "current_user_simulated"
    private var locationSearchJob: Job? = null
    private val LOCATION_API_LOG_TAG = "GeoapifyAPI" // Updated Log Tag

    init {
        loadPosts()
        loadAvailableTags()
    }

    fun handleEvent(event: PostEvent) {
        when (event) {
            is PostEvent.LoadPosts -> loadPosts()
            is PostEvent.RefreshPosts -> refreshPosts()
            is PostEvent.SelectPost -> selectPost(event.postId)
            is PostEvent.TitleChanged -> updateTitle(event.title)
            is PostEvent.DescriptionChanged -> updateDescription(event.description)
            is PostEvent.ImageAdded -> addImage(event.image)
            is PostEvent.ImageRemoved -> removeImage(event.imageId)
            is PostEvent.LocationSelected -> selectLocation(event.location)
            is PostEvent.TagToggled -> toggleTag(event.tag)
            is PostEvent.HashtagsChanged -> updateHashtags(event.hashtags)
            is PostEvent.SubmitPost -> submitPost()
            is PostEvent.ResetForm -> resetForm()
            is PostEvent.DeletePost -> deletePost(event.postId)
            is PostEvent.ToggleLike -> toggleLike(event.postId)
            is PostEvent.AddComment -> addComment(event.postId, event.text)
            is PostEvent.AddReply -> addReply(event.postId, event.commentId, event.text)
            is PostEvent.SearchLocation -> searchLocationsGeoapify(event.query) // Changed to new method
            is PostEvent.ClearLocationSearch -> clearLocationSearch()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getAllPosts()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown error loading posts"
                        )
                    }
                    Log.e("PostViewModel", "Error loading posts", e)
                }
                .collect { updatedPostsList ->
                    val currentSelectedPostId = _uiState.value.selectedPost?.id
                    _uiState.update {
                        it.copy(
                            posts = updatedPostsList,
                            isLoading = false,
                            error = null,
                            selectedPost = if (currentSelectedPostId != null) {
                                updatedPostsList.find { post -> post.id == currentSelectedPostId }
                            } else {
                                null
                            }
                        )
                    }
                }
        }
    }

    private fun refreshPosts() {
        loadPosts()
    }

    private fun selectPost(postId: String) {
        val post = _uiState.value.posts.find { it.id == postId }
        if (post != _uiState.value.selectedPost || _uiState.value.selectedPost == null) {
            _uiState.update { it.copy(selectedPost = post) }
        }
    }

    private fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
        validateForm()
    }

    private fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
        validateForm()
    }

    private fun addImage(image: ImageModel) {
        _uiState.update {
            val currentImages = it.selectedImages.toMutableList()
            if (currentImages.size < 10) {
                currentImages.add(image)
            }
            it.copy(selectedImages = currentImages)
        }
        validateForm()
    }

    private fun removeImage(imageId: String) {
        _uiState.update {
            val currentImages = it.selectedImages.toMutableList()
            currentImages.removeAll { img -> img.id == imageId }
            it.copy(selectedImages = currentImages)
        }
        validateForm()
    }

    private fun selectLocation(location: Location) {
        _uiState.update { it.copy(selectedLocation = location, locationSearchResults = emptyList(), isSearchingLocation = false, locationSearchError = null) }
        validateForm()
    }

    private fun toggleTag(tag: TagModel) {
        _uiState.update {
            val currentTags = it.selectedTags.toMutableList()
            val existingTag = currentTags.find { t -> t.id == tag.id }
            if (existingTag != null) {
                currentTags.remove(existingTag)
            } else {
                currentTags.add(tag.copy(isSelected = true))
            }
            it.copy(selectedTags = currentTags)
        }
        validateForm()
    }

    private fun updateHashtags(hashtags: String) {
        _uiState.update { it.copy(hashtags = hashtags) }
        validateForm()
    }

    private fun validateForm() {
        val currentState = _uiState.value
        val isValid = validator.validatePost(
            title = currentState.title,
            description = currentState.description,
            location = currentState.selectedLocation,
            images = currentState.selectedImages
        )
        _uiState.update { it.copy(isFormValid = isValid) }
    }

    private fun submitPost() {
        if (!_uiState.value.isFormValid) {
            _uiState.update { it.copy(error = "Please fill all required fields correctly.") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSubmitting = true, error = null) }
                val currentState = _uiState.value
                val hashtagList = parseHashtags(currentState.hashtags)
                val newPost = PostModel(
                    userId = currentUserId, username = "Wanderer_7",
                    userAvatar = "https://i.pravatar.cc/100?u=${System.currentTimeMillis()}",
                    title = currentState.title, description = currentState.description,
                    location = currentState.selectedLocation!!, images = currentState.selectedImages,
                    categories = currentState.selectedTags.map { it.category }.distinct(),
                    tags = currentState.selectedTags, hashtags = hashtagList
                )
                repository.createPost(newPost)
                resetForm()
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error submitting post", e)
                _uiState.update {
                    it.copy(error = e.message ?: "Failed to create post. Please try again.", isSubmitting = false)
                }
            }
        }
    }

    private fun resetForm() {
        _uiState.update {
            PostUIState(
                posts = it.posts, availableTags = it.availableTags,
                title = "", description = "", selectedImages = emptyList(),
                selectedLocation = null, selectedTags = emptyList(), hashtags = "",
                isSubmitting = false, isFormValid = false, error = null,
                locationSearchResults = emptyList(), isSearchingLocation = false,
                locationSearchError = null, selectedPost = it.selectedPost
            )
        }
    }

    private fun parseHashtags(input: String): List<String> {
        return input.split(" ", ",", "\n").map { it.trim().removePrefix("#") }.filter { it.isNotBlank() && it.length < 30 }
    }

    private fun loadAvailableTags() {
        val tags = Category.values().flatMap { cat ->
            val tagNames = when (cat) {
                Category.ADVENTURE -> listOf("hiking", "climbing", "kayaking", "ziplining", "scuba diving")
                Category.CULTURE -> listOf("museum", "festival", "local arts", "temple", "history")
                Category.FOOD -> listOf("restaurant", "street food", "cooking class", "cafe", "local market")
                Category.NATURE -> listOf("wildlife", "national park", "waterfall", "forest", "botanical garden")
                Category.URBAN -> listOf("city walk", "architecture", "nightlife", "shopping", "public art")
                Category.BEACH -> listOf("surfing", "sunbathing", "snorkeling", "beachcombing", "volleyball")
                Category.MOUNTAINS -> listOf("trekking", "skiing", "mountain biking", "peak bagging", "scenic drive")
                Category.HISTORICAL -> listOf("ancient ruins", "monuments", "heritage sites", "castle", "battlefield")
            }
            tagNames.map { tagName -> TagModel(id = "${cat.name.lowercase()}_${tagName.replace(" ", "_")}", name = tagName, category = cat) }
        }
        _uiState.update { it.copy(availableTags = tags) }
    }

    private fun deletePost(postId: String) {
        viewModelScope.launch {
            try { repository.deletePost(postId) }
            catch (e: Exception) { Log.e("PostViewModel", "Error deleting post $postId", e); _uiState.update { it.copy(error = e.message ?: "Failed to delete post") } }
        }
    }
    private fun toggleLike(postId: String) {
        viewModelScope.launch {
            try { repository.toggleLike(postId, currentUserId) }
            catch (e: Exception) { Log.e("PostViewModel", "Error toggling like for post $postId", e); _uiState.update { it.copy(error = e.message ?: "Failed to toggle like") } }
        }
    }
    private fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            try { val comment = Comment(userId = currentUserId, username = "Commenter User", text = text.trim()); repository.addComment(postId, comment) }
            catch (e: Exception) { Log.e("PostViewModel", "Error adding comment to post $postId", e); _uiState.update { it.copy(error = e.message ?: "Failed to add comment") } }
        }
    }
    private fun addReply(postId: String, commentId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            try { val reply = Comment(userId = currentUserId, username = "Reply User", text = text.trim()); repository.addReply(postId, commentId, reply) }
            catch (e: Exception) { Log.e("PostViewModel", "Error adding reply to comment $commentId on post $postId", e); _uiState.update { it.copy(error = e.message ?: "Failed to add reply") } }
        }
    }

    // Updated to use Geoapify Geocoding Autocomplete API
    private fun searchLocationsGeoapify(query: String) {
        locationSearchJob?.cancel()
        if (query.length < 2) { // Typically, autocomplete APIs work better with at least 2-3 characters
            _uiState.update { it.copy(locationSearchResults = emptyList(), isSearchingLocation = false, locationSearchError = null) }
            return
        }
        Log.d(LOCATION_API_LOG_TAG, "Searching Geoapify for: $query")
        _uiState.update { it.copy(isSearchingLocation = true, locationSearchError = null) }

        locationSearchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(350) // Debounce to avoid too many API calls while typing
            val apiKey = "b821c991175547b8b1387e609a80a37d" // Your Geoapify API key
            val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
            // Using Geoapify Geocoding Autocomplete endpoint
            // You might want to add more parameters like 'type=city' or 'lang=en' if needed
            val urlString = "https://api.geoapify.com/v1/geocode/autocomplete?text=$encodedQuery&apiKey=$apiKey&limit=10"
            Log.d(LOCATION_API_LOG_TAG, "Request URL: $urlString")

            var connection: HttpURLConnection? = null
            try {
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                // Geoapify usually doesn't require custom host headers like RapidAPI
                connection.connectTimeout = 15000 // 15 seconds
                connection.readTimeout = 15000  // 15 seconds

                val responseCode = connection.responseCode
                Log.d(LOCATION_API_LOG_TAG, "Response Code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()
                    Log.d(LOCATION_API_LOG_TAG, "Response JSON: $response")

                    val jsonResponse = JSONObject(response)
                    val featuresArray: JSONArray = jsonResponse.optJSONArray("features") ?: JSONArray()
                    val results = mutableListOf<Location>()

                    for (i in 0 until featuresArray.length()) {
                        val feature = featuresArray.getJSONObject(i)
                        val properties = feature.optJSONObject("properties")
                        if (properties != null) {
                            val name = properties.optString("name", properties.optString("formatted","Unknown Place")) // Prefer 'name', fallback to 'formatted'
                            val city = properties.optString("city", "") // May not always be present or same as name
                            val country = properties.optString("country", "Unknown Country")
                            val lat = properties.optDouble("lat", 0.0)
                            val lon = properties.optDouble("lon", 0.0)

                            // Construct a display name, prefer formatted if available and different from name
                            val displayName = properties.optString("formatted", name)

                            results.add(
                                Location(
                                    name = displayName, // Use formatted or name for display
                                    city = if (city.isNotEmpty() && city != name) city else name, // Use city if distinct, else name
                                    country = country,
                                    coordinates = Coordinates(latitude = lat, longitude = lon)
                                )
                            )
                        }
                    }
                    Log.d(LOCATION_API_LOG_TAG, "Parsed ${results.size} locations from Geoapify.")
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(locationSearchResults = results, isSearchingLocation = false) }
                    }
                } else {
                    val errorResponse = connection.errorStream?.bufferedReader()?.readText() ?: connection.responseMessage
                    Log.e(LOCATION_API_LOG_TAG, "Error: $responseCode - $errorResponse")
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isSearchingLocation = false, locationSearchError = "Error fetching locations ($responseCode).") }
                    }
                }
            } catch (e: Exception) {
                Log.e(LOCATION_API_LOG_TAG, "Exception during Geoapify location search", e)
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isSearchingLocation = false, locationSearchError = "Network error or processing failed.") }
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun clearLocationSearch() {
        locationSearchJob?.cancel()
        _uiState.update { it.copy(locationSearchResults = emptyList(), isSearchingLocation = false, locationSearchError = null) }
    }
}

class PostViewModelFactory(
    private val repository: PostRepository,
    private val validator: PostValidator
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(repository, validator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

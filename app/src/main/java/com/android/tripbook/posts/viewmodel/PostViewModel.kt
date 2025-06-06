package com.android.tripbook.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.tripbook.posts.model.*
import com.android.tripbook.posts.repository.PostRepository
import com.android.tripbook.posts.utils.ConfigManager
import com.android.tripbook.posts.utils.PostValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository,
    private val validator: PostValidator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PostUIState())
    val uiState: StateFlow<PostUIState> = _uiState.asStateFlow()
    
    init {
        initializeData()
    }
    
    private fun initializeData() {
        viewModelScope.launch {
            // Add sample Cameroon travel posts
            addSampleCameroonPosts()
            
            // Initialize available tags
            updateAvailableTags()
            
            // Collect posts from repository
            repository.getAllPosts().collect { posts ->
                _uiState.value = _uiState.value.copy(
                    posts = posts,
                    isLoading = false
                )
            }
        }
    }
    
    private suspend fun addSampleCameroonPosts() {
        val samplePosts = listOf(
            PostModel(
                userId = ConfigManager.DEFAULT_USER_ID,
                username = "CameroonExplorer",
                userAvatar = "https://i.pravatar.cc/100?u=cameroon001",
                title = "Mount Cameroon Adventure",
                description = "Just conquered the highest peak in West Africa! The sunrise from Mount Cameroon was absolutely breathtaking. The volcanic landscape and diverse ecosystems make this a must-visit destination for any adventure seeker.",
                location = Location(
                    name = "Mount Cameroon",
                    city = "Buea",
                    country = "Cameroon",
                    coordinates = Coordinates(4.2026, 9.1705)
                ),
                categories = listOf(Category.ADVENTURE, Category.MOUNTAINS, Category.NATURE),
                tags = listOf(
                    TagModel("1", "Hiking", Category.ADVENTURE),
                    TagModel("2", "Mountain", Category.MOUNTAINS),
                    TagModel("3", "Volcano", Category.NATURE)
                ),
                hashtags = listOf("#MountCameroon", "#WestAfrica", "#Hiking", "#Adventure"),
                likes = listOf("user1", "user2", "user3"),
                comments = listOf(
                    Comment(
                        userId = "user1",
                        username = "TravelBuddy",
                        text = "Wow! This looks amazing. How long did the hike take?"
                    )
                )
            ),
            PostModel(
                userId = ConfigManager.DEFAULT_USER_ID,
                username = "AfricaWanderer",
                userAvatar = "https://i.pravatar.cc/100?u=cameroon002",
                title = "Waza National Park Safari",
                description = "Incredible wildlife spotting at Waza National Park! Saw elephants, giraffes, lions, and so many beautiful bird species. The dry season is perfect for game viewing as animals gather around water sources.",
                location = Location(
                    name = "Waza National Park",
                    city = "Waza",
                    country = "Cameroon",
                    coordinates = Coordinates(11.3167, 14.6500)
                ),
                categories = listOf(Category.NATURE, Category.ADVENTURE),
                tags = listOf(
                    TagModel("4", "Safari", Category.NATURE),
                    TagModel("5", "Wildlife", Category.NATURE),
                    TagModel("6", "Photography", Category.ADVENTURE)
                ),
                hashtags = listOf("#WazaPark", "#Safari", "#Wildlife", "#Cameroon"),
                likes = listOf("user4", "user5"),
                comments = listOf()
            ),
            PostModel(
                userId = ConfigManager.DEFAULT_USER_ID,
                username = "CulturalTraveler",
                userAvatar = "https://i.pravatar.cc/100?u=cameroon003",
                title = "Foumban Royal Palace",
                description = "Exploring the rich Bamoun culture at the Royal Palace of Foumban. The architecture is stunning and the museum houses incredible artifacts that tell the story of this ancient kingdom. A perfect blend of history and culture!",
                location = Location(
                    name = "Foumban Royal Palace",
                    city = "Foumban",
                    country = "Cameroon",
                    coordinates = Coordinates(5.7269, 10.9003)
                ),
                categories = listOf(Category.CULTURE, Category.HISTORICAL),
                tags = listOf(
                    TagModel("7", "Culture", Category.CULTURE),
                    TagModel("8", "History", Category.HISTORICAL),
                    TagModel("9", "Palace", Category.HISTORICAL)
                ),
                hashtags = listOf("#Foumban", "#BamounCulture", "#RoyalPalace", "#History"),
                likes = listOf("user6", "user7", "user8", "user9"),
                comments = listOf(
                    Comment(
                        userId = "user6",
                        username = "HistoryBuff",
                        text = "The Bamoun script is fascinating! Did you learn about it during your visit?"
                    ),
                    Comment(
                        userId = "user7",
                        username = "CultureLover",
                        text = "Added to my travel bucket list! Thanks for sharing."
                    )
                )
            ),
            PostModel(
                userId = ConfigManager.DEFAULT_USER_ID,
                username = "BeachVibes",
                userAvatar = "https://i.pravatar.cc/100?u=cameroon004",
                title = "Limbe Beach Paradise",
                description = "Black volcanic sand beaches of Limbe are absolutely unique! Spent the day relaxing by the Atlantic Ocean with the backdrop of Mount Cameroon. Don't miss the Limbe Wildlife Centre nearby - great for conservation efforts!",
                location = Location(
                    name = "Limbe Beach",
                    city = "Limbe",
                    country = "Cameroon",
                    coordinates = Coordinates(4.0186, 9.2056)
                ),
                categories = listOf(Category.BEACH, Category.NATURE),
                tags = listOf(
                    TagModel("10", "Beach", Category.BEACH),
                    TagModel("11", "Ocean", Category.BEACH),
                    TagModel("12", "Relaxation", Category.NATURE)
                ),
                hashtags = listOf("#LimbeBeach", "#BlackSand", "#Atlantic", "#BeachLife"),
                likes = listOf("user10", "user11"),
                comments = listOf()
            )
        )
        
        samplePosts.forEach { post ->
            repository.createPost(post)
        }
    }
    
    private fun updateAvailableTags() {
        val tags = listOf(
            TagModel("1", "Adventure", Category.ADVENTURE),
            TagModel("2", "Culture", Category.CULTURE),
            TagModel("3", "Food", Category.FOOD),
            TagModel("4", "Nature", Category.NATURE),
            TagModel("5", "Urban", Category.URBAN),
            TagModel("6", "Beach", Category.BEACH),
            TagModel("7", "Mountains", Category.MOUNTAINS),
            TagModel("8", "Historical", Category.HISTORICAL),
            TagModel("9", "Safari", Category.NATURE),
            TagModel("10", "Wildlife", Category.NATURE),
            TagModel("11", "Hiking", Category.ADVENTURE),
            TagModel("12", "Photography", Category.ADVENTURE)
        )
        
        _uiState.value = _uiState.value.copy(availableTags = tags)
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
            is PostEvent.SearchLocation -> searchLocation(event.query)
            is PostEvent.ClearLocationSearch -> clearLocationSearch()
        }
    }
    
    private fun loadPosts() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
    }
    
    private fun refreshPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            // Refresh logic would go here
        }
    }
    
    private fun selectPost(postId: String) {
        val post = _uiState.value.posts.find { it.id == postId }
        _uiState.value = _uiState.value.copy(selectedPost = post)
    }
    
    private fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
        validateForm()
    }
    
    private fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
        validateForm()
    }
    
    private fun addImage(image: ImageModel) {
        val currentImages = _uiState.value.selectedImages.toMutableList()
        currentImages.add(image)
        _uiState.value = _uiState.value.copy(selectedImages = currentImages)
    }
    
    private fun removeImage(imageId: String) {
        val currentImages = _uiState.value.selectedImages.filter { it.id != imageId }
        _uiState.value = _uiState.value.copy(selectedImages = currentImages)
    }
    
    private fun selectLocation(location: Location) {
        _uiState.value = _uiState.value.copy(selectedLocation = location)
        validateForm()
    }
    
    private fun toggleTag(tag: TagModel) {
        val currentTags = _uiState.value.selectedTags.toMutableList()
        val existingTag = currentTags.find { it.id == tag.id }
        
        if (existingTag != null) {
            currentTags.remove(existingTag)
        } else {
            currentTags.add(tag.copy(isSelected = true))
        }
        
        _uiState.value = _uiState.value.copy(selectedTags = currentTags)
    }
    
    private fun updateHashtags(hashtags: String) {
        _uiState.value = _uiState.value.copy(hashtags = hashtags)
    }
    
    private fun submitPost() {
        if (!_uiState.value.isFormValid) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null)
            
            try {
                val post = PostModel(
                    userId = ConfigManager.DEFAULT_USER_ID,
                    username = ConfigManager.DEFAULT_USERNAME,
                    userAvatar = ConfigManager.DEFAULT_USER_AVATAR,
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    location = _uiState.value.selectedLocation!!,
                    images = _uiState.value.selectedImages,
                    tags = _uiState.value.selectedTags,
                    hashtags = _uiState.value.hashtags.split(" ").filter { it.startsWith("#") }
                )
                
                repository.createPost(post)
                resetForm()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to create post: ${e.message}",
                    isSubmitting = false
                )
            }
        }
    }
    
    private fun resetForm() {
        _uiState.value = _uiState.value.copy(
            title = "",
            description = "",
            selectedImages = emptyList(),
            selectedLocation = null,
            selectedTags = emptyList(),
            hashtags = "",
            isFormValid = false,
            isSubmitting = false,
            error = null
        )
    }
    
    private fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                repository.deletePost(postId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete post")
            }
        }
    }
    
    private fun toggleLike(postId: String) {
        viewModelScope.launch {
            try {
                repository.toggleLike(postId, ConfigManager.DEFAULT_USER_ID)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to toggle like")
            }
        }
    }
    
    private fun addComment(postId: String, text: String) {
        viewModelScope.launch {
            try {
                val comment = Comment(
                    userId = ConfigManager.DEFAULT_USER_ID,
                    username = ConfigManager.DEFAULT_USERNAME,
                    text = text
                )
                repository.addComment(postId, comment)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to add comment")
            }
        }
    }
    
    private fun addReply(postId: String, commentId: String, text: String) {
        viewModelScope.launch {
            try {
                val reply = Comment(
                    userId = ConfigManager.DEFAULT_USER_ID,
                    username = ConfigManager.DEFAULT_USERNAME,
                    text = text
                )
                repository.addReply(postId, commentId, reply)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to add reply")
            }
        }
    }
    
    private fun searchLocation(query: String) {
        _uiState.value = _uiState.value.copy(isSearchingLocation = true)
        // In a real app, this would call a location API
        // For now, just simulate a search
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(
                isSearchingLocation = false,
                locationSearchResults = emptyList()
            )
        }
    }
    
    private fun clearLocationSearch() {
        _uiState.value = _uiState.value.copy(
            locationSearchResults = emptyList(),
            locationSearchError = null
        )
    }
    
    private fun validateForm() {
        val isValid = validator.validatePost(
            _uiState.value.title,
            _uiState.value.description,
            _uiState.value.selectedLocation,
            _uiState.value.selectedImages
        )
        _uiState.value = _uiState.value.copy(isFormValid = isValid)
    }
}

class PostViewModelFactory(
    private val repository: PostRepository,
    private val validator: PostValidator
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(repository, validator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
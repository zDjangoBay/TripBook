package com.android.tripbook.posts.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.accessibility.TextToSpeechService
import com.android.tripbook.posts.model.Post
import com.android.tripbook.posts.model.TranslationState
import com.android.tripbook.preferences.UserPreferences
import com.android.tripbook.preferences.UserPreferencesRepository
import com.android.tripbook.translation.TranslationService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class PostsViewModel(
    private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val translationService: TranslationService,
    private val textToSpeechService: TextToSpeechService
) : ViewModel() {
    
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()
    
    private val _translationStates = MutableStateFlow<Map<String, TranslationState>>(emptyMap())
    val translationStates: StateFlow<Map<String, TranslationState>> = _translationStates.asStateFlow()
    
    private val _currentSpeakingPostId = MutableStateFlow<String?>(null)
    val currentSpeakingPostId: StateFlow<String?> = _currentSpeakingPostId.asStateFlow()
    
    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )
    
    val isSpeaking: StateFlow<Boolean> = textToSpeechService.isSpeaking
    
    init {
        // Charger des posts d'exemple
        loadSamplePosts()
        
        // Observer les changements de langue TTS
        viewModelScope.launch {
            userPreferences.collect { preferences ->
                textToSpeechService.setSpeechRate(preferences.speechRate)
            }
        }
        
        // Observer les changements d'état de lecture
        viewModelScope.launch {
            isSpeaking.collect { speaking ->
                if (!speaking) {
                    _currentSpeakingPostId.value = null
                }
            }
        }
    }
    
    private fun loadSamplePosts() {
        viewModelScope.launch {
            // Création d'un URI d'exemple pour la photo de manguier
            // En réalité, cette URI viendrait de la galerie ou appareil photo
            val mangoPhotoUri = Uri.parse("android.resource://com.android.tripbook/drawable/photo")
            
            val samplePosts = listOf(
                // Post d'exemple avec photo de manguier et texte en espagnol
                Post(
                    id = "mango_post",
                    content = "¡Necesito este mango! Mi hijo está en el árbol y quiere cogerlo. ¡Qué aventura!",
                    author = "Marie claire",
                    timestamp = System.currentTimeMillis() - 1800000, // 30 minutes ago
                    photos = listOf(mangoPhotoUri)
                ),
                Post(
                    id = "1",
                    content = "Hello everyone! Just arrived in beautiful KRIBI. The beach is amazing!",
                    author = "Mbarga joseph",
                    timestamp = System.currentTimeMillis() - 3600000
                ),
                Post(
                    id = "2",
                    content = "Bonjour tout le monde ! Quelle belle journée pour visiter DOUALA.",
                    author = "zambo joseph",
                    timestamp = System.currentTimeMillis() - 7200000
                ),
                Post(
                    id = "3",
                    content = "¡Hola! Visitando el hermoso barrio de Montmartre. ¡Las vistas son increíbles!",
                    author = "ondo gladis",
                    timestamp = System.currentTimeMillis() - 10800000
                ),
                Post(
                    id = "4",
                    content = "Guten Tag! Das Essen in diesem kleinen Café ist fantastisch. Sehr empfehlenswert!",
                    author = " yves",
                    timestamp = System.currentTimeMillis() - 14400000
                )
            )
            
            // Détecter la langue de chaque post
            val postsWithLanguage = samplePosts.map { post ->
                try {
                    val detectedLanguage = translationService.detectLanguage(post.content)
                    post.copy(originalLanguage = detectedLanguage)
                } catch (e: Exception) {
                    post
                }
            }
            
            _posts.value = postsWithLanguage
        }
    }
    
    fun translatePost(postId: String) {
        val post = _posts.value.find { it.id == postId } ?: return
        if (post.originalLanguage == null || post.originalLanguage == "fr") return
        
        viewModelScope.launch {
            // Mettre à jour l'état de traduction
            _translationStates.value = _translationStates.value.toMutableMap().apply {
                this[postId] = TranslationState(isTranslating = true)
            }
            
            try {
                val translatedText = translationService.translateText(
                    text = post.content,
                    sourceLanguage = post.originalLanguage,
                    targetLanguage = "fr"
                )
                
                // Mettre à jour le post avec la traduction
                _posts.value = _posts.value.map { p ->
                    if (p.id == postId) {
                        p.copy(
                            translatedContent = translatedText,
                            targetLanguage = "fr",
                            isTranslated = true
                        )
                    } else p
                }
                
                // Mettre à jour l'état de traduction
                _translationStates.value = _translationStates.value.toMutableMap().apply {
                    this[postId] = TranslationState(isTranslating = false)
                }
                
            } catch (e: Exception) {
                _translationStates.value = _translationStates.value.toMutableMap().apply {
                    this[postId] = TranslationState(
                        isTranslating = false,
                        error = "Erreur de traduction: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun speakPost(postId: String) {
        val post = _posts.value.find { it.id == postId } ?: return
        
        // Arrêter la lecture en cours si nécessaire
        if (_currentSpeakingPostId.value != null) {
            textToSpeechService.stop()
        }
        
        _currentSpeakingPostId.value = postId
        
        // Utiliser la traduction si disponible et en français
        val textToSpeak = if (post.isTranslated && post.translatedContent != null) {
            post.translatedContent
        } else {
            post.content
        }
        
        textToSpeechService.speakPostContent(textToSpeak, post.author)
    }
    
    fun stopSpeaking() {
        textToSpeechService.stop()
        _currentSpeakingPostId.value = null
    }
    
    // Gestion des préférences
    fun toggleDarkMode() {
        viewModelScope.launch {
            val currentPrefs = userPreferences.value
            userPreferencesRepository.updateDarkMode(!currentPrefs.isDarkMode)
        }
    }
    
    fun updateTextSize(textSize: com.android.tripbook.preferences.TextSize) {
        viewModelScope.launch {
            userPreferencesRepository.updateTextSize(textSize)
        }
    }
    
    fun toggleAccessibilityMode() {
        viewModelScope.launch {
            val currentPrefs = userPreferences.value
            userPreferencesRepository.updateAccessibilityMode(!currentPrefs.isAccessibilityModeEnabled)
        }
    }
    
    fun updateSpeechRate(rate: Float) {
        viewModelScope.launch {
            userPreferencesRepository.updateSpeechRate(rate)
        }
    }
    
    fun addPost(content: String, author: String) {
        viewModelScope.launch {
            val newPost = Post(
                id = UUID.randomUUID().toString(),
                content = content,
                author = author,
                timestamp = System.currentTimeMillis()
            )
            
            // Détecter la langue
            try {
                val detectedLanguage = translationService.detectLanguage(content)
                val postWithLanguage = newPost.copy(originalLanguage = detectedLanguage)
                
                _posts.value = listOf(postWithLanguage) + _posts.value
            } catch (e: Exception) {
                _posts.value = listOf(newPost) + _posts.value
            }
        }
    }
    
    fun addPostWithPhotos(content: String, author: String, photos: List<Uri>) {
        viewModelScope.launch {
            val newPost = Post(
                id = UUID.randomUUID().toString(),
                content = content,
                author = author,
                timestamp = System.currentTimeMillis(),
                photos = photos
            )
            
            // Détecter la langue
            try {
                val detectedLanguage = translationService.detectLanguage(content)
                val postWithLanguage = newPost.copy(originalLanguage = detectedLanguage)
                
                _posts.value = listOf(postWithLanguage) + _posts.value
            } catch (e: Exception) {
                _posts.value = listOf(newPost) + _posts.value
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        translationService.cleanup()
        textToSpeechService.shutdown()
    }
}


package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.tripbook.accessibility.TextToSpeechService
import com.android.tripbook.posts.ui.components.PostCard
import com.android.tripbook.posts.viewmodel.PostsViewModel
import com.android.tripbook.preferences.UserPreferencesRepository
import com.android.tripbook.settings.SettingsScreen
import com.android.tripbook.translation.TranslationService
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    
    private val viewModel: PostsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PostsViewModel(
                    context = applicationContext,
                    userPreferencesRepository = UserPreferencesRepository(applicationContext),
                    translationService = TranslationService(),
                    textToSpeechService = TextToSpeechService(applicationContext)
                ) as T
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPreferences by viewModel.userPreferences.collectAsState()
            
            TripBookTheme(
                darkTheme = userPreferences.isDarkMode,
                textSize = userPreferences.textSize
            ) {
                TripBookApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripBookApp(viewModel: PostsViewModel) {
    var showSettings by remember { mutableStateOf(false) }
    
    val posts by viewModel.posts.collectAsState()
    val translationStates by viewModel.translationStates.collectAsState()
    val currentSpeakingPostId by viewModel.currentSpeakingPostId.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()
    
    if (showSettings) {
        SettingsScreen(
            userPreferences = userPreferences,
            onDarkModeToggle = viewModel::toggleDarkMode,
            onTextSizeChange = viewModel::updateTextSize,
            onAccessibilityModeToggle = viewModel::toggleAccessibilityMode,
            onSpeechRateChange = viewModel::updateSpeechRate
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("TripBook") },
                    actions = {
                        IconButton(onClick = { showSettings = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Paramètres"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            PostsList(
                posts = posts,
                translationStates = translationStates,
                currentSpeakingPostId = currentSpeakingPostId,
                isAccessibilityModeEnabled = userPreferences.isAccessibilityModeEnabled,
                onTranslatePost = viewModel::translatePost,
                onSpeakPost = viewModel::speakPost,
                onStopSpeaking = viewModel::stopSpeaking,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
    
    // Gestion du bouton retour pour revenir de l'écran des paramètres
    if (showSettings) {
        LaunchedEffect(Unit) {
            // Cette logique pourrait être améliorée avec un système de navigation plus robuste
        }
    }
}

@Composable
fun PostsList(
    posts: List<com.android.tripbook.posts.model.Post>,
    translationStates: Map<String, com.android.tripbook.posts.model.TranslationState>,
    currentSpeakingPostId: String?,
    isAccessibilityModeEnabled: Boolean,
    onTranslatePost: (String) -> Unit,
    onSpeakPost: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(posts) { post ->
            val translationState = translationStates[post.id]
            val isSpeaking = currentSpeakingPostId == post.id
            
            PostCard(
                post = post,
                isAccessibilityModeEnabled = isAccessibilityModeEnabled,
                isSpeaking = isSpeaking,
                isTranslating = translationState?.isTranslating == true,
                onTranslateClick = { onTranslatePost(post.id) },
                onSpeakClick = { onSpeakPost(post.id) },
                onStopSpeaking = onStopSpeaking
            )
        }
        
        if (posts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Text(
                        text = "Aucun post disponible",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

package com.android.tripbook.demo

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.model.Post
import com.android.tripbook.posts.ui.components.PhotoGallery
import com.android.tripbook.ui.theme.TripBookTheme


class MangoPostDemoActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            TripBookTheme {
                MangoPostDemoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangoPostDemoScreen() {
    var showTranslation by remember { mutableStateOf(false) }
    var isTranslating by remember { mutableStateOf(false) }
    

    val mangoPost = Post(
        id = "demo_mango",
        content = "¡Necesito este mango! Mi hijo está en el árbol y quiere cogerlo. ¡Qué aventura!",
        originalLanguage = "es", // Espagnol détecté
        translatedContent = if (showTranslation) "J'ai besoin de cette mangue ! Mon fils est sur l'arbre et veut l'attraper. Quelle aventure !" else null,
        targetLanguage = if (showTranslation) "fr" else null,
        author = "Marie claire",
        timestamp = System.currentTimeMillis() - 1800000,
        isTranslated = showTranslation,
        photos = listOf(
            // URI d'exemple - en réalité viendrait de la galerie
            Uri.parse("android.resource://com.android.tripbook/drawable/photo")
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Démonstration : Post Manguier",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Explication de la fonctionnalité
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "🥭 Exemple de Post Multilingue avec Photo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ce post démontre comment TripBook gère :\n" +
                        "• Les photos (ici un manguier)\n" +
                        "• La détection automatique de langue (espagnol)\n" +
                        "• La traduction automatique vers le français\n" +
                        "• L'affichage bilingue",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Affichage du post
            PostCard(
                post = mangoPost,
                onTranslate = { 
                    isTranslating = true
                    // Simuler un délai de traduction
                    /* 
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(1500)
                        showTranslation = true
                        isTranslating = false
                    }
                    */
                    // Pour la demo, traduction instantanée
                    showTranslation = true
                    isTranslating = false
                },
                onSpeak = { /* Fonctionnalité TTS */ },
                isTranslating = isTranslating
            )
            
            // Informations techniques
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "🔧 Informations Techniques",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    InfoRow("Langue détectée", "Espagnol (es)")
                    InfoRow("Auteur", "Marie claire")
                    InfoRow("Nombre de photos", "1")
                    InfoRow("Type d'affichage", "Photo unique (grand format)")
                    InfoRow("Service de traduction", "Google ML Kit")
                    InfoRow("Langue cible", "Français (fr)")
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onTranslate: () -> Unit,
    onSpeak: () -> Unit,
    isTranslating: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // En-tête du post
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.author,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (post.originalLanguage != null) {
                    AssistChip(
                        onClick = { },
                        label = { 
                            Text(
                                when(post.originalLanguage) {
                                    "es" -> "🇪🇸 Espagnol"
                                    "fr" -> "🇫🇷 Français"
                                    "en" -> "🇺🇸 Anglais"
                                    else -> post.originalLanguage
                                }
                            ) 
                        }
                    )
                }
            }
            
            // Photo(s)
            if (post.photos.isNotEmpty()) {
                PhotoGallery(
                    photos = post.photos,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Contenu original
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Texte original (espagnol) :",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        post.content,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            // Traduction si disponible
            if (post.isTranslated && post.translatedContent != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Traduction française :",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            post.translatedContent,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!post.isTranslated && !isTranslating) {
                    Button(
                        onClick = onTranslate,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🌐 Traduire")
                    }
                }
                
                if (isTranslating) {
                    Button(
                        onClick = { },
                        enabled = false,
                        modifier = Modifier.weight(1f)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Traduction...")
                    }
                }
                
                OutlinedButton(
                    onClick = onSpeak,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🔊 Écouter")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label :",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


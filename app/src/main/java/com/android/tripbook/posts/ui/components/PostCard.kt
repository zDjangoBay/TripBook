package com.android.tripbook.posts.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.model.Post
import com.android.tripbook.ui.theme.AccessibilityHighlight
import com.android.tripbook.ui.theme.SpeechActiveColor
import com.android.tripbook.ui.theme.TranslationIndicator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    post: Post,
    isAccessibilityModeEnabled: Boolean,
    isSpeaking: Boolean,
    isTranslating: Boolean,
    onTranslateClick: () -> Unit,
    onSpeakClick: () -> Unit,
    onStopSpeaking: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTranslation by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(
                if (isAccessibilityModeEnabled) {
                    Modifier.border(
                        2.dp,
                        if (isSpeaking) SpeechActiveColor else AccessibilityHighlight,
                        RoundedCornerShape(12.dp)
                    )
                } else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // En-tête du post
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.author,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = formatTimestamp(post.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Indicateur de langue détectée
                post.originalLanguage?.let { language ->
                    if (language != "fr") {
                        Surface(
                            color = TranslationIndicator.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = language.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TranslationIndicator,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Contenu du post
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Photos du post s'il y en a
            if (post.photos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                PhotoGallery(
                    photos = post.photos,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Traduction si disponible
            AnimatedVisibility(
                visible = showTranslation && post.isTranslated && post.translatedContent != null,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                post.translatedContent?.let { translatedText ->
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            color = TranslationIndicator.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Traduction :",
                            style = MaterialTheme.typography.labelMedium,
                            color = TranslationIndicator,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = translatedText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    TranslationIndicator.copy(alpha = 0.05f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Boutons d'actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bouton de traduction
                if (post.originalLanguage != null && post.originalLanguage != "fr") {
                    AssistChip(
                        onClick = {
                            if (post.isTranslated) {
                                showTranslation = !showTranslation
                            } else {
                                onTranslateClick()
                            }
                        },
                        label = {
                            Text(
                                text = when {
                                    isTranslating -> "Traduction..."
                                    post.isTranslated && showTranslation -> "Masquer traduction"
                                    post.isTranslated -> "Voir traduction"
                                    else -> "Traduire"
                                }
                            )
                        },
                        leadingIcon = {
                            if (isTranslating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = TranslationIndicator.copy(alpha = 0.1f),
                            labelColor = TranslationIndicator
                        ),
                        enabled = !isTranslating
                    )
                }
                
                // Bouton TTS pour l'accessibilité
                if (isAccessibilityModeEnabled) {
                    AssistChip(
                        onClick = {
                            if (isSpeaking) {
                                onStopSpeaking()
                            } else {
                                onSpeakClick()
                            }
                        },
                        label = {
                            Text(
                                text = if (isSpeaking) "Arrêter" else "Écouter"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isSpeaking) {
                                SpeechActiveColor.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            labelColor = if (isSpeaking) {
                                SpeechActiveColor
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "À l'instant"
        diff < 3600_000 -> "${diff / 60_000} min"
        diff < 86400_000 -> "${diff / 3600_000} h"
        else -> {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
            formatter.format(Date(timestamp))
        }
    }
}


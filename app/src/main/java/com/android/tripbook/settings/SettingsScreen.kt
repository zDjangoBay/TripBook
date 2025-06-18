package com.android.tripbook.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.preferences.TextSize
import com.android.tripbook.preferences.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userPreferences: UserPreferences,
    onDarkModeToggle: () -> Unit,
    onTextSizeChange: (TextSize) -> Unit,
    onAccessibilityModeToggle: () -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Paramètres",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            SettingsSection(title = "Apparence") {
                // Mode sombre
                SettingsItem(
                    icon = if (userPreferences.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    title = "Mode sombre",
                    subtitle = if (userPreferences.isDarkMode) "Activé" else "Désactivé"
                ) {
                    Switch(
                        checked = userPreferences.isDarkMode,
                        onCheckedChange = { onDarkModeToggle() }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Taille du texte
                TextSizeSelector(
                    currentTextSize = userPreferences.textSize,
                    onTextSizeChange = onTextSizeChange
                )
            }
        }
        
        item {
            SettingsSection(title = "Accessibilité") {
                // Mode accessibilité
                SettingsItem(
                    icon = Icons.Default.Accessibility,
                    title = "Mode accessibilité",
                    subtitle = "Audio et indicateurs visuels pour malvoyants"
                ) {
                    Switch(
                        checked = userPreferences.isAccessibilityModeEnabled,
                        onCheckedChange = { onAccessibilityModeToggle() }
                    )
                }
                
                if (userPreferences.isAccessibilityModeEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Vitesse de lecture
                    SpeechRateSlider(
                        currentRate = userPreferences.speechRate,
                        onRateChange = onSpeechRateChange
                    )
                }
            }
        }
        
        item {
            SettingsSection(title = "Traduction") {
                SettingsItem(
                    icon = Icons.Default.Translate,
                    title = "Traduction automatique",
                    subtitle = "Détecter et traduire les posts en langues étrangères"
                ) {
                    Switch(
                        checked = userPreferences.autoTranslateEnabled,
                        onCheckedChange = { /* TODO: implement */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        action()
    }
}

@Composable
fun TextSizeSelector(
    currentTextSize: TextSize,
    onTextSizeChange: (TextSize) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TextFields,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "Taille du texte",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(start = 40.dp)
        ) {
            TextSize.values().forEach { textSize ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (currentTextSize == textSize),
                            onClick = { onTextSizeChange(textSize) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (currentTextSize == textSize),
                        onClick = null
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = textSize.displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SpeechRateSlider(
    currentRate: Float,
    onRateChange: (Float) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "Vitesse de lecture",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.padding(start = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lent",
                style = MaterialTheme.typography.labelSmall
            )
            
            Slider(
                value = currentRate,
                onValueChange = onRateChange,
                valueRange = 0.5f..2.0f,
                steps = 5,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )
            
            Text(
                text = "Rapide",
                style = MaterialTheme.typography.labelSmall
            )
        }
        
        Text(
            text = "Vitesse actuelle: ${String.format("%.1f", currentRate)}x",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 40.dp, top = 4.dp)
        )
    }
}


package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * A composable for managing tags
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TagSelectionComponent(
    selectedTags: List<String>,
    onTagsUpdated: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    maxTags: Int = 8,
    suggestedTags: List<String> = listOf("travel", "vacation", "adventure", "tourism", "landscape", "food", "culture", "wildlife")
) {
    var newTag by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Suggested tags that haven't been selected yet
    val availableSuggestedTags = remember(selectedTags) {
        suggestedTags.filter { it !in selectedTags }
    }
    
    // Function to add a new tag
    fun addTag(tag: String) {
        if (tag.isNotBlank() && selectedTags.size < maxTags && tag.trim() !in selectedTags) {
            val updatedTags = selectedTags.toMutableList().apply { add(tag.trim()) }
            onTagsUpdated(updatedTags)
        }
        newTag = ""
    }
    
    Column(modifier = modifier) {
        // Selected tags
        if (selectedTags.isNotEmpty()) {
            Text(
                text = "Tags",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                items(selectedTags) { tag ->
                    TagChip(
                        tag = tag,
                        onRemove = {
                            val updatedTags = selectedTags.toMutableList().apply { remove(tag) }
                            onTagsUpdated(updatedTags)
                        }
                    )
                }
            }
        }
        
        // Add new tag field (only if we haven't reached max)
        if (selectedTags.size < maxTags) {
            OutlinedTextField(
                value = newTag,
                onValueChange = { newTag = it },
                label = { Text(if (selectedTags.isEmpty()) "Add tags" else "Add more tags") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (newTag.isNotBlank()) {
                        IconButton(onClick = { addTag(newTag) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Tag")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        addTag(newTag)
                        keyboardController?.hide()
                    }
                )
            )
            
            // Tag suggestions
            if (availableSuggestedTags.isNotEmpty()) {
                Text(
                    text = "Suggested tags:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableSuggestedTags) { tag ->
                        SuggestedTagChip(
                            tag = tag,
                            onSelect = { addTag(tag) }
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Maximum tags reached (${maxTags})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * A chip representing a selected tag
 */
@Composable
private fun TagChip(
    tag: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove Tag",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * A chip representing a suggested tag
 */
@Composable
private fun SuggestedTagChip(
    tag: String,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = Modifier
            .height(32.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelect)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Tag",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

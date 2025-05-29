// HashtagInput.kt
package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HashtagInput(
    hashtags: String,
    onHashtagsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Hashtags",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = hashtags,
            onValueChange = onHashtagsChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("#travel #adventure #memories") },
            leadingIcon = {
                Icon(
                    Icons.Default.Tag,
                    contentDescription = "Hashtags",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            supportingText = {
                Text(
                    text = "Separate hashtags with spaces",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )
    }
}
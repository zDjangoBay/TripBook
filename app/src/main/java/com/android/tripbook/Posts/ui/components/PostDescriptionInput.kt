package com.android.tripbook.posts.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDescriptionInput(
    description: String,
    onDescriptionChange: (String) -> Unit,
    error: String? = null,
    enabled: Boolean = true,
    focusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier
)
) {
    val haptic = LocalHapticFeedback.current
    Column(modifier = modifier) {
        OutlinedTextField(
            value = description,
            onValueChange = { newText ->
                if (newText.length <= 1000) {
                    onDescriptionChange(newText)
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            },
            label = { Text("Description") },
            placeholder = { Text("Share your travel experience...") },
            minLines = 4,
            maxLines = 8,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Text(
            text = "${description.length}/1000",
            style = MaterialTheme.typography.bodySmall,
            color = when {
                description.length >= 1000 -> MaterialTheme.colorScheme.error
                description.length >= 900 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp),
            textAlign = TextAlign.End
        )
        if (description.length >= 1000) {
            Text(
                text = "Maximum character limit reached",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp),
                textAlign = TextAlign.End
            )
        }
    }
}
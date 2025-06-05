package com.android.tripbook.posts.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashtagInput(
    hashtags: String,
    onHashtagsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = hashtags,
            onValueChange = onHashtagsChange,
            label = { Text("Hashtags") },
            placeholder = { Text("#travel #adventure #photography") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Separate hashtags with spaces or commas",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )

        val parsedHashtags = remember(hashtags) {
            hashtags.split(" ", ",", "\n")
                .map { it.trim().removePrefix("#") }
                .filter { it.isNotBlank() }
        }

        if (parsedHashtags.isNotEmpty()) {
            Text(
                text = "Preview: ${parsedHashtags.joinToString(" ") { "#$it" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHashtagInput() {
    TripBookTheme {
        var hashtags by remember { mutableStateOf("") }
        HashtagInput(hashtags = hashtags, onHashtagsChange = { hashtags = it })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHashtagInputWithText() {
    TripBookTheme {
        var hashtags by remember { mutableStateOf("#paris #eiffeltower, #france adventure") }
        HashtagInput(hashtags = hashtags, onHashtagsChange = { hashtags = it })
    }
}
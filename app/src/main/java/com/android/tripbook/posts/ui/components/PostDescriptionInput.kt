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
fun PostDescriptionInput(
    description: String,
    onDescriptionChange: (String) -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            placeholder = { Text("Share your travel experience...") },
            minLines = 4,
            maxLines = 8,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier.fillMaxWidth()
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostDescriptionInput() {
    TripBookTheme {
        var desc by remember { mutableStateOf("") }
        PostDescriptionInput(description = desc, onDescriptionChange = { desc = it })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostDescriptionInputWithError() {
    TripBookTheme {
        var desc by remember { mutableStateOf("Too short") }
        PostDescriptionInput(description = desc, onDescriptionChange = { desc = it }, error = "Description is too short.")
    }
}
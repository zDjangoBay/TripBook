package com.android.tripbook.posts.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.posts.model.PostVisibility
import com.android.tripbook.ui.theme.TripBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostVisibilitySelector(selectedVisibility: PostVisibility, onVisibilitySelected: (PostVisibility) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedVisibility.name,
            onValueChange = { },
            readOnly = true,
            label = { Text("Visibilité du post") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PostVisibility.entries.forEach { visibility ->
                DropdownMenuItem(
                    text = { Text(visibility.name) },
                    onClick = {
                        onVisibilitySelected(visibility)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostVisibilitySelector() {
    TripBookTheme {
        var selected by remember { mutableStateOf(PostVisibility.PUBLIC) }
        PostVisibilitySelector(selectedVisibility = selected) { newVisibility ->
            selected = newVisibility
        }
    }
}
package com.android.tripbook.posts.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTitleInput(
    title: String,
    onTitleChange: (String) -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Post Title") },
            placeholder = { Text("Enter an engaging title...") },
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
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
            text = "${title.length}/100",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 17.dp, top = 4.dp)
        )
    }
}
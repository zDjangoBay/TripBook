package com.android.tripbook.ui.theme.components.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun CommentMentionTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    mentionSuggestions: List<String>,
    onMentionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var showDropdown by remember { mutableStateOf(false) }
    val currentWord = getCurrentMentionWord(value)

    LaunchedEffect(currentWord) {
        showDropdown = currentWord?.startsWith("@") == true && currentWord.length > 1
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column {
            TextField(
                value = value,
                onValueChange = {
                    onValueChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Write a comment...") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            if (showDropdown) {
                val suggestions = mentionSuggestions.filter {
                    it.contains(currentWord!!.removePrefix("@"), ignoreCase = true)
                }

                DropdownMenu(
                    expanded = suggestions.isNotEmpty(),
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp)
                ) {
                    suggestions.forEach { username ->
                        DropdownMenuItem(
                            onClick = {
                                val newValue = insertMention(value, currentWord!!, username)
                                onValueChange(newValue)
                                onMentionSelected(username)
                                showDropdown = false
                            },
                            text = { Text("@$username") }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Extracts the current word being typed (e.g., "@us" from "hello @us|")
 */
private fun getCurrentMentionWord(value: TextFieldValue): String? {
    val textBeforeCursor = value.text.substring(0, value.selection.start)
    val words = textBeforeCursor.split(" ")
    return words.lastOrNull { it.startsWith("@") }
}

/**
 * Replaces the current mention word with the selected username
 */
private fun insertMention(
    value: TextFieldValue,
    currentWord: String,
    username: String
): TextFieldValue {
    val text = value.text
    val start = text.substring(0, value.selection.start).lastIndexOf(currentWord)
    val end = start + currentWord.length

    val newText = text.replaceRange(start, end, "@$username ")
    val newCursor = start + username.length + 2

    return TextFieldValue(
        text = newText,
        selection = TextRange(newCursor)
    )
}
package com.android.tripbook.ui.theme.components.comments

@Composable
fun CommentInput(
    text: String,
    onTextChange: (String) -> Unit,
    onMentionSelected: (String) -> Unit,
    userSuggestions: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Write a comment...") }
        )

        if (text.contains("@")) {
            DropdownMenu(expanded = true, onDismissRequest = { /* TODO */ }) {
                userSuggestions.forEach { username ->
                    DropdownMenuItem(onClick = { onMentionSelected(username) }) {
                        Text(text = "@$username")
                    }
                }
            }
        }
    }
}

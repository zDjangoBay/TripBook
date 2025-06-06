package com.android.tripbook.ui.theme.components.comments

class CommentViewModel : ViewModel() {

    var commentText by mutableStateOf("")
    var mentionSuggestions by mutableStateOf(listOf<String>())

    fun onCommentChange(newText: String) {
        commentText = newText
        if (newText.contains("@")) {
            val query = newText.substringAfterLast("@")
            fetchUserSuggestions(query)
        }
    }

    private fun fetchUserSuggestions(query: String) {
        // TODO: Make API call to search users with username matching query
        // For now, mock data
        mentionSuggestions = listOf("JohnDoe", "JaneSmith", "TravelGuru")
            .filter { it.contains(query, ignoreCase = true) }
    }

    fun selectMention(username: String) {
        val before = commentText.substringBeforeLast("@")
        commentText = "$before@$username "
        mentionSuggestions = emptyList()
    }
}

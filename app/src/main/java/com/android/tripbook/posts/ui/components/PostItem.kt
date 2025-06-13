package com.android.tripbook.posts.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.ui.components.ReactionBar
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp



@Composable
fun PostItem(post: PostModel) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = post.title, style = MaterialTheme.typography.titleLarge)
        Text(text = post.content, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        ReactionBar(onReact = { emoji ->
            println("Reacted with $emoji to post ${post.id}")
        })
    }
}



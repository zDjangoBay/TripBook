/*
- This composable makes it easy to create consistent section headers across the app,
  improving readability and structure.
 */
package com.android.tripbook.companycatalog.ui.components
import androidx.compose.foundation.layout.fillMaxWidth // Import fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign // Import TextAlign
import androidx.compose.ui.unit.dp
@Composable
fun SectionTitle(title: String, color: Color = Color.Unspecified) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        modifier = Modifier
            .fillMaxWidth() // Ensure text takes full width to center
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center // Center the text
    )
}


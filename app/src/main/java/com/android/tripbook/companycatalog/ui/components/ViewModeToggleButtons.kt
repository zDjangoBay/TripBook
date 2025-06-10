
package com.android.tripbook.companycatalog.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ViewModeToggleButtons(
    isGridView: Boolean,
    onViewModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        IconButton(
            onClick = { onViewModeChange(false) }
        ) {
            Icon(
                Icons.Default.ViewList,
                contentDescription = "List View",
                tint = if (!isGridView) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = { onViewModeChange(true) }
        ) {
            Icon(
                Icons.Default.GridView,
                contentDescription = "Grid View",
                tint = if (isGridView) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

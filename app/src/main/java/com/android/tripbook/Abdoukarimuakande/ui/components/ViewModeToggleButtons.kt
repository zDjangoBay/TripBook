
package com.android.tripbook.Abdoukarimuakande.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ViewModeToggleButtons(
    isGridView: Boolean,
    onViewModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "View:",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Row {
            IconButton(
                onClick = { onViewModeChange(true) }
            ) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "Grid View",
                    tint = if (isGridView) MaterialTheme.colorScheme.primary 
                          else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(
                onClick = { onViewModeChange(false) }
            ) {
                Icon(
                    imageVector = Icons.Default.ViewList,
                    contentDescription = "List View",
                    tint = if (!isGridView) MaterialTheme.colorScheme.primary 
                          else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

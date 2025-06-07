
/*
This composable streamlines view mode selection,
either list or gird form
 */
package com.android.tripbook.companycatalog.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.android.tripbook.companycatalog.ui.catalog.ViewMode

@Composable
fun ViewModeToggleButtons(
    currentViewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        ViewModeButton(
            icon = Icons.Default.GridView,
            contentDescription = "Grid View",
            isSelected = currentViewMode == ViewMode.GRID,
            onClick = { onViewModeChange(ViewMode.GRID) },
            position = ButtonPosition.Start
        )
        
        ViewModeButton(
            icon = Icons.Default.ViewList,
            contentDescription = "List View",
            isSelected = currentViewMode == ViewMode.LIST,
            onClick = { onViewModeChange(ViewMode.LIST) },
            position = ButtonPosition.End
        )
    }
}

@Composable
private fun ViewModeButton(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    position: ButtonPosition,
    modifier: Modifier = Modifier
) {
    val shape = when (position) {
        ButtonPosition.Start -> RoundedCornerShape(
            topStart = 8.dp,
            bottomStart = 8.dp,
            topEnd = 0.dp,
            bottomEnd = 0.dp
        )
        ButtonPosition.End -> RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 0.dp,
            topEnd = 8.dp,
            bottomEnd = 8.dp
        )
    }
    
    if (isSelected) {
        Button(
            onClick = onClick,
            modifier = modifier.size(48.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp)
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.size(48.dp),
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private enum class ButtonPosition {
    Start, End
}

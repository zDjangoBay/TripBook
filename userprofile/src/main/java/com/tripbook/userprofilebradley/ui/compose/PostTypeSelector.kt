package com.tripbook.userprofilebradley.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.tripbook.userprofilebradley.data.PostType

@Composable
fun PostTypeSelector(
    selectedType: PostType,
    onTypeSelected: (PostType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.selectableGroup()
    ) {
        Text(
            text = "Post Type",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PostTypeOption(
                type = PostType.TEXT,
                icon = Icons.Default.Edit,
                label = "Text",
                selected = selectedType == PostType.TEXT,
                onSelect = { onTypeSelected(PostType.TEXT) },
                modifier = Modifier.weight(1f)
            )

            PostTypeOption(
                type = PostType.MEDIA,
                icon = Icons.Default.PhotoCamera,
                label = "Media",
                selected = selectedType == PostType.MEDIA,
                onSelect = { onTypeSelected(PostType.MEDIA) },
                modifier = Modifier.weight(1f)
            )

            PostTypeOption(
                type = PostType.LOCATION,
                icon = Icons.Default.LocationOn,
                label = "Location",
                selected = selectedType == PostType.LOCATION,
                onSelect = { onTypeSelected(PostType.LOCATION) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PostTypeOption(
    type: PostType,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

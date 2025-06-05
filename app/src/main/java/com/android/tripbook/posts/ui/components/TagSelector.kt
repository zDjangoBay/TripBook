package com.android.tripbook.posts.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.model.TagModel
import com.android.tripbook.posts.model.Category
import com.android.tripbook.ui.theme.TripBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelector(
    availableTags: List<TagModel>,
    selectedTags: List<TagModel>,
    onTagToggle: (TagModel) -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    Column(modifier = modifier) {
        Text(
            text = "Tags",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            item {
                FilterChip(
                    onClick = { selectedCategory = null },
                    label = { Text("All") },
                    selected = selectedCategory == null
                )
            }

            items(Category.values()) { category ->
                FilterChip(
                    onClick = { selectedCategory = category },
                    label = { Text(category.displayName) },
                    selected = selectedCategory == category
                )
            }
        }

        val filteredTags = if (selectedCategory == null) {
            availableTags
        } else {
            availableTags.filter { it.category == selectedCategory }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTags) { tag ->
                val isSelected = selectedTags.any { it.id == tag.id }

                FilterChip(
                    onClick = { onTagToggle(tag) },
                    label = { Text(tag.name) },
                    selected = isSelected,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }

        if (selectedTags.isNotEmpty()) {
            Text(
                text = "${selectedTags.size} tags selected",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTagSelector() {
    TripBookTheme {
        val sampleTags = listOf(
            TagModel("t1", "Beach", Category.DESTINATION),
            TagModel("t2", "Mountains", Category.DESTINATION),
            TagModel("t3", "City", Category.DESTINATION),
            TagModel("t4", "Hiking", Category.ACTIVITY),
            TagModel("t5", "Photography", Category.ACTIVITY)
        )
        var selected by remember { mutableStateOf(listOf(sampleTags[0])) }

        TagSelector(
            availableTags = sampleTags,
            selectedTags = selected,
            onTagToggle = { tag ->
                selected = if (selected.contains(tag)) {
                    selected - tag
                } else {
                    selected + tag
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTagSelectorWithError() {
    TripBookTheme {
        val sampleTags = listOf(
            TagModel("t1", "Beach", Category.DESTINATION),
            TagModel("t2", "Mountains", Category.DESTINATION)
        )
        var selected by remember { mutableStateOf(emptyList<TagModel>()) }

        TagSelector(
            availableTags = sampleTags,
            selectedTags = selected,
            onTagToggle = { tag ->
                selected = if (selected.contains(tag)) {
                    selected - tag
                } else {
                    selected + tag
                }
            },
            error = "Please select at least one tag."
        )
    }
}
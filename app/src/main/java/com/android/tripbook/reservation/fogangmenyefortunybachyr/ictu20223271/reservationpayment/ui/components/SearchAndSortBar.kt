package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.model.Reservation
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.animation.AnimationUtils
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.theme.*

/**
 * Sort options for reservations
 */
enum class SortOption(val displayName: String) {
    DATE_ASC("Date (Earliest First)"),
    DATE_DESC("Date (Latest First)"),
    PRICE_ASC("Price (Low to High)"),
    PRICE_DESC("Price (High to Low)"),
    DESTINATION_ASC("Destination (A-Z)"),
    DESTINATION_DESC("Destination (Z-A)")
}

/**
 * Search and sort bar for the reservations dashboard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndSortBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentSortOption: SortOption,
    onSortOptionSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    var showSortOptions by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search icon with animation
            IconButton(
                onClick = {
                    isSearchExpanded = !isSearchExpanded
                    if (isSearchExpanded) {
                        focusRequester.requestFocus()
                    }
                }
            ) {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.then(
                        if (isSearchExpanded)
                            AnimationUtils.pulseAnimation(pulseFraction = 1.1f, duration = 1500)
                        else
                            Modifier
                    )
                )
            }

            // Search field with animation
            AnimatedVisibility(
                visible = isSearchExpanded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = { Text("Search reservations...") },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
            }

            // If search is not expanded, show the sort button
            AnimatedVisibility(
                visible = !isSearchExpanded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort by: ${currentSortOption.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Sort button
            IconButton(
                onClick = { showSortOptions = true }
            ) {
                Icon(
                    imageVector = AppIcons.Sort,
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.then(AnimationUtils.rotateAnimation(degrees = 5f, duration = 2000))
                )
            }
        }
    }

    // Sort options dialog
    if (showSortOptions) {
        SortOptionsDialog(
            currentSortOption = currentSortOption,
            onSortOptionSelected = {
                onSortOptionSelected(it)
                showSortOptions = false
            },
            onDismiss = { showSortOptions = false }
        )
    }
}

/**
 * Dialog for selecting sort options
 */
@Composable
private fun SortOptionsDialog(
    currentSortOption: SortOption,
    onSortOptionSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Sort Reservations",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SortOption.values().forEach { option ->
                    val isSelected = option == currentSortOption

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSortOptionSelected(option) }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                            )
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSortOptionSelected(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = option.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

/**
 * Apply sort option to a list of reservations
 */
fun applySortOption(reservations: List<Reservation>, sortOption: SortOption): List<Reservation> {
    return when (sortOption) {
        SortOption.DATE_ASC -> reservations.sortedBy { it.startDate }
        SortOption.DATE_DESC -> reservations.sortedByDescending { it.startDate }
        SortOption.PRICE_ASC -> reservations.sortedBy { it.price }
        SortOption.PRICE_DESC -> reservations.sortedByDescending { it.price }
        SortOption.DESTINATION_ASC -> reservations.sortedBy { it.destination }
        SortOption.DESTINATION_DESC -> reservations.sortedByDescending { it.destination }
    }
}

/**
 * Apply search query to a list of reservations
 */
fun applySearchQuery(reservations: List<Reservation>, query: String): List<Reservation> {
    if (query.isBlank()) return reservations

    val searchTerms = query.lowercase().split(" ").filter { it.isNotBlank() }

    return reservations.filter { reservation ->
        searchTerms.all { term ->
            reservation.destination.lowercase().contains(term) ||
            reservation.title.lowercase().contains(term) ||
            reservation.accommodationName?.lowercase()?.contains(term) == true ||
            reservation.transportInfo?.lowercase()?.contains(term) == true ||
            reservation.notes?.lowercase()?.contains(term) == true ||
            reservation.bookingReference.lowercase().contains(term)
        }
    }
}

/*
This composable streamlines view mode selection,
either list or gird form
 */
package com.android.tripbook.companycatalog.ui.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List // Import for List icon
import androidx.compose.material.icons.filled.Menu // Import for Menu icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon // Import for Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.Purple700 // Assuming this is your primary app bar color
import com.android.tripbook.ui.theme.Purple40 // Assuming this is another color in your theme (not directly used here but good to keep if in theme)
@Composable
fun ViewModeToggleButtons(
    isListView: Boolean, // true if list view is active, false if card view is active
    onToggleView: (Boolean) -> Unit // Lambda to update the view mode
) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Occupy entire space width
            .padding(horizontal = 16.dp, vertical = 4.dp), // Reduced vertical padding from 8.dp to 4.dp
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Add space between buttons
    ) {
        // List View Button
        Button(
            onClick = { onToggleView(true) }, // Set isListView to true for list view
                    modifier = Modifier.weight(1f), // Make this button take equal width
                    shape = RoundedCornerShape(12.dp), // Rounded corners for the button
                    colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListView) Purple700 else
                        Color.LightGray, // Purple700 for active, Light Gray for inactive
            contentColor = if (isListView) Color.White else Color.Black
// Text/Icon color based on background
                    )
        ) {
            Icon( // Replaced Text with Icon
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "List View"
            )
        }
        // Menu/Card View Button
        Button(
            onClick = { onToggleView(false) }, // Set isListView to false for card view
                    modifier = Modifier.weight(1f), // Make this button take equal width
                    shape = RoundedCornerShape(12.dp), // Rounded corners for the button
                    colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isListView) Purple700 else
                        Color.LightGray, // Purple700 for active, Light Gray for inactive
            contentColor = if (!isListView) Color.White else Color.Black
// Text/Icon color based on background
        )
        ) {
        Icon( // Replaced Text with Icon
            imageVector = Icons.Filled.Menu,
            contentDescription = "Card View"
        )
    }
    }
}

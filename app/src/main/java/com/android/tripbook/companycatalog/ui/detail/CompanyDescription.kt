/*
This composable serves as a visually structured way to present company details,
 */
package com.android.tripbook.companycatalog.ui.detail
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tripbook.companycatalog.ui.components.SectionTitle
import com.example.tripbook.ui.theme.Purple700 // Import Purple700 for heading

@Composable
fun CompanyDescription(description: String) { // Removed profitability parameter
    SectionTitle(title = "About Us", color = Purple700) // Set heading color
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp), // Rounded corners for the container

        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)//Small elevation
    ) {
        Text(
            text = description,
            modifier = Modifier.padding(16.dp) // Padding inside the container
        )
    }
}


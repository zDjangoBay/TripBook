/*
This composable ensures a clean and structured way to present company services,
 */
package com.android.tripbook.companycatalog.ui.detail
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check // Import for tick icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.tripbook.companycatalog.model.CompanyService
import com.android.tripbook.companycatalog.ui.components.SectionTitle
import com.android.tripbook.ui.theme.Purple700 //Purple 700 for tht heading

@Composable
fun CompanyServices(services: List<CompanyService>) {
    SectionTitle(title = "Services Offered", color = Purple700) // Set Heading colour

    Column(modifier = Modifier.fillMaxWidth()) {
        services.forEach { service ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),// padding between cards
                shape = RoundedCornerShape(12.dp), //round corners for the service containers
                elevation = CardDefault.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp) //service card paddings
                            verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check, //tick icon
                        contentDescription = "Service Available",
                        tint = Purple700,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = service.title)
                }
            }
        }
    }
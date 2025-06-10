
package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SafetyTip(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val priority: String
)

data class EmergencyContact(
    val id: String,
    val country: String,
    val police: String,
    val medical: String,
    val fire: String,
    val tourist: String?
)

@Composable
fun SafetyScreen() {
    val safetyTips = remember {
        listOf(
            SafetyTip(
                id = "1",
                title = "Share Your Location",
                description = "Always inform someone about your travel plans and share your live location with trusted contacts.",
                icon = Icons.Default.LocationOn,
                priority = "High"
            ),
            SafetyTip(
                id = "2",
                title = "Keep Copies of Documents",
                description = "Store digital copies of passport, visa, and important documents in cloud storage and email them to yourself.",
                icon = Icons.Default.Description,
                priority = "High"
            ),
            SafetyTip(
                id = "3",
                title = "Emergency Cash",
                description = "Keep emergency cash in multiple currencies hidden in different locations on your person and luggage.",
                icon = Icons.Default.AttachMoney,
                priority = "Medium"
            ),
            SafetyTip(
                id = "4",
                title = "Local Laws & Customs",
                description = "Research local laws, customs, and cultural norms before traveling to avoid unintentional offenses.",
                icon = Icons.Default.Gavel,
                priority = "Medium"
            ),
            SafetyTip(
                id = "5",
                title = "Stay Connected",
                description = "Ensure your phone is always charged and consider carrying a portable charger. Register with your embassy if staying long-term.",
                icon = Icons.Default.PhoneAndroid,
                priority = "High"
            )
        )
    }

    val emergencyContacts = remember {
        listOf(
            EmergencyContact(
                id = "1",
                country = "Cameroon",
                police = "17",
                medical = "18",
                fire = "18",
                tourist = "+237 222 23 40 11"
            ),
            EmergencyContact(
                id = "2",
                country = "Chad",
                police = "17",
                medical = "18",
                fire = "18",
                tourist = "+235 22 52 80 09"
            ),
            EmergencyContact(
                id = "3",
                country = "Central African Republic",
                police = "117",
                medical = "118",
                fire = "118",
                tourist = "+236 21 61 45 66"
            ),
            EmergencyContact(
                id = "4",
                country = "Equatorial Guinea",
                police = "114",
                medical = "115",
                fire = "115",
                tourist = "+240 333 09 26 53"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Safety Center",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        
        Text(
            text = "Stay safe on your adventures",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Emergency SOS Button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Emergency",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Emergency SOS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { /* Trigger emergency protocol */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ACTIVATE EMERGENCY ALERT",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sends location and alert to emergency contacts",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Safety Tips Section
            item {
                Text(
                    text = "Safety Tips",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            items(safetyTips) { tip ->
                SafetyTipCard(tip = tip)
            }

            // Emergency Contacts Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Emergency Contacts by Country",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            items(emergencyContacts) { contact ->
                EmergencyContactCard(contact = contact)
            }

            // Safety Resources
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Additional Resources",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        SafetyResourceItem(
                            title = "Travel Insurance",
                            description = "Always have comprehensive travel insurance",
                            icon = Icons.Default.Security
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        SafetyResourceItem(
                            title = "Local Embassy",
                            description = "Register with your embassy for long stays",
                            icon = Icons.Default.AccountBalance
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        SafetyResourceItem(
                            title = "Health Precautions",
                            description = "Check vaccination requirements and health advisories",
                            icon = Icons.Default.LocalHospital
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SafetyTipCard(tip: SafetyTip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when(tip.priority) {
                        "High" -> Color(0xFFFFEBEE)
                        "Medium" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE8F5E8)
                    }
                )
            ) {
                Icon(
                    tip.icon,
                    contentDescription = tip.title,
                    tint = when(tip.priority) {
                        "High" -> Color(0xFFD32F2F)
                        "Medium" -> Color(0xFFFF9800)
                        else -> Color(0xFF4CAF50)
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = tip.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when(tip.priority) {
                                "High" -> Color(0xFFD32F2F)
                                "Medium" -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                    ) {
                        Text(
                            text = tip.priority,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = tip.description,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun EmergencyContactCard(contact: EmergencyContact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = contact.country,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EmergencyNumber("🚓 Police", contact.police)
                EmergencyNumber("🏥 Medical", contact.medical)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EmergencyNumber("🚒 Fire", contact.fire)
                if (contact.tourist != null) {
                    EmergencyNumber("ℹ️ Tourist", contact.tourist)
                }
            }
        }
    }
}

@Composable
fun EmergencyNumber(label: String, number: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = number,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF1976D2)
        )
    }
}

@Composable
fun SafetyResourceItem(
    title: String,
    description: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

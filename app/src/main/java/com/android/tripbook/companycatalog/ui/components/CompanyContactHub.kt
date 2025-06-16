package com.android.tripbook.companycatalog.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme


// Data classes for contact information
data class ContactMethod(
    val id: String,
    val type: ContactType,
    val value: String,
    val label: String,
    val isPrimary: Boolean = false,
    val isAvailable: Boolean = true,
    val responseTime: String? = null
)

data class BusinessHours(
    val dayOfWeek: String,
    val openTime: String,
    val closeTime: String,
    val isOpen: Boolean = true
)

data class ContactPerson(
    val id: String,
    val name: String,
    val title: String,
    val department: String,
    val email: String?,
    val phone: String?,
    val isAvailable: Boolean = true
)

enum class ContactType(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    PHONE("Phone", Icons.Default.Phone, Color(0xFF4CAF50)),
    EMAIL("Email", Icons.Default.Email, Color(0xFF2196F3)),
    WEBSITE("Website", Icons.Default.Language, Color(0xFF9C27B0)),
    WHATSAPP("WhatsApp", Icons.Default.Chat, Color(0xFF25D366)),
    TELEGRAM("Telegram", Icons.Default.Send, Color(0xFF0088CC)),
    FACEBOOK("Facebook", Icons.Default.Facebook, Color(0xFF1877F2)),
    INSTAGRAM("Instagram", Icons.Default.CameraAlt, Color(0xFFE4405F)),
    LINKEDIN("LinkedIn", Icons.Default.Business, Color(0xFF0A66C2)),
    TWITTER("Twitter", Icons.Default.AlternateEmail, Color(0xFF1DA1F2)),
    ADDRESS("Address", Icons.Default.LocationOn, Color(0xFFFF5722))
}

enum class InquiryType(val displayName: String) {
    GENERAL("General Inquiry"),
    QUOTE("Request Quote"),
    SUPPORT("Technical Support"),
    PARTNERSHIP("Partnership"),
    COMPLAINT("Complaint"),
    FEEDBACK("Feedback")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyContactHub(
    companyId: String,
    companyName: String,
    contactMethods: List<ContactMethod> = emptyList(),
    businessHours: List<BusinessHours> = emptyList(),
    contactPersons: List<ContactPerson> = emptyList(),
    onContactMethodClick: (ContactMethod) -> Unit = {},
    onSendMessage: (String, InquiryType, String, String) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var showQuickContact by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            ContactHubHeader(
                companyName = companyName,
                onQuickContact = { showQuickContact = !showQuickContact }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab navigation
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Contact Methods") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Business Hours") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Team") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab content
            when (selectedTab) {
                0 -> ContactMethodsSection(
                    contactMethods = contactMethods,
                    onContactMethodClick = onContactMethodClick
                )
                1 -> BusinessHoursSection(businessHours = businessHours)
                2 -> ContactPersonsSection(contactPersons = contactPersons)
            }

            // Quick contact form
            AnimatedVisibility(
                visible = showQuickContact,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                QuickContactForm(
                    companyId = companyId,
                    onSendMessage = onSendMessage,
                    onDismiss = { showQuickContact = false }
                )
            }
        }
    }
}

@Composable
private fun ContactHubHeader(
    companyName: String,
    onQuickContact: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Contact $companyName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Get in touch with us",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = onQuickContact,
            modifier = Modifier.height(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Quick Contact")
        }
    }
}

@Composable
private fun ContactMethodsSection(
    contactMethods: List<ContactMethod>,
    onContactMethodClick: (ContactMethod) -> Unit
) {
    if (contactMethods.isEmpty()) {
        EmptyContactMethodsState()
    } else {
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contactMethods) { method ->
                ContactMethodCard(
                    contactMethod = method,
                    onClick = { onContactMethodClick(method) }
                )
            }
        }
    }
}

@Composable
private fun ContactMethodCard(
    contactMethod: ContactMethod,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (contactMethod.isPrimary) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Contact method icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(contactMethod.type.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = contactMethod.type.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = contactMethod.type.color
                    )
                }

                // Contact details
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = contactMethod.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        if (contactMethod.isPrimary) {
                            AssistChip(
                                onClick = { },
                                label = { Text("Primary") },
                                modifier = Modifier.height(24.dp),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    labelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Text(
                        text = contactMethod.value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (contactMethod.responseTime != null) {
                        Text(
                            text = "Response time: ${contactMethod.responseTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (contactMethod.isAvailable) {
                            Color(0xFF4CAF50)
                        } else {
                            Color(0xFFF44336)
                        }
                    )
            )
        }
    }
}

@Composable
private fun BusinessHoursSection(businessHours: List<BusinessHours>) {
    if (businessHours.isEmpty()) {
        EmptyBusinessHoursState()
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            businessHours.forEach { hours ->
                BusinessHoursCard(businessHours = hours)
            }
        }
    }
}

@Composable
private fun BusinessHoursCard(businessHours: BusinessHours) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = businessHours.dayOfWeek,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            if (businessHours.isOpen) {
                Text(
                    text = "${businessHours.openTime} - ${businessHours.closeTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Closed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFF44336),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ContactPersonsSection(contactPersons: List<ContactPerson>) {
    if (contactPersons.isEmpty()) {
        EmptyContactPersonsState()
    } else {
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contactPersons) { person ->
                ContactPersonCard(contactPerson = person)
            }
        }
    }
}

@Composable
private fun ContactPersonCard(contactPerson: ContactPerson) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Person avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contactPerson.name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Person details
                Column {
                    Text(
                        text = contactPerson.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = contactPerson.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = contactPerson.department,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // Contact actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (contactPerson.email != null) {
                    IconButton(
                        onClick = { /* Handle email */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                if (contactPerson.phone != null) {
                    IconButton(
                        onClick = { /* Handle phone */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickContactForm(
    companyId: String,
    onSendMessage: (String, InquiryType, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedInquiryType by remember { mutableStateOf(InquiryType.GENERAL) }
    var message by remember { mutableStateOf("") }
    var showInquiryTypeMenu by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Contact Form",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            // Inquiry type dropdown
            Box {
                OutlinedTextField(
                    value = selectedInquiryType.displayName,
                    onValueChange = { },
                    label = { Text("Inquiry Type") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showInquiryTypeMenu = true },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                )
                
                DropdownMenu(
                    expanded = showInquiryTypeMenu,
                    onDismissRequest = { showInquiryTypeMenu = false }
                ) {
                    InquiryType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                selectedInquiryType = type
                                showInquiryTypeMenu = false
                            }
                        )
                    }
                }
            }

            // Message field
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            // Send button
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && message.isNotBlank()) {
                        onSendMessage(name, selectedInquiryType, email, message)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && email.isNotBlank() && message.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Message")
            }
        }
    }
}

@Composable
private fun EmptyContactMethodsState() {
    EmptyState(
        icon = Icons.Default.ContactPhone,
        title = "No contact methods available",
        subtitle = "Contact information will be displayed here"
    )
}

@Composable
private fun EmptyBusinessHoursState() {
    EmptyState(
        icon = Icons.Default.Schedule,
        title = "Business hours not available",
        subtitle = "Operating hours will be displayed here"
    )
}

@Composable
private fun EmptyContactPersonsState() {
    EmptyState(
        icon = Icons.Default.People,
        title = "No team members listed",
        subtitle = "Contact persons will be displayed here"
    )
}

@Composable
private fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyContactHubPreview() {
    TripBookTheme {
        CompanyContactHub(
            companyId = "company_1",
            companyName = "TechSolutions Inc.",
            contactMethods = listOf(
                ContactMethod(
                    id = "contact_1",
                    type = ContactType.PHONE,
                    value = "+1 (555) 123-4567",
                    label = "Main Office",
                    isPrimary = true,
                    responseTime = "Within 2 hours"
                ),
                ContactMethod(
                    id = "contact_2",
                    type = ContactType.EMAIL,
                    value = "info@techsolutions.com",
                    label = "General Inquiries",
                    responseTime = "Within 24 hours"
                ),
                ContactMethod(
                    id = "contact_3",
                    type = ContactType.WHATSAPP,
                    value = "+1 (555) 123-4567",
                    label = "WhatsApp Support",
                    responseTime = "Within 1 hour"
                )
            ),
            businessHours = listOf(
                BusinessHours("Monday", "9:00 AM", "6:00 PM"),
                BusinessHours("Tuesday", "9:00 AM", "6:00 PM"),
                BusinessHours("Wednesday", "9:00 AM", "6:00 PM"),
                BusinessHours("Thursday", "9:00 AM", "6:00 PM"),
                BusinessHours("Friday", "9:00 AM", "5:00 PM"),
                BusinessHours("Saturday", "", "", false),
                BusinessHours("Sunday", "", "", false)
            ),
            contactPersons = listOf(
                ContactPerson(
                    id = "person_1",
                    name = "John Smith",
                    title = "Sales Manager",
                    department = "Sales",
                    email = "john.smith@techsolutions.com",
                    phone = "+1 (555) 123-4567"
                ),
                ContactPerson(
                    id = "person_2",
                    name = "Sarah Johnson",
                    title = "Technical Lead",
                    department = "Engineering",
                    email = "sarah.johnson@techsolutions.com",
                    phone = "+1 (555) 123-4568"
                )
            )
        )
    }
}

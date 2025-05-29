package com.android.tripbook.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelerInfoStep(
    adultCount: Int,
    childCount: Int,
    contactName: String,
    contactEmail: String,
    contactPhone: String,
    specialRequirements: String,
    onInfoUpdated: (Int, Int, String, String, String, String) -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit
) {
    var adults by remember { mutableStateOf(adultCount) }
    var children by remember { mutableStateOf(childCount) }
    var name by remember { mutableStateOf(contactName) }
    var email by remember { mutableStateOf(contactEmail) }
    var phone by remember { mutableStateOf(contactPhone) }
    var requirements by remember { mutableStateOf(specialRequirements) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Traveler Information",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Number of Travelers
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Number of Travelers",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Adults
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Adults", style = MaterialTheme.typography.bodyLarge)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (adults > 1) adults-- },
                            enabled = adults > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease adults")
                        }
                        Text(adults.toString(), style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = { adults++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase adults")
                        }
                    }
                }

                // Children
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Children", style = MaterialTheme.typography.bodyLarge)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (children > 0) children-- },
                            enabled = children > 0
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease children")
                        }
                        Text(children.toString(), style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = { children++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase children")
                        }
                    }
                }
            }
        }

        // Contact Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Contact Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }

        // Special Requirements
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Special Requirements",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = requirements,
                    onValueChange = { requirements = it },
                    label = { Text("Any special requests or requirements?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    onInfoUpdated(adults, children, name, email, phone, requirements)
                    onPreviousStep()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    onInfoUpdated(adults, children, name, email, phone, requirements)
                    onNextStep()
                },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()
            ) {
                Text("Continue")
            }
        }
    }
}

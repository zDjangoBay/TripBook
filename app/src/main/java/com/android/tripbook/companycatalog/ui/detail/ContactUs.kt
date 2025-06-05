/*
This composable streamlines user contact interactions with one-tap actions,
 */
package com.android.tripbook.companycatalog.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.compose.ui.unit.dp
import com.android.tripbook.companycatalog.model.CompanyContact
import com.android.tripbook.companycatalog.ui.components.SectionTitle
import com.android.tripbook.ui.theme.Purple700 // Import Purple700 for heading and button colors
import androidx.core.net.toUri

@Composable
fun ContactUs(contacts: List<CompanyContact>) {
    val context = LocalContext.current // Get the context for launching intents

    SectionTitle(title = "Contact Us", color = Purple700) // Set heading color

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly, // Distribute buttons evenly
        verticalAlignment = Alignment.CenterVertically
    ) {
        val emailContact = contacts.find { it.type == "Email" }
        val phoneContact = contacts.find { it.type == "Phone" }

        emailContact?.let { contact ->
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:${contact.value}".toUri()
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        // Handle case where no email app is available (e.g., show a Toast)
                        // Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                        println("No email app found") // For debugging in console
                    }
                },
                modifier = Modifier
                    .weight(1f) // Take equal width
                    .padding(horizontal = 4.dp), // Small padding between buttons
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple700, // Button background color
                    contentColor = Color.White // Button content color
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Email")
                }
            }
        }

        phoneContact?.let { contact ->
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${contact.value}")
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        // Handle case where no dialer app is available
                        // Toast.makeText(context, "No phone app found", Toast.LENGTH_SHORT).show()
                        println("No phone app found") // For debugging in console
                    }
                },
                modifier = Modifier
                    .weight(1f) // Take equal width
                    .padding(horizontal = 4.dp), // Small padding between buttons
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple700, // Button background color
                    contentColor = Color.White // Button content color
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Phone")
                }
            }
        }
    }
}
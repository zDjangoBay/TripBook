package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.tripbook.model.JournalEntry
import java.time.LocalDate
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryDialog(
    entry: JournalEntry? = null,
    onDismiss: () -> Unit,
    onSave: (JournalEntry) -> Unit
) {
    var title by remember { mutableStateOf(entry?.title ?: "") }
    var content by remember { mutableStateOf(entry?.content ?: "") }
    var location by remember { mutableStateOf(entry?.location ?: "") }
    var mood by remember { mutableStateOf(entry?.mood ?: "") }
    var weather by remember { mutableStateOf(entry?.weather ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (entry != null) "Edit Journal Entry" else "New Journal Entry",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = mood,
                        onValueChange = { mood = it },
                        label = { Text("Mood") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Face, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = weather,
                        onValueChange = { weather = it },
                        label = { Text("Weather") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.WbSunny, contentDescription = null) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val newEntry = JournalEntry(
                                id = entry?.id ?: UUID.randomUUID().toString(),
                                date = entry?.date ?: LocalDate.now(),
                                title = title,
                                content = content,
                                location = location,
                                mood = mood,
                                weather = weather
                            )
                            onSave(newEntry)
                        },
                        enabled = title.isNotEmpty() && content.isNotEmpty()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

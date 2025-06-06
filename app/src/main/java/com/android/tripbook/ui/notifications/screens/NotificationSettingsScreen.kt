package com.android.tripbook.ui.notifications.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pushNotifications by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(true) }
    var bookingNotifications by remember { mutableStateOf(true) }
    var paymentNotifications by remember { mutableStateOf(true) }
    var reminderNotifications by remember { mutableStateOf(true) }
    var marketingNotifications by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = "Paramètres de notification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour"
                    )
                }
            }
        )

        // Contenu défilable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section Général
            SettingsSection(
                title = "Général",
                description = "Choisissez comment recevoir vos notifications"
            ) {
                SettingsSwitch(
                    title = "Notifications push",
                    description = "Recevoir des notifications sur votre appareil",
                    checked = pushNotifications,
                    onCheckedChange = { pushNotifications = it }
                )

                SettingsSwitch(
                    title = "Notifications par email",
                    description = "Recevoir des notifications par email",
                    checked = emailNotifications,
                    onCheckedChange = { emailNotifications = it }
                )
            }

            // Section Types de notifications
            SettingsSection(
                title = "Types de notifications",
                description = "Choisissez les notifications que vous voulez recevoir"
            ) {
                SettingsSwitch(
                    title = "Réservations",
                    description = "Confirmations, modifications et annulations",
                    checked = bookingNotifications,
                    onCheckedChange = { bookingNotifications = it }
                )

                SettingsSwitch(
                    title = "Paiements",
                    description = "Confirmations de paiement et remboursements",
                    checked = paymentNotifications,
                    onCheckedChange = { paymentNotifications = it }
                )

                SettingsSwitch(
                    title = "Rappels de voyage",
                    description = "Rappels avant vos départs",
                    checked = reminderNotifications,
                    onCheckedChange = { reminderNotifications = it }
                )
            }

            // Section Marketing
            SettingsSection(
                title = "Promotions",
                description = "Offres spéciales et nouveautés"
            ) {
                SettingsSwitch(
                    title = "Offres promotionnelles",
                    description = "Recevoir nos offres et promotions",
                    checked = marketingNotifications,
                    onCheckedChange = { marketingNotifications = it }
                )
            }

            // Bouton de sauvegarde
            Button(
                onClick = {
                    // TODO: Sauvegarder les préférences
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Sauvegarder les préférences")
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        )
    }
}
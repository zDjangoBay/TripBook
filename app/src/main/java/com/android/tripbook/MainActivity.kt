package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.notifications.managers.NotificationTemplateManager
import com.android.tripbook.notifications.models.NotificationType
import com.android.tripbook.notifications.services.NotificationService
import com.android.tripbook.notifications.utils.NotificationUtils
import com.android.tripbook.ui.notifications.screens.NotificationScreen
import com.android.tripbook.ui.notifications.screens.NotificationSettingsScreen
import com.android.tripbook.ui.notifications.viewmodels.NotificationViewModel
import com.android.tripbook.ui.theme.TripBookTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var notificationService: NotificationService
    private lateinit var templateManager: NotificationTemplateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize notification components
        templateManager = NotificationTemplateManager(this)
        notificationService = NotificationService(this, templateManager)

        // Test notifications on app start
        testNotifications()

        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                TripBookApp(
                    notificationService = notificationService,
                    templateManager = templateManager
                )
            }
        }
    }

    private fun testNotifications() {
        lifecycleScope.launch {
            // Test booking confirmation
            val bookingData = mapOf(
                "destination" to "Douala",
                "date" to "25 Juin 2025",
                "transport" to "Bus",
                "departure_time" to "08:00",
                "seat_number" to "A12"
            )

            val bookingNotification = NotificationUtils.createBookingConfirmation(
                userId = "user_123",
                destination = bookingData["destination"] as String,
                date = bookingData["date"] as String,
                transport = bookingData["transport"] as String
            )

            notificationService.processNotification(bookingNotification, bookingData)
        }

        lifecycleScope.launch {
            // Test payment success
            val paymentData = mapOf(
                "amount" to "25,000 FCFA",
                "payment_method" to "Mobile Money",
                "transaction_id" to "TXN123456789",
                "date" to "23 Juin 2025"
            )

            val paymentNotification = NotificationUtils.createPaymentSuccess(
                userId = "user_123",
                amount = paymentData["amount"] as String,
                paymentMethod = paymentData["payment_method"] as String
            )

            notificationService.processNotification(paymentNotification, paymentData)
        }

        lifecycleScope.launch {
            // Test trip reminder
            val reminderData = mapOf(
                "destination" to "Yaoundé",
                "departure_time" to "08:00",
                "departure_location" to "Gare Routière Mvan",
                "seat_number" to "B05"
            )

            val reminderNotification = NotificationUtils.createTripReminder(
                userId = "user_123",
                destination = reminderData["destination"] as String,
                departureTime = reminderData["departure_time"] as String
            )

            notificationService.processNotification(reminderNotification, reminderData)
        }

        lifecycleScope.launch {
            // Test booking modification
            val modificationData = mapOf(
                "destination" to "Bamenda",
                "old_date" to "24 Juin 2025",
                "new_date" to "26 Juin 2025",
                "reason" to "Changement d'horaire demandé"
            )

            val modificationNotification = NotificationUtils.createBookingModification(
                userId = "user_123",
                bookingId = "BK789456",
                changes = "Date modifiée"
            )

            notificationService.processNotification(modificationNotification, modificationData)
        }

        lifecycleScope.launch {
            // Test refund processed
            val refundData = mapOf(
                "amount" to "15,000 FCFA",
                "original_amount" to "25,000 FCFA",
                "refund_reason" to "Annulation voyage",
                "processing_time" to "3-5 jours ouvrables"
            )

            val refundNotification = NotificationUtils.createRefundProcessed(
                userId = "user_123",
                amount = refundData["amount"] as String,
                transactionId = "REF123456"
            )

            notificationService.processNotification(refundNotification, refundData)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationService.cleanup()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripBookApp(
    notificationService: NotificationService,
    templateManager: NotificationTemplateManager,
    navController: NavHostController = rememberNavController()
) {

    val notificationViewModel: NotificationViewModel = viewModel {
        NotificationViewModel(notificationService, templateManager)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("home") {
                HomeScreen(
                    onNotificationClick = {
                        navController.navigate("notifications")
                    },
                    notificationViewModel = notificationViewModel,
                    templateManager = templateManager
                )
            }

            // Écran des notifications
            composable("notifications") {
                NotificationScreen(
                    viewModel = notificationViewModel,
                    onSettingsClick = {
                        navController.navigate("notification_settings")
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Écran des paramètres de notifications
            composable("notification_settings") {
                NotificationSettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Écran de test des notifications
            composable("test_notifications") {
                TestNotificationScreen(
                    templateManager = templateManager,
                    notificationService = notificationService,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNotificationClick: () -> Unit,
    notificationViewModel: NotificationViewModel,
    templateManager: NotificationTemplateManager
) {
    val uiState by notificationViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TripBook") },
                actions = {
                    BadgedBox(
                        badge = {
                            if (uiState.unreadCount > 0) {
                                Badge {
                                    Text(uiState.unreadCount.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onNotificationClick) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Carte de bienvenue
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Bienvenue sur TripBook ! 🚌",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Votre compagnon de voyage au Cameroun",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            // Bouton notifications
            Button(
                onClick = onNotificationClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voir mes notifications")
                if (uiState.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge {
                        Text(uiState.unreadCount.toString())
                    }
                }
            }

            // Statistiques des notifications
            if (uiState.notifications.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Résumé des notifications",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            NotificationStat(
                                label = "Total",
                                count = uiState.notifications.size
                            )
                            NotificationStat(
                                label = "Non lues",
                                count = uiState.unreadCount
                            )
                            NotificationStat(
                                label = "Lues",
                                count = uiState.notifications.size - uiState.unreadCount
                            )
                        }
                    }
                }
            }

            // Répartition par type de notification
            if (uiState.notifications.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Types de notifications",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val notificationCounts = uiState.notifications.groupBy { it.type }.mapValues { it.value.size }

                        notificationCounts.forEach { (type, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = getNotificationTypeLabel(type),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Message d'information
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🔔 Système de notifications actif",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Les notifications de test ont été générées automatiquement. " +
                                "Vous recevrez des notifications pour vos réservations, paiements et rappels de voyage.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun TestNotificationScreen(
    templateManager: NotificationTemplateManager,
    notificationService: NotificationService,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Test des Notifications",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                // Test notification booking
                val template = templateManager.getTemplate(
                    NotificationType.BOOKING_CONFIRMED,
                    mapOf("destination" to "Douala", "date" to "Demain")
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tester Réservation Confirmée")
        }

        Button(
            onClick = {
                // Test notification payment
                val template = templateManager.getTemplate(
                    NotificationType.PAYMENT_SUCCESS,
                    mapOf("amount" to "15,000 FCFA")
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tester Paiement Réussi")
        }

        Button(
            onClick = {
                // Test notification reminder
                val template = templateManager.getTemplate(
                    NotificationType.TRIP_REMINDER,
                    mapOf("destination" to "Yaoundé")
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tester Rappel de Voyage")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retour")
        }
    }
}

@Composable
fun NotificationStat(
    label: String,
    count: Int
) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

fun getNotificationTypeLabel(type: NotificationType): String {
    return when (type) {
        NotificationType.BOOKING_CONFIRMED -> "Réservation confirmée"
        NotificationType.PAYMENT_SUCCESS -> "Paiement réussi"
        NotificationType.TRIP_REMINDER -> "Rappel de voyage"
        NotificationType.BOOKING_MODIFIED -> "Réservation modifiée"
        NotificationType.BOOKING_CANCELLED -> "Réservation annulée"
        NotificationType.REFUND_PROCESSED -> "Remboursement traité"
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview
@Composable
fun GreetingPreview() {
    TripBookTheme {
        Greeting("TripBook")
    }
}
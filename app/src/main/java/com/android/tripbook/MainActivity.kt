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
import com.android.tripbook.notifications.services.NotificationService
import com.android.tripbook.notifications.utils.NotificationUtils
import com.android.tripbook.ui.notifications.screens.NotificationScreen
import com.android.tripbook.ui.notifications.screens.NotificationSettingsScreen
import com.android.tripbook.ui.notifications.viewmodels.NotificationViewModel
import com.android.tripbook.ui.theme.TripBookTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var notificationService: NotificationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize notification service
        notificationService = NotificationService(this)

        // Test notifications on app start
        testNotifications()

        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                TripBookApp(notificationService = notificationService)
            }
        }
    }

    private fun testNotifications() {
        // Test booking confirmation
        lifecycleScope.launch {
            val bookingNotification = NotificationUtils.createBookingConfirmation(
                userId = "test123",
                destination = "Douala",
                date = "Demain",
                transport = "Bus"
            )
            notificationService.processNotification(bookingNotification)
        }

        // Test payment success
        lifecycleScope.launch {
            val paymentNotification = NotificationUtils.createPaymentSuccess(
                userId = "test123",
                amount = "25,000 FCFA",
                paymentMethod = "Mobile Money"
            )
            notificationService.processNotification(paymentNotification)
        }

        // Test trip reminder
        lifecycleScope.launch {
            val reminderNotification = NotificationUtils.createTripReminder(
                userId = "test123",
                destination = "Yaoundé",
                departureTime = "08:00"
            )
            notificationService.processNotification(reminderNotification)
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
    navController: NavHostController = rememberNavController()
) {
    // Créer le ViewModel avec le service de notification
    val notificationViewModel: NotificationViewModel = viewModel {
        NotificationViewModel(notificationService)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Écran d'accueil
            composable("home") {
                HomeScreen(
                    onNotificationClick = {
                        navController.navigate("notifications")
                    },
                    notificationViewModel = notificationViewModel
                )
            }

            // Écran des notifications
            composable("notifications") {
                NotificationScreen(
                    viewModel = notificationViewModel,
                    onSettingsClick = {
                        navController.navigate("notification_settings")
                    }
                )
            }

            // Écran des paramètres de notification
            composable("notification_settings") {
                NotificationSettingsScreen(
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
    notificationViewModel: NotificationViewModel
) {
    val uiState by notificationViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TripBook") },
                actions = {
                    // Badge de notification avec compteur
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
            // Message de bienvenue
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

            // Bouton pour tester les notifications
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
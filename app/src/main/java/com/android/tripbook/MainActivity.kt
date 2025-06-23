package com.android.tripbook.reservation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.android.tripbook.ui.screens.DashboardActivity
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialisation du système de notifications
        setupNotifications()
        
        // Configuration de l'interface Compose
        setContent {
            TripBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
    
    private fun setupNotifications() {
        // Configuration de base pour les notifications
        Log.d("TripBook", "Initialisation du système de notifications")
        // Ici, vous pouvez ajouter l'initialisation de votre NotificationService
    }
    
    @Composable
    private fun MainScreen() {
        var showDashboard by remember { mutableStateOf(false) }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TripBook",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Button(
                onClick = { 
                    showDashboard = true
                    // Lancer DashboardActivity ou naviguer vers le dashboard
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Commencer")
            }
        }
    }
}
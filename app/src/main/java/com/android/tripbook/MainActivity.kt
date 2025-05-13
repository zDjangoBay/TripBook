package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.datamining.data.repository.DataMiningRepository
import com.android.tripbook.datamining.ui.screen.DataMiningScreen
import com.android.tripbook.datamining.ui.viewmodel.DataMiningViewModel
import com.android.tripbook.ui.theme.ThemeMode
import com.android.tripbook.ui.theme.ThemeViewModel
import com.android.tripbook.ui.theme.TripBookTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

class MainActivity : ComponentActivity() {
    private lateinit var dataMiningViewModel: DataMiningViewModel
    private lateinit var themeViewModel: ThemeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the repository
        val repository = DataMiningRepository()

        // Initialize the ViewModels
        dataMiningViewModel = ViewModelProvider(
            this,
            DataMiningViewModel.Factory(repository)
        )[DataMiningViewModel::class.java]

        themeViewModel = ViewModelProvider(this)[ThemeViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()

            // Determine if dark mode should be used
            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            TripBookTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            onNavigateToDataMining = {
                                navController.navigate("dataMining")
                            },
                            themeViewModel = themeViewModel
                        )
                    }

                    composable("dataMining") {
                        DataMiningScreen(
                            viewModel = dataMiningViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onNavigateToDataMining: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val themeMode by themeViewModel.themeMode.collectAsState()
    var showThemeMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(onClick = { showThemeMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Theme settings"
                    )
                }

                DropdownMenu(
                    expanded = showThemeMenu,
                    onDismissRequest = { showThemeMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Light Theme") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LightMode,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            themeViewModel.updateThemeMode(ThemeMode.LIGHT)
                            showThemeMenu = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Dark Theme") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            themeViewModel.updateThemeMode(ThemeMode.DARK)
                            showThemeMenu = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("System Default") },
                        onClick = {
                            themeViewModel.updateThemeMode(ThemeMode.SYSTEM)
                            showThemeMenu = false
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TripBook",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp)
            )

            // Travel image
            Image(
                painter = painterResource(id = R.drawable.travel_background),
                contentDescription = "Travel background",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "A social network for travelers exploring Africa & beyond",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Button(
                onClick = onNavigateToDataMining
            ) {
                Text("Explore Travel Insights")
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    TripBookTheme {
        HomeScreen(
            onNavigateToDataMining = {},
            themeViewModel = ThemeViewModel()
        )
    }
}
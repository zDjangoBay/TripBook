package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.datamining.data.database.TripBookDatabase
import com.android.tripbook.datamining.data.repository.DataMiningRepository
import com.android.tripbook.datamining.ui.screens.DataMiningScreen
import com.android.tripbook.datamining.ui.viewmodel.DataMiningViewModel
import com.android.tripbook.ui.theme.TripBookTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    private lateinit var dataMiningViewModel: DataMiningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the database from the application
        val database = (application as TripBookApplication).database

        // Initialize the repository
        val repository = DataMiningRepository(
            destinationDao = database.destinationDao(),
            travelPatternDao = database.travelPatternDao(),
            userPreferenceDao = database.userPreferenceDao()
        )

        // Initialize the ViewModel
        dataMiningViewModel = ViewModelProvider(
            this,
            DataMiningViewModel.Factory(repository)
        )[DataMiningViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            onNavigateToDataMining = {
                                navController.navigate("dataMining")
                            }
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
    onNavigateToDataMining: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
        HomeScreen(onNavigateToDataMining = {})
    }
}
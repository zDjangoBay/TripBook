package com.android.tripbook

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.algorithm.AlgorithmScheduler
import com.android.tripbook.ui.theme.TripBookTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    @Inject
    lateinit var algorithmScheduler: AlgorithmScheduler
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        try {
            // Schedule algorithm workers
            algorithmScheduler.scheduleAlgorithmWorkers()
            
            setContent {
                TripBookTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Greeting(
                            name = "TripBook",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during app initialization", e)
            // Show a fallback UI instead of crashing
            setContent {
                TripBookTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Text(
                            text = "Something went wrong. Please try again later.",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )
}

@Preview
@Composable
fun GreetingPreview() {
    TripBookTheme {
        Greeting("Android")
    }
}

package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme
import com.example.tripbook.PostActivity // Ensure this matches your actual package

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                MainScreenWithNavigation()
            }
        }
    }
}
@Composable
fun MainScreenWithNavigation() {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Post") },
                    label = { Text("Post") },
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        val intent = Intent(context, PostActivity::class.java)
                        context.startActivity(intent)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 }
                )
            }
        }
    ) { innerPadding ->
        when (selectedItem) {
            0 -> HomeScreen(Modifier.padding(innerPadding))
            2 -> ProfileScreen(Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun ProfileScreen(padding: Modifier) {

}

@Composable
fun HomeScreen(padding: Modifier) {

}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TripBookTheme {
        MainScreenWithNavigation()
    }
}

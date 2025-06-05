package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.android.tripbook.ui.navigation.WelcomeScreen
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TripBookTheme {
                WelcomeScreen(
                    onGetStarted = {
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // Finish MainActivity so user can't navigate back
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
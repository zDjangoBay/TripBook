package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.android.tripbook.ui.theme.MainContent

/**
 * This is the main activity for the app.
 * @see androidx.activity.ComponentActivity
 *
 */
class MainActivity : ComponentActivity() {
    /**
     *  callback method that initialises the state and contents of the [ComponentActivity]
     *  @param savedInstanceState  [Bundle]  representing a previous state  of the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainContent()
        }
    }
}


package com.android.tripbook.Abdoukarimuakande

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.android.tripbook.Abdoukarimuakande.navigation.AbdoukarimuakandeAppNavigation
import com.android.tripbook.ui.theme.TripBookTheme

class AbdoukarimuakandeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AbdoukarimuakandeAppNavigation()
                }
            }
        }
    }
}

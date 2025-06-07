
/*
Company Catalog Activity for running the company catalog module
 */
package com.android.tripbook.companycatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.companycatalog.controller.NavigationController
import com.android.tripbook.companycatalog.model.CompanyRepository
import com.android.tripbook.companycatalog.navigation.AppNavigation

class CompanyCatalogActivity : ComponentActivity() {
    
    private lateinit var repository: CompanyRepository
    private lateinit var navigationController: NavigationController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize repository
        repository = CompanyRepository()
        
        setContent {
            TripBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    navigationController = NavigationController(navController, repository)
                    
                    AppNavigation(
                        navController = navController,
                        repository = repository
                    )
                }
            }
        }
    }
    
    override fun onBackPressed() {
        if (navigationController.canNavigateBack()) {
            navigationController.navigateBack()
        } else {
            super.onBackPressed()
        }
    }
}

@Composable
fun TripBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TripBookTheme {
        // Preview content can be added here
    }
}

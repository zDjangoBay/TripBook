/*
Company Catalog Activity for running the company catalog module
 */
package com.android.tripbook.companycatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.companycatalog.navigation.AppNavigation

class CompanyCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripBookTheme {
                AppNavigation()
            }
        }
    }
}
/*
    This class should provide a structured and reactive navigation system for the app, linking the
    catalog screen to detailed company pages while handling missing company cases.
 */
package com.android.tripbook.companycatalog.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost // Explicit import
import androidx.navigation.compose.composable // Explicit import
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.tripbook.companycatalog.model.MockCompanyData
import com.android.tripbook.companycatalog.ui.catalog.CompanyCatalogScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.CATALOG_SCREEN
    ) {
        composable(Routes.CATALOG_SCREEN) {
            CompanyCatalogScreen(
                onCompanyClick = { company ->
                    navController.navigate("${Routes.DETAIL_SCREEN}/${company.id}")
                }
            )
        }

        composable(
            route = "${Routes.DETAIL_SCREEN}/{companyId}",
            arguments = listOf(navArgument("companyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getInt("companyId")
            val company = MockCompanyData.companies.find { it.id == companyId
}

            if (company != null) {
                // Pass the onBackClick lambda to CompanyDetailScreen
                CompanyDetailScreen(
                    company = company,
                    onBackClick = {
                        navController.popBackStack() // Navigate back when the back button is clicked
                    }
                )
            } else {
                // TODO: Handle case where company is not found, e.g., show error screen or navigate back
                // For now, let's navigate back if company is null to avoid a blank screen
                        navController.popBackStack()
            }
        }
    }
}
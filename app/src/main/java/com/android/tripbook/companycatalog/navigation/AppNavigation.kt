
package com.android.tripbook.companycatalog.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.companycatalog.ui.catalog.CompanyCatalogScreen
import com.android.tripbook.companycatalog.ui.detail.CompanyDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.COMPANY_CATALOG,
        modifier = modifier
    ) {
        composable(Routes.COMPANY_CATALOG) {
            CompanyCatalogScreen(
                onCompanyClick = { companyId ->
                    navController.navigate(Routes.companyDetail(companyId))
                }
            )
        }
        
        composable(Routes.COMPANY_DETAIL) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
            CompanyDetailScreen(
                companyId = companyId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

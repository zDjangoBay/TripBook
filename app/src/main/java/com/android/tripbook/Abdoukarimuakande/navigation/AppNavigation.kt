
package com.android.tripbook.Abdoukarimuakande.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.Abdoukarimuakande.ui.catalog.CompanyCatalogScreen
import com.android.tripbook.Abdoukarimuakande.ui.detail.CompanyDetailScreen

@Composable
fun AbdoukarimuakandeAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.COMPANY_CATALOG
    ) {
        composable(Routes.COMPANY_CATALOG) {
            CompanyCatalogScreen(
                onCompanyClick = { companyId ->
                    navController.navigate(Routes.companyDetail(companyId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Routes.COMPANY_DETAIL) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId")
            CompanyDetailScreen(
                companyId = companyId ?: "",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}


/*
Controller class for app navigation logic
*/
package com.android.tripbook.companycatalog.controller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.tripbook.companycatalog.ui.catalog.CompanyCatalogScreen
import com.android.tripbook.companycatalog.ui.detail.CompanyDetailScreen
import com.android.tripbook.companycatalog.model.CompanyRepository

class NavigationController(
    private val navController: NavHostController,
    private val repository: CompanyRepository
) {
    
    fun navigateToCompanyDetail(companyId: String) {
        navController.navigate("company_detail/$companyId")
    }
    
    fun navigateBack() {
        navController.popBackStack()
    }
    
    fun navigateToCatalog() {
        navController.navigate("catalog") {
            popUpTo("catalog") { inclusive = true }
        }
    }
    
    @Composable
    fun SetupNavigation() {
        NavHost(
            navController = navController,
            startDestination = "catalog"
        ) {
            composable("catalog") {
                CompanyCatalogScreen(
                    repository = repository,
                    onCompanyClick = { companyId ->
                        navigateToCompanyDetail(companyId)
                    }
                )
            }
            
            composable("company_detail/{companyId}") { backStackEntry ->
                val companyId = backStackEntry.arguments?.getString("companyId")
                companyId?.let { id ->
                    val company = repository.getCompanyById(id)
                    company?.let {
                        CompanyDetailScreen(
                            company = it,
                            repository = repository,
                            onBackClick = { navigateBack() }
                        )
                    }
                }
            }
        }
    }
    
    fun getCurrentRoute(): String? {
        return navController.currentDestination?.route
    }
    
    fun canNavigateBack(): Boolean {
        return navController.previousBackStackEntry != null
    }
}

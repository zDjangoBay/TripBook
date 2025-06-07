
/*
    This class should provide a structured and reactive navigation system for the app, linking the
    catalog screen to detailed company pages while handling missing company cases.
 */
package com.android.tripbook.companycatalog.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.tripbook.companycatalog.model.CompanyRepository
import com.android.tripbook.companycatalog.ui.catalog.CompanyCatalogScreen
import com.android.tripbook.companycatalog.ui.detail.CompanyDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: CompanyRepository
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.CATALOG
    ) {
        composable(NavigationRoutes.CATALOG) {
            CompanyCatalogScreen(
                repository = repository,
                onCompanyClick = { companyId ->
                    // Set the selected company before navigation
                    repository.getCompanyById(companyId)?.let { company ->
                        repository.selectCompany(company)
                        navController.navigate("${NavigationRoutes.COMPANY_DETAIL}/$companyId")
                    }
                }
            )
        }
        
        composable("${NavigationRoutes.COMPANY_DETAIL}/{companyId}") { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId")
            
            if (companyId != null) {
                val selectedCompany by repository.selectedCompany.collectAsState()
                
                // If no company is selected or IDs don't match, try to load it
                LaunchedEffect(companyId) {
                    if (selectedCompany?.id != companyId) {
                        repository.getCompanyById(companyId)?.let { company ->
                            repository.selectCompany(company)
                        }
                    }
                }
                
                selectedCompany?.let { company ->
                    CompanyDetailScreen(
                        company = company,
                        repository = repository,
                        onBackClick = {
                            repository.clearSelection()
                            navController.popBackStack()
                        }
                    )
                } ?: run {
                    // Handle case where company is not found
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            } else {
                // Handle case where companyId is null
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}

object NavigationRoutes {
    const val CATALOG = "catalog"
    const val COMPANY_DETAIL = "company_detail"
}

sealed class NavigationEvent {
    object NavigateBack : NavigationEvent()
    data class NavigateToCompanyDetail(val companyId: String) : NavigationEvent()
    object NavigateToCatalog : NavigationEvent()
}

class NavigationHandler(private val navController: NavHostController) {
    
    fun handleNavigationEvent(event: NavigationEvent) {
        when (event) {
            is NavigationEvent.NavigateBack -> {
                navController.popBackStack()
            }
            is NavigationEvent.NavigateToCompanyDetail -> {
                navController.navigate("${NavigationRoutes.COMPANY_DETAIL}/${event.companyId}")
            }
            is NavigationEvent.NavigateToCatalog -> {
                navController.navigate(NavigationRoutes.CATALOG) {
                    popUpTo(NavigationRoutes.CATALOG) { inclusive = true }
                }
            }
        }
    }
    
    fun canNavigateBack(): Boolean {
        return navController.previousBackStackEntry != null
    }
    
    fun getCurrentRoute(): String? {
        return navController.currentDestination?.route
    }
}

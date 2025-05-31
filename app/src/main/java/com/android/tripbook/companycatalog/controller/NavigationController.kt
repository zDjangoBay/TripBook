/*
Controller class for app navigation logic
*/
package com.android.tripbook.companycatalog.controller
import androidx.navigation.NavHostController
import com.android.tripbook.companycatalog.navigation.Routes

object NavigationController {
    fun navigateToDetail(navController: NavHostController, companyId: Int) {
        navController.navigate("${Routes.DETAIL_SCREEN}/$companyId")
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}

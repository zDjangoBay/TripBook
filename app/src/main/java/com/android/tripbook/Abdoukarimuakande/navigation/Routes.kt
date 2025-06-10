
package com.android.tripbook.Abdoukarimuakande.navigation

object Routes {
    const val COMPANY_CATALOG = "abdoukarimuakande_company_catalog"
    const val COMPANY_DETAIL = "abdoukarimuakande_company_detail/{companyId}"
    
    fun companyDetail(companyId: String): String {
        return "abdoukarimuakande_company_detail/$companyId"
    }
}

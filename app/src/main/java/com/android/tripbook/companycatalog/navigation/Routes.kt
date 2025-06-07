
package com.android.tripbook.companycatalog.navigation

object Routes {
    const val COMPANY_CATALOG = "company_catalog"
    const val COMPANY_DETAIL = "company_detail/{companyId}"
    
    fun companyDetail(companyId: String): String {
        return "company_detail/$companyId"
    }
}

/* controller logic
- To provide an interface for accessing company details through an ID,
  without managing database interaction.
*/
package com.android.tripbook.companycatalog.controller
import com.android.tripbook.companycatalog.model.Company
import com.android.tripbook.companycatalog.model.CompanyRepository

object CompanyController {
    fun getCompanyById(id: Int): Company? {
        return CompanyRepository.getCompanyById(id)
    }
}

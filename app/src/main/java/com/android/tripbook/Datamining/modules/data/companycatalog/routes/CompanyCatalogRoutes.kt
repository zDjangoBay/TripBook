package com.android.Tripbook.Datamining.modules.data.companycatalog.routes

import io.ktor.server.routing.*


fun Route.CompanyCatalog(){
    route("/Company"){
        get("/{:company_id}"){// Find a company by his Main id , this is the id of the company itself not managing at the succursale level

        }
        get("/{:id}"){
            // This right here will be about retrieving the information of an agency of a  particular agency
        }
    }
}
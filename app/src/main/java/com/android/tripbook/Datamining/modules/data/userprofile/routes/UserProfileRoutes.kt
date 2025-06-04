package com.android.Tripbook.Datamining.modules.data.userprofile.routes

import io.ktor.server.routing.*


fun Route.UserProfileRoutes(){
    route("/user_registration"){
        get("/all"){

        }
        get("/{:id}"){

        }
    }
}
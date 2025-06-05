package com.android.Tripbook.Datamining.modules.data.posts.routes

import io.ktor.server.routing.*


fun Routing.PostRoutes(){

    route("/posts"){
        get("/{:id}"){// This is the id of the user

        }
        get("/{:username}"){
            // This corresponds to the retrieval of posts depending on the username of a user
        }
    }


}
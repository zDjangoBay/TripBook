package com.android.Tripbook.Datamining.modules.data.reservations.routes

import io.ktor.server.routing.*

fun Route.ReservationRoutes(){
    route("/reservations"){
        get("/all"){

        }

        get("/{:id}"){

        }
    }
}
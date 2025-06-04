package com.android.com.android.Tripbook.Datamining

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 6000, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    configureRouting()
}

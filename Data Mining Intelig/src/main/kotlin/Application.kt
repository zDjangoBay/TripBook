package com.android

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    // The order matters here - install serialization first
    configureSerialization()
    configureRouting()
}

// Main.kt or Application.kt
fun Application.module() {
    routing {
        userProfileRoutes()
    }
}

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

fun Application.module() {
    configureSecurity()
    routing {
        loginRoutes()
        protectedRoutes()
        // other routes...
    }
}

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "TripBook"
            verifier(
                JWT.require(Algorithm.HMAC256("secret_key")) // 🔐 Use .env or secure method
                    .withIssuer("tripbook-server")
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asString().isNotEmpty()) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

object JwtConfig {
    private const val secret = "secret_key" // ❗ Change in production
    private const val issuer = "tripbook-server"
    private const val audience = "tripbook-users"
    private const val validityInMs = 36_000_00 * 10 // 10 hours

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: String): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorithm)
    }

    fun getVerifier(): JWTVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()
}

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)



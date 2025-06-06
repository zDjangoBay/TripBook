package com.android

import com.android.comments.model.CommentService
import com.android.comments.model.CommentServiceImpl
import com.android.comments.model.CommentRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import redis.clients.jedis.Jedis

fun Application.configureRouting() {
    // Install necessary plugins (but NOT ContentNegotiation)
    install(Resources)
    install(RequestValidation) {
        validate<String> { bodyText ->
            if (!bodyText.startsWith("Hello"))
                ValidationResult.Invalid("Body text should start with 'Hello'")
            else ValidationResult.Valid
        }
    }
    
    // Initialize database connections with correct credentials and database name
    val mongoClient = KMongo.createClient("mongodb://MiltonJ23:SpGJx*i6*4vXfwsL@localhost:27017/tripbook")
    val database: CoroutineDatabase = mongoClient.getDatabase("tripbook").coroutine
    val jedis = Jedis(
        environment.config.propertyOrNull("redis.host")?.getString() ?: "localhost",
        environment.config.propertyOrNull("redis.port")?.getString()?.toInt() ?: 6379
    )
    
    // Initialize services
    val jsonConfig = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true 
    }
    
    val commentService: CommentService = CommentServiceImpl(database, jedis, jsonConfig)
    
    // Configure routes
    routing {
        // Basic routes
        get("/") {
            call.respondText("Welcome to TripBook API!")
        }
        
        route("/articles") {
            get {
                val sort = call.request.queryParameters["sort"] ?: "new"
                call.respond("List of articles sorted starting from $sort")
            }
        }
        
        // Module routes
        CommentRoutes(commentService)
    }
}

@Serializable
class Articles(val sort: String? = "new")

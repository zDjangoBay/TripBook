package com.android.Tripbook.Datamining.modules.files

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import java.util.concurrent.TimeUnit

// --- MinIO Client Configuration ---
// IMPORTANT: Securely manage your MinIO credentials.
// Consider using environment variables, Ktor's application.conf, or a secret management system.
// Avoid hardcoding credentials directly in your code for production environments.

// Example: Initialize MinioClient (ideally as a singleton or injected)
// You might want to initialize this in your Ktor application module or a dedicated config class.
val minioClient: MinioClient = MinioClient.builder()
    .endpoint("http://localhost:9000") // TODO: Replace with your MinIO server endpoint
    .credentials("YOUR_MINIO_ACCESS_KEY", "YOUR_MINIO_SECRET_KEY") // TODO: Replace with your MinIO access key and secret key
    .build()

const val BUCKET_NAME = "your-tripbook-bucket" // TODO: Replace with your desired bucket name

/**
 * Generates a presigned URL for uploading a file to MinIO.
 * The client requesting this URL will then use it to perform a PUT request
 * directly to MinIO with the file data in the request body.
 */
fun Route.generatePresignedUploadUrlRoute() {
    get("/generate-upload-url") {
        // The 'objectName' should be the full path within the bucket where the file will be stored.
        // e.g., "posts/image123.jpg", "userprofile/user456/avatar.png"
        val objectName = call.request.queryParameters["objectName"]

        if (objectName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or empty 'objectName' query parameter."))
            return@get
        }

        try {
            // Define the duration for which the presigned URL will be valid
            val expiryDuration = 15 // minutes
            val expiryInSeconds = TimeUnit.MINUTES.toSeconds(expiryDuration.toLong()).toInt()

            val presignedUrlArgs = GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT) // We want to generate a URL for uploading (PUT operation)
                .bucket(BUCKET_NAME)
                .objectName(objectName) // The full path, including any "folders" and the filename
                .expiry(expiryInSeconds)
                .build()

            val presignedUrl = minioClient.getPresignedObjectUrl(presignedUrlArgs)

            // Log successful generation if needed
            // application.log.info("Generated presigned PUT URL for $objectName in bucket $BUCKET_NAME")

            call.respond(HttpStatusCode.OK, mapOf("url" to presignedUrl, "objectName" to objectName, "method" to "PUT"))

        } catch (e: Exception) {
            // It's good practice to log the actual exception on the server side
            application.log.error("Error generating presigned URL for object '$objectName' in bucket '$BUCKET_NAME': ${e.message}", e)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Could not generate upload URL. ${e.localizedMessage}"))
        }
    }
}

// This is the mechanic of file uploading using Minio
fun Route.UploadFilesRoutes() {
    route("/uploadfiles") {
        // Register the route for generating presigned URLs
        generatePresignedUploadUrlRoute()

        // Your existing route structure can remain for organization
        // or to potentially handle metadata after upload.
        route("/posts") {
            // Example:
            // If a client successfully uploads a file using a presigned URL
            // (e.g., to "posts/some-post-id/image.jpg"),
            // it would then call another endpoint here, perhaps:
            // post("/metadata") {
            //    val uploadedFileUrl = call.receive<String>() // The final public/cdn URL of the file
            //    // ... save this URL and other metadata to your database ...
            // }
        }
        route("/userprofile") {
            // Similar logic might apply
        }
        route("/companycatalog") {
            // If there's a need
        }
        route("/tripcatalog") {
            //
        }
    }
}
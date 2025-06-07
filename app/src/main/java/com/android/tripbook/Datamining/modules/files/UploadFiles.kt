package com.android.Tripbook.Datamining.modules.files

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import java.util.concurrent.TimeUnit


val minioClient: MinioClient = MinioClient.builder()
    .endpoint("http://localhost:9000") // This is on my computer but it is to be replaced with an actual minio secure endpoint 
    .credentials("YOUR_MINIO_ACCESS_KEY", "YOUR_MINIO_SECRET_KEY") 
    .build()

const val BUCKET_NAME = "tripbook" 
/**
 * Generates a presigned URL for uploading a file to MinIO.
 * The client requesting this URL will then use it to perform a PUT request
 * directly to MinIO with the file data in the request body.
 */
fun Route.generatePresignedUploadUrlRoute() {
    get("/generate-upload-url") {
        val objectName = call.request.queryParameters["objectName"]

        if (objectName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or empty 'objectName' query parameter."))
            return@get
        }

        try {
          
            val expiryDuration = 15 // minutes
            val expiryInSeconds = TimeUnit.MINUTES.toSeconds(expiryDuration.toLong()).toInt()

            val presignedUrlArgs = GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT) //  This is to generate a Url during the PUT Operation
                .bucket(BUCKET_NAME)
                .objectName(objectName) 
                .expiry(expiryInSeconds)
                .build()

            val presignedUrl = minioClient.getPresignedObjectUrl(presignedUrlArgs)


            call.respond(HttpStatusCode.OK, mapOf("url" to presignedUrl, "objectName" to objectName, "method" to "PUT"))

        } catch (e: Exception) {
            
            application.log.error("Error generating presigned URL for object '$objectName' in bucket '$BUCKET_NAME': ${e.message}", e)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Could not generate upload URL. ${e.localizedMessage}"))
        }
    }
}

// This is the mechanic of file uploading using Minio
fun Route.UploadFilesRoutes() {
    route("/uploadfiles") {
        
        generatePresignedUploadUrlRoute()
        route("/posts") {
            generatePresignedUploadUrlRoute()
        }
        route("/userprofile") {
            generatePresignedUploadUrlRoute()
        }
        route("/companycatalog") {
            generatePresignedUploadUrlRoute()
        }
        route("/tripcatalog") {
            generatePresignedUploadUrlRoute()
        }
    }
}

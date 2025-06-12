package com.android.algorithms.classification.routes

import com.android.algorithms.classification.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.ClassificationRoutes(classificationService: ClassificationService) {

    route("/classification") {

        // Classify single text
        post("/classify") {
            try {
                val request = call.receive<ClassificationRequest>()
                val result = classificationService.classifyText(request)

                if (result != null) {
                    call.respond(
                        HttpStatusCode.Created,
                        ClassificationResponse(
                            success = true,
                            message = "Text classification completed successfully",
                            data = result
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ClassificationResponse(
                            success = false,
                            message = "Failed to classify text",
                            error = "Classification processing failed"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Batch classify multiple texts
        post("/classify/batch") {
            try {
                val request = call.receive<BatchClassificationRequest>()
                val results = classificationService.batchClassifyTexts(request)

                call.respond(
                    HttpStatusCode.Created,
                    ClassificationListResponse(
                        success = true,
                        message = "Batch classification completed",
                        data = results,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid batch request",
                        error = e.message
                    )
                )
            }
        }

        // Predict category without storing
        post("/predict") {
            try {
                val requestBody = call.receive<Map<String, String>>()
                val text = requestBody["text"] ?: throw IllegalArgumentException("Text is required")
                val modelTypeStr = requestBody["modelType"] ?: "GENERAL"
                val modelType = ClassificationModelType.valueOf(modelTypeStr.uppercase())

                val predictions = classificationService.predictCategory(text, modelType)

                call.respond(
                    HttpStatusCode.OK,
                    PredictionResponse(
                        success = true,
                        message = "Category prediction completed",
                        data = predictions,
                        modelType = modelType.name
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid prediction request",
                        error = e.message
                    )
                )
            }
        }

        // Get classification by ID
        get("/result/{id}") {
            try {
                val classificationId = call.parameters["id"] ?: throw IllegalArgumentException("Classification ID is required")
                val result = classificationService.getClassificationById(classificationId)

                if (result != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        ClassificationResponse(
                            success = true,
                            message = "Classification retrieved successfully",
                            data = result
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ClassificationResponse(
                            success = false,
                            message = "Classification not found"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get classifications by text analysis ID
        get("/text-analysis/{textAnalysisId}") {
            try {
                val textAnalysisId = call.parameters["textAnalysisId"] ?: throw IllegalArgumentException("Text analysis ID is required")
                val results = classificationService.getClassificationsByTextAnalysis(textAnalysisId)

                call.respond(
                    HttpStatusCode.OK,
                    ClassificationListResponse(
                        success = true,
                        message = "Classifications retrieved successfully",
                        data = results,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get classifications by user
        get("/user/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val results = classificationService.getClassificationsByUser(userId, page, pageSize)

                call.respond(
                    HttpStatusCode.OK,
                    ClassificationListResponse(
                        success = true,
                        message = "User classifications retrieved successfully",
                        data = results,
                        page = page,
                        pageSize = pageSize,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get classifications by category
        get("/category/{category}") {
            try {
                val category = call.parameters["category"] ?: throw IllegalArgumentException("Category is required")
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val results = classificationService.getClassificationsByCategory(category, page, pageSize)

                call.respond(
                    HttpStatusCode.OK,
                    ClassificationListResponse(
                        success = true,
                        message = "Category classifications retrieved successfully",
                        data = results,
                        page = page,
                        pageSize = pageSize,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get classifications by model type
        get("/model/{modelType}") {
            try {
                val modelTypeStr = call.parameters["modelType"] ?: throw IllegalArgumentException("Model type is required")
                val modelType = ClassificationModelType.valueOf(modelTypeStr.uppercase())
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val results = classificationService.getClassificationsByModelType(modelType, page, pageSize)

                call.respond(
                    HttpStatusCode.OK,
                    ClassificationListResponse(
                        success = true,
                        message = "Model type classifications retrieved successfully",
                        data = results,
                        page = page,
                        pageSize = pageSize,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid model type or request",
                        error = e.message
                    )
                )
            }
        }

        // Get classifications by confidence range
        get("/confidence") {
            try {
                val minConfidence = call.request.queryParameters["min"]?.toDoubleOrNull() ?: 0.0
                val maxConfidence = call.request.queryParameters["max"]?.toDoubleOrNull() ?: 1.0
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val results = classificationService.getClassificationsByConfidence(minConfidence, maxConfidence, page, pageSize)

                call.respond(
                    HttpStatusCode.OK,
                    ClassificationListResponse(
                        success = true,
                        message = "Confidence-based classifications retrieved successfully",
                        data = results,
                        page = page,
                        pageSize = pageSize,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid confidence range or request",
                        error = e.message
                    )
                )
            }
        }

        // Get classification statistics
        get("/stats") {
            try {
                val stats = classificationService.getClassificationStats()

                call.respond(
                    HttpStatusCode.OK,
                    ClassificationStatsResponse(
                        success = true,
                        message = "Statistics retrieved successfully",
                        data = stats
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ClassificationResponse(
                        success = false,
                        message = "Failed to retrieve statistics",
                        error = e.message
                    )
                )
            }
        }

        // Get feature importance for a model
        get("/features/{modelType}") {
            try {
                val modelTypeStr = call.parameters["modelType"] ?: throw IllegalArgumentException("Model type is required")
                val modelType = ClassificationModelType.valueOf(modelTypeStr.uppercase())

                val features = classificationService.getFeatureImportance(modelType)

                call.respond(
                    HttpStatusCode.OK,
                    FeatureImportanceResponse(
                        success = true,
                        message = "Feature importance retrieved successfully",
                        data = features
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid model type or request",
                        error = e.message
                    )
                )
            }
        }

        // Get classification insights
        get("/insights") {
            try {
                val timeframe = call.request.queryParameters["timeframe"] ?: "7d"
                val insights = classificationService.getClassificationInsights(timeframe)

                call.respond(
                    HttpStatusCode.OK,
                    ClassificationInsightsResponse(
                        success = true,
                        message = "Classification insights retrieved successfully",
                        data = insights,
                        timeframe = timeframe
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ClassificationResponse(
                        success = false,
                        message = "Failed to retrieve insights",
                        error = e.message
                    )
                )
            }
        }

        // Get trending categories
        get("/trending/categories") {
            try {
                val timeframe = call.request.queryParameters["timeframe"] ?: "24h"
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

                val trending = classificationService.getTrendingCategories(timeframe, limit)

                call.respond(
                    HttpStatusCode.OK,
                    TrendingCategoriesResponse(
                        success = true,
                        message = "Trending categories retrieved successfully",
                        data = trending,
                        timeframe = timeframe,
                        limit = limit
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ClassificationResponse(
                        success = false,
                        message = "Failed to retrieve trending categories",
                        error = e.message
                    )
                )
            }
        }

        // Train a new model
        post("/train") {
            try {
                val request = call.receive<ModelTrainingRequest>()
                val performance = classificationService.trainModel(request)

                if (performance != null) {
                    call.respond(
                        HttpStatusCode.Created,
                        ModelTrainingResponse(
                            success = true,
                            message = "Model training completed successfully",
                            data = performance
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ClassificationResponse(
                            success = false,
                            message = "Failed to train model",
                            error = "Training process failed"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid training request",
                        error = e.message
                    )
                )
            }
        }

        // Get model performance
        get("/performance/{modelType}") {
            try {
                val modelTypeStr = call.parameters["modelType"] ?: throw IllegalArgumentException("Model type is required")
                val modelType = ClassificationModelType.valueOf(modelTypeStr.uppercase())

                val performance = classificationService.getModelPerformance(modelType)

                if (performance != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        ModelPerformanceResponse(
                            success = true,
                            message = "Model performance retrieved successfully",
                            data = performance
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ClassificationResponse(
                            success = false,
                            message = "Model performance not found"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid model type or request",
                        error = e.message
                    )
                )
            }
        }

        // Update classification (for feedback/correction)
        put("/result/{id}") {
            try {
                val classificationId = call.parameters["id"] ?: throw IllegalArgumentException("Classification ID is required")
                val corrections = call.receive<List<Classification>>()

                val updated = classificationService.updateClassification(classificationId, corrections)

                if (updated != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        ClassificationResponse(
                            success = true,
                            message = "Classification updated successfully",
                            data = updated
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ClassificationResponse(
                            success = false,
                            message = "Classification not found or could not be updated"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid update request",
                        error = e.message
                    )
                )
            }
        }

        // Delete classification
        delete("/result/{id}") {
            try {
                val classificationId = call.parameters["id"] ?: throw IllegalArgumentException("Classification ID is required")
                val deleted = classificationService.deleteClassification(classificationId)

                if (deleted) {
                    call.respond(
                        HttpStatusCode.OK,
                        ClassificationResponse(
                            success = true,
                            message = "Classification deleted successfully"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ClassificationResponse(
                            success = false,
                            message = "Classification not found or could not be deleted"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ClassificationResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }
    }
}

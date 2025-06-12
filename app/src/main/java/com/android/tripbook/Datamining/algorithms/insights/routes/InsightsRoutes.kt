package com.android.algorithms.insights.routes

import com.android.algorithms.insights.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.*

fun Route.InsightsRoutes(insightsService: InsightsService) {

    route("/insights") {

        // Generate insights
        post("/generate") {
            try {
                val request = call.receive<InsightRequest>()
                val insights = insightsService.generateInsights(request)

                call.respond(
                    HttpStatusCode.Created,
                    InsightListResponse(
                        success = true,
                        message = "Insights generated successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get insight by ID
        get("/{id}") {
            try {
                val insightId = call.parameters["id"] ?: throw IllegalArgumentException("Insight ID is required")
                val insight = insightsService.getInsightById(insightId)

                if (insight != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        InsightResponse(
                            success = true,
                            message = "Insight retrieved successfully",
                            data = insight
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        InsightResponse(
                            success = false,
                            message = "Insight not found"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get insights by user
        get("/user/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val insights = insightsService.getInsightsByUser(userId, page, pageSize)

                call.respond(
                    HttpStatusCode.OK,
                    InsightListResponse(
                        success = true,
                        message = "User insights retrieved successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get insights by type
        get("/type/{type}") {
            try {
                val typeStr = call.parameters["type"] ?: throw IllegalArgumentException("Insight type is required")
                val type = InsightType.valueOf(typeStr.uppercase())
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val insights = insightsService.getInsightsByType(type, page, pageSize)

                call.respond(
                    HttpStatusCode.OK,
                    InsightListResponse(
                        success = true,
                        message = "Type-based insights retrieved successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid insight type or request",
                        error = e.message
                    )
                )
            }
        }

        // Get insights by priority
        get("/priority/{priority}") {
            try {
                val priorityStr = call.parameters["priority"] ?: throw IllegalArgumentException("Priority is required")
                val priority = InsightPriority.valueOf(priorityStr.uppercase())
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val insights = insightsService.getInsightsByPriority(priority, page, pageSize)

                call.respond(
                    HttpStatusCode.OK,
                    InsightListResponse(
                        success = true,
                        message = "Priority-based insights retrieved successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid priority or request",
                        error = e.message
                    )
                )
            }
        }

        // Get insights by category
        get("/category/{category}") {
            try {
                val category = call.parameters["category"] ?: throw IllegalArgumentException("Category is required")
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val insights = insightsService.getInsightsByCategory(category, page, pageSize)

                call.respond(
                    HttpStatusCode.OK,
                    InsightListResponse(
                        success = true,
                        message = "Category-based insights retrieved successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get unread insights
        get("/unread/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")
                val insights = insightsService.getUnreadInsights(userId)

                call.respond(
                    HttpStatusCode.OK,
                    InsightListResponse(
                        success = true,
                        message = "Unread insights retrieved successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get actionable insights
        get("/actionable") {
            try {
                val userId = call.request.queryParameters["userId"]
                val insights = insightsService.getActionableInsights(userId)

                call.respond(
                    HttpStatusCode.OK,
                    InsightListResponse(
                        success = true,
                        message = "Actionable insights retrieved successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Mark insight as read
        put("/{id}/read") {
            try {
                val insightId = call.parameters["id"] ?: throw IllegalArgumentException("Insight ID is required")
                val requestBody = call.receive<Map<String, String>>()
                val userId = requestBody["userId"] ?: throw IllegalArgumentException("User ID is required")

                val success = insightsService.markInsightAsRead(insightId, userId)

                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        InsightResponse(
                            success = true,
                            message = "Insight marked as read successfully"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        InsightResponse(
                            success = false,
                            message = "Insight not found or could not be updated"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Get insight statistics
        get("/stats") {
            try {
                val userId = call.request.queryParameters["userId"]
                val stats = insightsService.getInsightStats(userId)

                call.respond(
                    HttpStatusCode.OK,
                    InsightStatsResponse(
                        success = true,
                        message = "Insight statistics retrieved successfully",
                        data = stats
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    InsightResponse(
                        success = false,
                        message = "Failed to retrieve statistics",
                        error = e.message
                    )
                )
            }
        }

        // Send notification
        post("/notify") {
            try {
                val request = call.receive<NotificationRequest>()
                val notification = insightsService.sendNotification(request)

                if (notification != null) {
                    call.respond(
                        HttpStatusCode.Created,
                        NotificationResponse(
                            success = true,
                            message = "Notification sent successfully",
                            data = notification
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        InsightResponse(
                            success = false,
                            message = "Failed to send notification"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid notification request",
                        error = e.message
                    )
                )
            }
        }

        // Get notification history
        get("/notifications/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val notifications = insightsService.getNotificationHistory(userId, page, pageSize)

                call.respond(
                    HttpStatusCode.OK,
                    NotificationHistoryResponse(
                        success = true,
                        message = "Notification history retrieved successfully",
                        data = notifications,
                        page = page,
                        pageSize = pageSize,
                        totalCount = notifications.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Submit feedback
        post("/feedback") {
            try {
                // Receive the feedback data as raw text and parse it manually
                val requestBody = call.receiveText()
                println("Received feedback body: $requestBody") // Debug log

                // Parse JSON manually using kotlinx.serialization
                val json = Json { ignoreUnknownKeys = true }
                val feedbackData = json.parseToJsonElement(requestBody).jsonObject

                // Extract and validate required fields
                val insightId = feedbackData["insightId"]?.jsonPrimitive?.content
                    ?: throw IllegalArgumentException("insightId is required")
                val userId = feedbackData["userId"]?.jsonPrimitive?.content
                    ?: throw IllegalArgumentException("userId is required")
                val rating = feedbackData["rating"]?.jsonPrimitive?.int
                    ?: throw IllegalArgumentException("rating is required")
                val isUseful = feedbackData["isUseful"]?.jsonPrimitive?.boolean
                    ?: throw IllegalArgumentException("isUseful is required")
                val isAccurate = feedbackData["isAccurate"]?.jsonPrimitive?.boolean
                    ?: throw IllegalArgumentException("isAccurate is required")
                val comments = feedbackData["comments"]?.jsonPrimitive?.content
                val actionTaken = feedbackData["actionTaken"]?.jsonPrimitive?.content

                // Create the full feedback object with timestamp
                val feedback = InsightFeedback(
                    insightId = insightId,
                    userId = userId,
                    rating = rating,
                    isUseful = isUseful,
                    isAccurate = isAccurate,
                    comments = comments,
                    actionTaken = actionTaken,
                    submittedAt = java.time.LocalDateTime.now().toString()
                )

                println("Created feedback object: $feedback") // Debug log

                val success = insightsService.submitFeedback(feedback)

                if (success) {
                    call.respond(
                        HttpStatusCode.Created,
                        InsightResponse(
                            success = true,
                            message = "Feedback submitted successfully"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        InsightResponse(
                            success = false,
                            message = "Failed to submit feedback"
                        )
                    )
                }
            } catch (e: Exception) {
                println("Error in feedback endpoint: ${e.message}") // Debug log
                e.printStackTrace() // Full stack trace for debugging
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid feedback request: ${e.message}",
                        error = e.message
                    )
                )
            }
        }

        // Get user configuration
        get("/config/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")
                val config = insightsService.getInsightConfiguration(userId)

                if (config != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        ConfigurationResponse(
                            success = true,
                            message = "Configuration retrieved successfully",
                            data = config
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        InsightResponse(
                            success = false,
                            message = "Configuration not found"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Update user configuration
        put("/config/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")
                val config = call.receive<InsightConfiguration>()

                val success = insightsService.updateInsightConfiguration(userId, config)

                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        InsightResponse(
                            success = true,
                            message = "Configuration updated successfully"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        InsightResponse(
                            success = false,
                            message = "Failed to update configuration"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid configuration request",
                        error = e.message
                    )
                )
            }
        }

        // Analyze trends
        get("/trends/{metric}") {
            try {
                val metric = call.parameters["metric"] ?: throw IllegalArgumentException("Metric is required")
                val timeframe = call.request.queryParameters["timeframe"] ?: "7d"
                val userId = call.request.queryParameters["userId"]

                val analysis = insightsService.analyzeTrends(metric, timeframe, userId)

                if (analysis != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        TrendAnalysisResponse(
                            success = true,
                            message = "Trend analysis completed successfully",
                            data = analysis
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        InsightResponse(
                            success = false,
                            message = "Trend analysis not available"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid trend analysis request",
                        error = e.message
                    )
                )
            }
        }

        // Get user behavior insights
        get("/behavior/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")
                val behaviors = insightsService.getUserBehaviorInsights(userId)

                call.respond(
                    HttpStatusCode.OK,
                    UserBehaviorResponse(
                        success = true,
                        message = "User behavior insights retrieved successfully",
                        data = behaviors
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }

        // Detect anomalies
        get("/anomalies/{metric}") {
            try {
                val metric = call.parameters["metric"] ?: throw IllegalArgumentException("Metric is required")
                val timeframe = call.request.queryParameters["timeframe"] ?: "24h"

                val anomalies = insightsService.detectAnomalies(metric, timeframe)

                call.respond(
                    HttpStatusCode.OK,
                    AnomalyDetectionResponse(
                        success = true,
                        message = "Anomaly detection completed successfully",
                        data = anomalies,
                        metric = metric,
                        timeframe = timeframe
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid anomaly detection request",
                        error = e.message
                    )
                )
            }
        }

        // Get recommendations
        get("/recommendations") {
            try {
                val userId = call.request.queryParameters["userId"]
                val category = call.request.queryParameters["category"]

                val recommendations = insightsService.getRecommendations(userId, category)

                call.respond(
                    HttpStatusCode.OK,
                    RecommendationsResponse(
                        success = true,
                        message = "Recommendations retrieved successfully",
                        data = recommendations
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid recommendations request",
                        error = e.message
                    )
                )
            }
        }

        // Get dashboard data
        get("/dashboard/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")
                val dashboardData = insightsService.getDashboardData(userId)

                call.respond(
                    HttpStatusCode.OK,
                    DashboardResponse(
                        success = true,
                        message = "Dashboard data retrieved successfully",
                        data = dashboardData
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid dashboard request",
                        error = e.message
                    )
                )
            }
        }

        // Process real-time data
        post("/realtime") {
            try {
                val data = call.receive<Map<String, Any>>()
                val insights = insightsService.processRealTimeData(data)

                call.respond(
                    HttpStatusCode.OK,
                    InsightListResponse(
                        success = true,
                        message = "Real-time data processed successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid real-time data request",
                        error = e.message
                    )
                )
            }
        }

        // Get trending patterns
        get("/trending") {
            try {
                val timeframe = call.request.queryParameters["timeframe"] ?: "24h"
                val patterns = insightsService.getTrendingPatterns(timeframe)

                call.respond(
                    HttpStatusCode.OK,
                    TrendingPatternsResponse(
                        success = true,
                        message = "Trending patterns retrieved successfully",
                        data = patterns
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    InsightResponse(
                        success = false,
                        message = "Failed to retrieve trending patterns",
                        error = e.message
                    )
                )
            }
        }

        // Predict trends
        get("/predict/{metric}") {
            try {
                val metric = call.parameters["metric"] ?: throw IllegalArgumentException("Metric is required")
                val timeHorizon = call.request.queryParameters["timeHorizon"] ?: "7d"

                val prediction = insightsService.predictTrends(metric, timeHorizon)

                if (prediction != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        TrendPredictionResponse(
                            success = true,
                            message = "Trend prediction completed successfully",
                            data = prediction
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        InsightResponse(
                            success = false,
                            message = "Trend prediction not available"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid prediction request",
                        error = e.message
                    )
                )
            }
        }

        // Get content quality insights
        get("/content-quality") {
            try {
                val timeframe = call.request.queryParameters["timeframe"] ?: "7d"
                val insights = insightsService.getContentQualityInsights(timeframe)

                call.respond(
                    HttpStatusCode.OK,
                    InsightListResponse(
                        success = true,
                        message = "Content quality insights retrieved successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    InsightResponse(
                        success = false,
                        message = "Failed to retrieve content quality insights",
                        error = e.message
                    )
                )
            }
        }

        // Get engagement predictions
        get("/engagement-predictions") {
            try {
                val userId = call.request.queryParameters["userId"]
                val insights = insightsService.getEngagementPredictions(userId)

                call.respond(
                    HttpStatusCode.OK,
                    InsightListResponse(
                        success = true,
                        message = "Engagement predictions retrieved successfully",
                        data = insights,
                        totalCount = insights.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    InsightResponse(
                        success = false,
                        message = "Failed to retrieve engagement predictions",
                        error = e.message
                    )
                )
            }
        }

        // Delete insight
        delete("/{id}") {
            try {
                val insightId = call.parameters["id"] ?: throw IllegalArgumentException("Insight ID is required")
                val deleted = insightsService.deleteInsight(insightId)

                if (deleted) {
                    call.respond(
                        HttpStatusCode.OK,
                        InsightResponse(
                            success = true,
                            message = "Insight deleted successfully"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        InsightResponse(
                            success = false,
                            message = "Insight not found or could not be deleted"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    InsightResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }
    }
}

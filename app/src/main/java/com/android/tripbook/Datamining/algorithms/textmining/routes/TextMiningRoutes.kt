package com.android.algorithms.textmining.routes

import com.android.algorithms.textmining.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.TextMiningRoutes(textMiningService: TextMiningService) {
    
    route("/text-mining") {
        
        // Analyze single text
        post("/analyze") {
            try {
                val request = call.receive<TextAnalysisRequest>()
                val result = textMiningService.analyzeText(request)
                
                if (result != null) {
                    call.respond(
                        HttpStatusCode.Created,
                        TextMiningResponse(
                            success = true,
                            message = "Text analysis completed successfully",
                            data = result
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        TextMiningResponse(
                            success = false,
                            message = "Failed to analyze text",
                            error = "Analysis processing failed"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TextMiningResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }
        
        // Batch analyze multiple texts
        post("/analyze/batch") {
            try {
                val request = call.receive<BatchTextAnalysisRequest>()
                val results = textMiningService.batchAnalyzeTexts(request)
                
                call.respond(
                    HttpStatusCode.Created,
                    TextMiningListResponse(
                        success = true,
                        message = "Batch text analysis completed",
                        data = results,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TextMiningResponse(
                        success = false,
                        message = "Invalid batch request",
                        error = e.message
                    )
                )
            }
        }
        
        // Get analysis by ID
        get("/analysis/{id}") {
            try {
                val analysisId = call.parameters["id"] ?: throw IllegalArgumentException("Analysis ID is required")
                val result = textMiningService.getAnalysisById(analysisId)
                
                if (result != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        TextMiningResponse(
                            success = true,
                            message = "Analysis retrieved successfully",
                            data = result
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        TextMiningResponse(
                            success = false,
                            message = "Analysis not found"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TextMiningResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }
        
        // Get analyses by source
        get("/analysis/source/{source}/{sourceId}") {
            try {
                val source = call.parameters["source"] ?: throw IllegalArgumentException("Source is required")
                val sourceId = call.parameters["sourceId"] ?: throw IllegalArgumentException("Source ID is required")
                
                val results = textMiningService.getAnalysesBySource(source, sourceId)
                
                call.respond(
                    HttpStatusCode.OK,
                    TextMiningListResponse(
                        success = true,
                        message = "Analyses retrieved successfully",
                        data = results,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TextMiningResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }
        
        // Get analyses by user
        get("/analysis/user/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20
                
                val results = textMiningService.getAnalysesByUser(userId, page, pageSize)
                
                call.respond(
                    HttpStatusCode.OK,
                    TextMiningListResponse(
                        success = true,
                        message = "User analyses retrieved successfully",
                        data = results,
                        page = page,
                        pageSize = pageSize,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TextMiningResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }
        
        // Get analyses by sentiment
        get("/analysis/sentiment/{sentiment}") {
            try {
                val sentimentStr = call.parameters["sentiment"] ?: throw IllegalArgumentException("Sentiment is required")
                val sentiment = SentimentLabel.valueOf(sentimentStr.uppercase())
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20
                
                val results = textMiningService.getAnalysesBySentiment(sentiment, page, pageSize)
                
                call.respond(
                    HttpStatusCode.OK,
                    TextMiningListResponse(
                        success = true,
                        message = "Sentiment analyses retrieved successfully",
                        data = results,
                        page = page,
                        pageSize = pageSize,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TextMiningResponse(
                        success = false,
                        message = "Invalid sentiment or request",
                        error = e.message
                    )
                )
            }
        }
        
        // Search analyses by keywords
        get("/analysis/search") {
            try {
                val keywordsParam = call.request.queryParameters["keywords"] ?: throw IllegalArgumentException("Keywords are required")
                val keywords = keywordsParam.split(",").map { it.trim() }
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20
                
                val results = textMiningService.searchAnalysesByKeywords(keywords, page, pageSize)
                
                call.respond(
                    HttpStatusCode.OK,
                    TextMiningListResponse(
                        success = true,
                        message = "Search results retrieved successfully",
                        data = results,
                        page = page,
                        pageSize = pageSize,
                        totalCount = results.size
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TextMiningResponse(
                        success = false,
                        message = "Invalid search request",
                        error = e.message
                    )
                )
            }
        }
        
        // Get text mining statistics
        get("/stats") {
            try {
                val stats = textMiningService.getTextMiningStats()
                
                call.respond(
                    HttpStatusCode.OK,
                    TextMiningStatsResponse(
                        success = true,
                        message = "Statistics retrieved successfully",
                        data = stats
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    TextMiningResponse(
                        success = false,
                        message = "Failed to retrieve statistics",
                        error = e.message
                    )
                )
            }
        }
        
        // Get trending topics
        get("/trending/topics") {
            try {
                val timeframe = call.request.queryParameters["timeframe"] ?: "24h"
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                
                val topics = textMiningService.getTrendingTopics(timeframe, limit)
                
                call.respond(
                    HttpStatusCode.OK,
                    TrendingTopicsResponse(
                        success = true,
                        message = "Trending topics retrieved successfully",
                        data = topics,
                        timeframe = timeframe,
                        limit = limit
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    TextMiningResponse(
                        success = false,
                        message = "Failed to retrieve trending topics",
                        error = e.message
                    )
                )
            }
        }
        
        // Get sentiment trends
        get("/trending/sentiment") {
            try {
                val timeframe = call.request.queryParameters["timeframe"] ?: "7d"
                
                val trends = textMiningService.getSentimentTrends(timeframe)
                
                call.respond(
                    HttpStatusCode.OK,
                    SentimentTrendsResponse(
                        success = true,
                        message = "Sentiment trends retrieved successfully",
                        data = trends.mapValues { (_, sentimentMap) ->
                            sentimentMap.mapKeys { it.key.name }
                        },
                        timeframe = timeframe
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    TextMiningResponse(
                        success = false,
                        message = "Failed to retrieve sentiment trends",
                        error = e.message
                    )
                )
            }
        }
        
        // Delete analysis
        delete("/analysis/{id}") {
            try {
                val analysisId = call.parameters["id"] ?: throw IllegalArgumentException("Analysis ID is required")
                val deleted = textMiningService.deleteAnalysis(analysisId)
                
                if (deleted) {
                    call.respond(
                        HttpStatusCode.OK,
                        TextMiningResponse(
                            success = true,
                            message = "Analysis deleted successfully"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        TextMiningResponse(
                            success = false,
                            message = "Analysis not found or could not be deleted"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TextMiningResponse(
                        success = false,
                        message = "Invalid request",
                        error = e.message
                    )
                )
            }
        }
    }
}

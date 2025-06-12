# DataMining Algorithm Module

## Overview

The DataMining Algorithm Module is a comprehensive text analysis and machine learning system that provides advanced natural language processing, classification, and insights generation capabilities. This module is designed to process text data, extract meaningful insights, and provide actionable recommendations through a robust REST API.

## Features

### 🔍 Text Mining & Analysis
- **Text Analysis**: Comprehensive text processing including sentiment analysis, entity extraction, and keyword identification
- **Batch Processing**: Efficient processing of multiple texts simultaneously
- **Statistics & Metrics**: Detailed analytics on processed text data
- **Search Capabilities**: Advanced search functionality with keyword-based filtering
- **Trending Topics**: Real-time identification of trending topics and themes

### 🎯 Classification & Prediction
- **Multi-Model Classification**: Support for various classification models (Intent, Sentiment, Spam Detection, etc.)
- **Real-time Prediction**: Instant text classification without storage
- **Feature Analysis**: Detailed feature importance and model interpretability
- **User-based Classification**: Personalized classification results per user
- **Model Performance**: Comprehensive statistics and accuracy metrics

### 💡 Insights & Intelligence
- **Automated Insights Generation**: AI-powered insights from text data
- **Priority-based Insights**: Categorized insights by importance and urgency
- **User-specific Insights**: Personalized insights for individual users
- **Trend Analysis**: Pattern detection and trending insights
- **Anomaly Detection**: Identification of unusual patterns in data
- **Actionable Recommendations**: AI-generated suggestions for improvement

### 📢 Notifications & Feedback
- **Smart Notifications**: Intelligent notification system for important insights
- **Feedback Collection**: User feedback mechanism for continuous improvement
- **Read Status Tracking**: Monitor user engagement with insights

## Architecture

The module is built using:
- **Kotlin** with **Ktor** framework for REST API
- **MongoDB** for data persistence
- **Redis** for caching and real-time data
- **Machine Learning** models for text classification and analysis
- **Natural Language Processing** for text understanding

## Base URL
```
http://localhost:8082
```

## API Routes

### GET Endpoints

| Endpoint | Description | Parameters | Response |
|----------|-------------|------------|----------|
| `/text-mining/stats` | Get text mining statistics | None | Statistics overview |
| `/text-mining/search` | Search analysis by keywords | `keywords`, `page`, `pageSize` | Filtered results |
| `/text-mining/trending` | Get trending topics | `timeframe`, `limit` | Trending topics list |
| `/text-mining/{id}` | Get analysis by ID | `id` (path) | Single analysis result |
| `/classification/stats` | Get classification statistics | None | Classification metrics |
| `/classification/features/{modelType}` | Get feature importance | `modelType` (path) | Feature analysis |
| `/classification/user/{userId}` | Get user classifications | `userId` (path), `page`, `pageSize` | User's classifications |
| `/classification/model/{modelType}` | Get classifications by model | `modelType` (path), `page`, `pageSize` | Model-specific results |
| `/classification/trending/categories` | Get trending categories | `timeframe`, `limit` | Trending categories |
| `/insights/stats` | Get insights statistics | None | Insights overview |
| `/insights/user/{userId}` | Get user insights | `userId` (path), `page`, `pageSize` | User's insights |
| `/insights/type/{type}` | Get insights by type | `type` (path), `page`, `pageSize` | Type-specific insights |
| `/insights/priority/{priority}` | Get insights by priority | `priority` (path), `page`, `pageSize` | Priority-based insights |
| `/insights/unread/{userId}` | Get unread insights | `userId` (path) | Unread insights |
| `/insights/dashboard/{userId}` | Get dashboard data | `userId` (path) | Dashboard metrics |
| `/insights/trending` | Get trending patterns | `timeframe` | Trending patterns |
| `/insights/anomalies/{type}` | Detect anomalies | `type` (path), `timeframe` | Anomaly detection |
| `/insights/recommendations` | Get recommendations | `userId`, `category` | AI recommendations |

### POST Endpoints

| Endpoint | Description | Request Body | Response |
|----------|-------------|--------------|----------|
| `/text-mining/analyze` | Analyze single text | `TextAnalysisRequest` | Analysis result |
| `/text-mining/batch` | Batch text analysis | `BatchAnalysisRequest` | Batch results |
| `/classification/classify` | Classify text (with storage) | `ClassificationRequest` | Classification result |
| `/classification/predict` | Predict category (no storage) | `PredictionRequest` | Prediction result |
| `/insights/generate` | Generate insights | `InsightRequest` | Generated insights |
| `/insights/notify` | Send notification | `NotificationRequest` | Notification result |
| `/insights/feedback` | Submit feedback | `FeedbackRequest` | Feedback confirmation |

### PUT Endpoints

| Endpoint | Description | Request Body | Response |
|----------|-------------|--------------|----------|
| `/insights/{id}/read` | Mark insight as read | `{"userId": "string"}` | Success confirmation |

## Request/Response Examples

### Text Analysis
```json
POST /text-mining/analyze
{
  "text": "I love this amazing product! Best purchase ever!",
  "source": "review",
  "sourceId": "review123",
  "userId": "user123",
  "language": "en"
}
```

### Text Classification
```json
POST /classification/classify
{
  "text": "I want to book a hotel room for next week",
  "modelType": "INTENT_CLASSIFICATION",
  "userId": "user123"
}
```

### Generate Insights
```json
POST /insights/generate
{
  "userId": "user123",
  "timeframe": "7d",
  "categories": ["sentiment", "content_quality"],
  "insightTypes": ["SENTIMENT_TREND", "CONTENT_QUALITY"],
  "priority": "NORMAL"
}
```

### Send Notification
```json
POST /insights/notify
{
  "userId": "user123",
  "insightId": "insight_101",
  "channel": "IN_APP",
  "priority": "HIGH",
  "customMessage": "Important insight detected!"
}
```

### Submit Feedback
```json
POST /insights/feedback
{
  "insightId": "insight_101",
  "userId": "user123",
  "rating": 5,
  "isUseful": true,
  "isAccurate": true,
  "comments": "Very helpful insight!",
  "actionTaken": "Updated content strategy"
}
```

## Model Types

### Classification Models
- `GENERAL` - General purpose classification
- `INTENT_CLASSIFICATION` - Intent detection
- `SENTIMENT_SPECIFIC` - Sentiment analysis
- `SPAM_DETECTION` - Spam identification
- `TOPIC_CLASSIFICATION` - Topic categorization

### Insight Types
- `SENTIMENT_TREND` - Sentiment analysis trends
- `CONTENT_QUALITY` - Content quality assessment
- `ANOMALY_DETECTION` - Unusual pattern detection
- `USER_BEHAVIOR` - User behavior analysis
- `PERFORMANCE_METRICS` - Performance insights

### Priority Levels
- `HIGH` - Critical insights requiring immediate attention
- `NORMAL` - Standard insights for regular review
- `LOW` - Informational insights for reference

## Getting Started

1. **Start the Application**
   ```bash
   ./gradlew run
   ```

2. **Test Basic Functionality**
   ```bash
   curl -X GET http://localhost:8082/text-mining/stats
   ```

3. **Analyze Your First Text**
   ```bash
   curl -X POST http://localhost:8082/text-mining/analyze \
     -H "Content-Type: application/json" \
     -d '{"text":"Hello world!","userId":"test"}'
   ```

## Error Handling

All endpoints return standardized error responses:
```json
{
  "success": false,
  "message": "Error description",
  "error": "Detailed error information"
}
```

## Status Codes
- `200` - Success
- `201` - Created successfully
- `400` - Bad request
- `404` - Resource not found
- `500` - Internal server error

## Building & Running

To build or run the project, use one of the following tasks:

| Task | Description |
|------|-------------|
| `./gradlew test` | Run the tests |
| `./gradlew build` | Build everything |
| `./gradlew run` | Run the server |
| `buildFatJar` | Build an executable JAR |
| `buildImage` | Build the docker image |

If the server starts successfully, you'll see:
```
2025-06-11 13:52:24.568 [main] INFO Application - Application started in 2.468 seconds.
2025-06-11 13:52:25.419 [DefaultDispatcher-worker-2] INFO Application - Responding at http://127.0.0.1:8082
```

## Support

For issues and questions, please refer to the application logs or contact the development team.


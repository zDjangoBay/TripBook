# DataMining Algorithm Module - API Documentation

## Base URL
```
http://localhost:8082
```

## API Endpoints Overview

Based on the Postman collection structure, the API is organized into three main modules:

### 📊 Text Mining Module
- **Purpose**: Text analysis, sentiment detection, entity extraction, and keyword analysis
- **Endpoints**: 5 endpoints (4 GET, 1 POST)

### 🎯 Classification Module  
- **Purpose**: Text classification, prediction, and model analysis
- **Endpoints**: 9 endpoints (7 GET, 2 POST)

### 💡 Insights Module
- **Purpose**: Insights generation, notifications, and feedback collection
- **Endpoints**: 13 endpoints (10 GET, 2 POST, 1 PUT)

---

## 📊 TEXT MINING MODULE

### GET Endpoints

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| `GET` | `/text-mining/stats` | Get text mining statistics | None |
| `GET` | `/text-mining/search` | Search analysis by keywords | `keywords`, `page`, `pageSize` |
| `GET` | `/text-mining/trending` | Get trending topics | `timeframe`, `limit` |
| `GET` | `/text-mining/{id}` | Get analysis by ID | `id` (path parameter) |

### POST Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/text-mining/analyze` | Analyze single text | `TextAnalysisRequest` |
| `POST` | `/text-mining/batch` | Batch text analysis | `BatchAnalysisRequest` |

### Request Examples

#### Analyze Single Text
```http
POST /text-mining/analyze
Content-Type: application/json

{
  "text": "I absolutely love this amazing travel destination! The hotel was fantastic and the food was incredible. Best vacation ever!",
  "source": "post",
  "sourceId": "post123",
  "userId": "user123",
  "language": "en"
}
```

#### Get Text Mining Statistics
```http
GET /text-mining/stats
```

---

## 🎯 CLASSIFICATION MODULE

### GET Endpoints

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| `GET` | `/classification/stats` | Get classification statistics | None |
| `GET` | `/classification/features/{modelType}` | Get feature importance | `modelType` (path) |
| `GET` | `/classification/user/{userId}` | Get user classifications | `userId` (path), `page`, `pageSize` |
| `GET` | `/classification/model/{modelType}` | Get classifications by model | `modelType` (path), `page`, `pageSize` |
| `GET` | `/classification/trending/categories` | Get trending categories | `timeframe`, `limit` |

### POST Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/classification/classify` | Classify text (with storage) | `ClassificationRequest` |
| `POST` | `/classification/predict` | Predict category (no storage) | `PredictionRequest` |

### Request Examples

#### Classify Text
```http
POST /classification/classify
Content-Type: application/json

{
  "text": "I want to book a hotel room for next week",
  "modelType": "INTENT_CLASSIFICATION",
  "userId": "user123"
}
```

#### Predict Category (No Storage)
```http
POST /classification/predict
Content-Type: application/json

{
  "text": "This is spam! Click here to win money now!",
  "modelType": "SPAM_DETECTION"
}
```

#### Get Feature Importance
```http
GET /classification/features/SENTIMENT_SPECIFIC
```

#### Get User Classifications
```http
GET /classification/user/user123?page=1&pageSize=20
```

#### Get Classifications by Model
```http
GET /classification/model/INTENT_CLASSIFICATION?page=1&pageSize=10
```

#### Get Trending Categories
```http
GET /classification/trending/categories?timeframe=24h&limit=10
```

---

## 💡 INSIGHTS MODULE

### GET Endpoints

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| `GET` | `/insights/stats` | Get insights statistics | None |
| `GET` | `/insights/user/{userId}` | Get user insights | `userId` (path), `page`, `pageSize` |
| `GET` | `/insights/type/{type}` | Get insights by type | `type` (path), `page`, `pageSize` |
| `GET` | `/insights/priority/{priority}` | Get insights by priority | `priority` (path), `page`, `pageSize` |
| `GET` | `/insights/unread/{userId}` | Get unread insights | `userId` (path) |
| `GET` | `/insights/dashboard/{userId}` | Get dashboard data | `userId` (path) |
| `GET` | `/insights/trending` | Get trending patterns | `timeframe` |
| `GET` | `/insights/anomalies/{type}` | Detect anomalies | `type` (path), `timeframe` |
| `GET` | `/insights/recommendations` | Get recommendations | `userId`, `category` |

### POST Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/insights/generate` | Generate insights | `InsightRequest` |
| `POST` | `/insights/notify` | Send notification | `NotificationRequest` |
| `POST` | `/insights/feedback` | Submit feedback | `FeedbackRequest` |

### PUT Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `PUT` | `/insights/{id}/read` | Mark insight as read | `{"userId": "string"}` |

### Request Examples

#### Generate Insights
```http
POST /insights/generate
Content-Type: application/json

{
  "userId": "user123",
  "timeframe": "7d",
  "categories": ["sentiment", "content_quality"],
  "insightTypes": ["SENTIMENT_TREND", "CONTENT_QUALITY", "ANOMALY_DETECTION"],
  "priority": "NORMAL"
}
```

#### Send Notification
```http
POST /insights/notify
Content-Type: application/json

{
  "userId": "user123",
  "insightId": "insight_101",
  "channel": "IN_APP",
  "priority": "HIGH",
  "customMessage": "Important insight detected!"
}
```

#### Submit Feedback
```http
POST /insights/feedback
Content-Type: application/json

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

#### Get User Insights
```http
GET /insights/user/user123?page=1&pageSize=20
```

#### Get Insights by Type
```http
GET /insights/type/SENTIMENT_TREND?page=1&pageSize=10
```

#### Get Insights by Priority
```http
GET /insights/priority/HIGH?page=1&pageSize=10
```

#### Get Unread Insights
```http
GET /insights/unread/user123
```

#### Get Dashboard Data
```http
GET /insights/dashboard/user123
```

#### Get Trending Patterns
```http
GET /insights/trending?timeframe=24h
```

#### Detect Anomalies
```http
GET /insights/anomalies/user_activity?timeframe=24h
```

#### Get Recommendations
```http
GET /insights/recommendations?userId=user123&category=content_strategy
```

#### Mark Insight as Read
```http
PUT /insights/insight_101/read
Content-Type: application/json

{
  "userId": "user123"
}
```

---

## Response Format

All endpoints return responses in the following format:

### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { /* response data */ }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "error": "Detailed error information"
}
```

## HTTP Status Codes

| Code | Description |
|------|-------------|
| `200` | OK - Request successful |
| `201` | Created - Resource created successfully |
| `400` | Bad Request - Invalid request data |
| `404` | Not Found - Resource not found |
| `500` | Internal Server Error - Server error |

## Authentication

Currently, the API uses user identification through the `userId` parameter in requests. No additional authentication is required for testing purposes.

## Rate Limiting

No rate limiting is currently implemented, but it's recommended for production use.

## Testing

Use the provided Postman collection "DataMining_Algorithm_Module" to test all endpoints. The collection includes pre-configured requests for all available endpoints.

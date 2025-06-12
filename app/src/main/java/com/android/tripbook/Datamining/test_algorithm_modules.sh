#!/bin/bash

# Algorithm Modules Testing Script
# Base URL for the API
BASE_URL="http://localhost:8082"

echo "🚀 Starting Algorithm Modules Testing..."
echo "Base URL: $BASE_URL"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -e "\n${BLUE}Testing: $description${NC}"
    echo "Endpoint: $method $endpoint"
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$BASE_URL$endpoint")
    fi
    
    # Extract HTTP status code (last line)
    http_code=$(echo "$response" | tail -n1)
    # Extract response body (all but last line)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✅ SUCCESS ($http_code)${NC}"
        echo "Response: $(echo "$body" | head -c 200)..."
    else
        echo -e "${RED}❌ FAILED ($http_code)${NC}"
        echo "Error: $body"
    fi
}

echo -e "\n${YELLOW}=== MODULE 1: TEXT MINING TESTS ===${NC}"

# Test 1: Text Mining Stats (should work with empty data)
test_endpoint "GET" "/text-mining/stats" "" "Get Text Mining Statistics"

# Test 2: Single Text Analysis
test_endpoint "POST" "/text-mining/analyze" '{
  "text": "I love this amazing travel destination! The hotel was fantastic and the food was incredible. Best vacation ever!",
  "source": "post",
  "sourceId": "test123",
  "userId": "user123",
  "language": "en"
}' "Analyze Single Text"

# Test 3: Batch Text Analysis
test_endpoint "POST" "/text-mining/analyze/batch" '{
  "texts": [
    {
      "text": "Great service and amazing food!",
      "source": "review",
      "sourceId": "review1",
      "userId": "user123"
    },
    {
      "text": "Terrible experience, very disappointed.",
      "source": "review",
      "sourceId": "review2",
      "userId": "user123"
    }
  ],
  "priority": "NORMAL"
}' "Batch Text Analysis"

# Test 4: Search by Keywords
test_endpoint "GET" "/text-mining/analysis/search?keywords=travel,vacation&page=1&pageSize=10" "" "Search by Keywords"

# Test 5: Trending Topics
test_endpoint "GET" "/text-mining/trending/topics?timeframe=24h&limit=5" "" "Get Trending Topics"

echo -e "\n${YELLOW}=== MODULE 2: CLASSIFICATION TESTS ===${NC}"

# Test 6: Classification Stats
test_endpoint "GET" "/classification/stats" "" "Get Classification Statistics"

# Test 7: Classify Text - Intent Classification
test_endpoint "POST" "/classification/classify" '{
  "text": "I want to book a hotel room for next week",
  "modelType": "INTENT_CLASSIFICATION",
  "userId": "user123"
}' "Intent Classification"

# Test 8: Classify Text - Sentiment Analysis
test_endpoint "POST" "/classification/classify" '{
  "text": "I absolutely hate this product! Worst purchase ever!",
  "modelType": "SENTIMENT_SPECIFIC",
  "userId": "user123"
}' "Sentiment Classification"

# Test 9: Spam Detection
test_endpoint "POST" "/classification/predict" '{
  "text": "This is spam! Click here to win $1000 now!",
  "modelType": "SPAM_DETECTION"
}' "Spam Detection (Predict Only)"

# Test 10: Content Moderation
test_endpoint "POST" "/classification/classify" '{
  "text": "This is a normal, appropriate comment about travel.",
  "modelType": "CONTENT_MODERATION",
  "userId": "user123"
}' "Content Moderation"

# Test 11: Get Feature Importance
test_endpoint "GET" "/classification/features/SENTIMENT_SPECIFIC" "" "Get Feature Importance"

# Test 12: Batch Classification
test_endpoint "POST" "/classification/classify/batch" '{
  "requests": [
    {
      "text": "I love this!",
      "modelType": "SENTIMENT_SPECIFIC",
      "userId": "user123"
    },
    {
      "text": "How do I cancel my booking?",
      "modelType": "INTENT_CLASSIFICATION",
      "userId": "user123"
    }
  ],
  "priority": "NORMAL"
}' "Batch Classification"

echo -e "\n${YELLOW}=== MODULE 3: INSIGHTS TESTS ===${NC}"

# Test 13: Insights Stats
test_endpoint "GET" "/insights/stats" "" "Get Insights Statistics"

# Test 14: Generate Insights
test_endpoint "POST" "/insights/generate" '{
  "userId": "user123",
  "timeframe": "7d",
  "categories": ["sentiment", "content_quality"],
  "insightTypes": ["SENTIMENT_TREND", "CONTENT_QUALITY"],
  "priority": "NORMAL"
}' "Generate Insights"

# Test 15: Get Trending Patterns
test_endpoint "GET" "/insights/trending?timeframe=24h" "" "Get Trending Patterns"

# Test 16: Detect Anomalies
test_endpoint "GET" "/insights/anomalies/user_activity?timeframe=24h" "" "Detect Anomalies"

# Test 17: Get Recommendations
test_endpoint "GET" "/insights/recommendations?userId=user123" "" "Get Recommendations"

# Test 18: Analyze Trends
test_endpoint "GET" "/insights/trends/sentiment?timeframe=7d&userId=user123" "" "Analyze Trends"

echo -e "\n${YELLOW}=== INTEGRATION TESTS ===${NC}"

# Test 19: User Dashboard
test_endpoint "GET" "/insights/dashboard/user123" "" "Get User Dashboard"

# Test 20: Send Notification
test_endpoint "POST" "/insights/notify" '{
  "userId": "user123",
  "insightId": "test-insight-123",
  "channel": "EMAIL",
  "priority": "NORMAL",
  "customMessage": "Test notification message"
}' "Send Notification"

echo -e "\n${GREEN}=========================================="
echo "🏁 Algorithm Modules Testing Complete!"
echo -e "=========================================${NC}"

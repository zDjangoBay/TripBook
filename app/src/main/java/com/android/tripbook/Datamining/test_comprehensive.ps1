# Comprehensive Test of All Algorithm Modules
$BASE_URL = "http://localhost:8082"

Write-Host "Testing All Algorithm Module Endpoints..." -ForegroundColor Green
Write-Host "Base URL: $BASE_URL" -ForegroundColor Yellow

# Test Classification Endpoints (Previously Failing)
Write-Host "`nTesting Classification Endpoints..." -ForegroundColor Cyan

# Test 11: Predict Category (No Storage)
try {
    $predictData = @{
        text = "This is spam! Click here to win money now!"
        modelType = "SPAM_DETECTION"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/classification/predict" -Method POST -Body $predictData -ContentType "application/json"
    Write-Host "SUCCESS: Predict Category" -ForegroundColor Green
    Write-Host "   Category: $($response.data[0].category)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Predict Category - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 12: Get Feature Importance
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/classification/features/SENTIMENT_SPECIFIC" -Method GET
    Write-Host "SUCCESS: Feature Importance" -ForegroundColor Green
    Write-Host "   Features Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Feature Importance - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 13: Get Classifications by User
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/classification/user/user123?page=1&pageSize=20" -Method GET
    Write-Host "SUCCESS: Classifications by User" -ForegroundColor Green
    Write-Host "   Classifications Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Classifications by User - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 14: Get Classifications by Model Type
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/classification/model/INTENT_CLASSIFICATION?page=1&pageSize=10" -Method GET
    Write-Host "SUCCESS: Classifications by Model" -ForegroundColor Green
    Write-Host "   Classifications Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Classifications by Model - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 15: Get Trending Categories
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/classification/trending/categories?timeframe=24h&limit=10" -Method GET
    Write-Host "SUCCESS: Trending Categories" -ForegroundColor Green
    Write-Host "   Trending Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Trending Categories - $($_.Exception.Message)" -ForegroundColor Red
}

# Test Insights Endpoints (Previously Failing)
Write-Host "`nTesting Insights Endpoints..." -ForegroundColor Cyan

# Test 18: Get User Insights
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/user/user123?page=1&pageSize=20" -Method GET
    Write-Host "SUCCESS: User Insights" -ForegroundColor Green
    Write-Host "   Insights Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: User Insights - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 19: Get Insights by Type
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/type/SENTIMENT_TREND?page=1&pageSize=10" -Method GET
    Write-Host "SUCCESS: Insights by Type" -ForegroundColor Green
    Write-Host "   Insights Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Insights by Type - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 20: Get Insights by Priority
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/priority/HIGH?page=1&pageSize=10" -Method GET
    Write-Host "SUCCESS: Insights by Priority" -ForegroundColor Green
    Write-Host "   Insights Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Insights by Priority - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 21: Get Unread Insights
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/unread/user123" -Method GET
    Write-Host "SUCCESS: Unread Insights" -ForegroundColor Green
    Write-Host "   Unread Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Unread Insights - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 22: Get Dashboard Data
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/dashboard/user123" -Method GET
    Write-Host "SUCCESS: Dashboard Data" -ForegroundColor Green
    Write-Host "   Total Insights: $($response.data.totalInsights)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Dashboard Data - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 23: Get Trending Patterns
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/trending?timeframe=24h" -Method GET
    Write-Host "SUCCESS: Trending Patterns" -ForegroundColor Green
    Write-Host "   Patterns Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Trending Patterns - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 24: Detect Anomalies
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/anomalies/user_activity?timeframe=24h" -Method GET
    Write-Host "SUCCESS: Anomaly Detection" -ForegroundColor Green
    Write-Host "   Anomalies Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Anomaly Detection - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 25: Get Recommendations
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/recommendations?userId=user123&category=content_strategy" -Method GET
    Write-Host "SUCCESS: Recommendations" -ForegroundColor Green
    Write-Host "   Recommendations Count: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Recommendations - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nComprehensive Testing Complete!" -ForegroundColor Green
Write-Host "====================================" -ForegroundColor Yellow

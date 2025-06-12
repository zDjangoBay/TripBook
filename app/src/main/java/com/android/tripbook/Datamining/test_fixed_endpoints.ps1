# Test the Fixed Endpoints
$BASE_URL = "http://localhost:8082"

Write-Host "Testing Fixed Endpoints..." -ForegroundColor Green
Write-Host "Base URL: $BASE_URL" -ForegroundColor Yellow

# Test 1: Insights Notify (Previously 500 Internal Server Error)
Write-Host "`nTesting Insights Notify..." -ForegroundColor Cyan
try {
    $notifyData = @{
        userId = "user123"
        insightId = "insight_101"
        channel = "IN_APP"
        priority = "HIGH"
        customMessage = "Important insight detected!"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/notify" -Method POST -Body $notifyData -ContentType "application/json"
    Write-Host "SUCCESS: Insights Notify" -ForegroundColor Green
    Write-Host "   Message: $($response.message)" -ForegroundColor White
    Write-Host "   Notification ID: $($response.data.id)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Insights Notify - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Insights Feedback (Previously 400 Bad Request)
Write-Host "`nTesting Insights Feedback..." -ForegroundColor Cyan
try {
    $feedbackData = @{
        insightId = "insight_101"
        userId = "user123"
        rating = 5
        isUseful = $true
        isAccurate = $true
        comments = "Very helpful insight!"
        actionTaken = "Updated content strategy"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/feedback" -Method POST -Body $feedbackData -ContentType "application/json"
    Write-Host "SUCCESS: Insights Feedback" -ForegroundColor Green
    Write-Host "   Message: $($response.message)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Insights Feedback - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Verify the fixes work with different data
Write-Host "`nTesting with Different Data..." -ForegroundColor Cyan

# Test Notify with minimal data
try {
    $minimalNotifyData = @{
        userId = "user456"
        insightId = "insight_102"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/notify" -Method POST -Body $minimalNotifyData -ContentType "application/json"
    Write-Host "SUCCESS: Minimal Notify Data" -ForegroundColor Green
    Write-Host "   Notification ID: $($response.data.id)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Minimal Notify Data - $($_.Exception.Message)" -ForegroundColor Red
}

# Test Feedback with minimal data
try {
    $minimalFeedbackData = @{
        insightId = "insight_102"
        userId = "user456"
        rating = 3
        isUseful = $false
        isAccurate = $true
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/feedback" -Method POST -Body $minimalFeedbackData -ContentType "application/json"
    Write-Host "SUCCESS: Minimal Feedback Data" -ForegroundColor Green
    Write-Host "   Message: $($response.message)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Minimal Feedback Data - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nFixed Endpoints Testing Complete!" -ForegroundColor Green
Write-Host "====================================" -ForegroundColor Yellow

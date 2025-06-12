# Quick Algorithm Modules Test Script
$BASE_URL = "http://localhost:8082"

Write-Host "🚀 Testing Algorithm Modules..." -ForegroundColor Green
Write-Host "Base URL: $BASE_URL" -ForegroundColor Yellow

# Test 1: Text Mining Stats
Write-Host "`n📊 Testing Text Mining Stats..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/text-mining/stats" -Method GET
    Write-Host "✅ SUCCESS: Text Mining Stats" -ForegroundColor Green
    Write-Host "   Total Texts: $($response.data.totalTextsProcessed)" -ForegroundColor White
} catch {
    Write-Host "❌ FAILED: Text Mining Stats - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Single Text Analysis
Write-Host "`n🔍 Testing Single Text Analysis..." -ForegroundColor Cyan
$textData = @{
    text = "I love this amazing travel destination! The hotel was fantastic and the food was incredible. Best vacation ever!"
    source = "test"
    sourceId = "test123"
    userId = "user123"
    language = "en"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/text-mining/analyze" -Method POST -Body $textData -ContentType "application/json"
    Write-Host "✅ SUCCESS: Text Analysis" -ForegroundColor Green
    Write-Host "   Sentiment: $($response.data.sentiment.label)" -ForegroundColor White
    $analysisId = $response.data.id
} catch {
    Write-Host "❌ FAILED: Text Analysis - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Classification Stats
Write-Host "`n📈 Testing Classification Stats..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/classification/stats" -Method GET
    Write-Host "✅ SUCCESS: Classification Stats" -ForegroundColor Green
    Write-Host "   Total Classifications: $($response.data.totalClassifications)" -ForegroundColor White
} catch {
    Write-Host "❌ FAILED: Classification Stats - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Text Classification
Write-Host "`n🎯 Testing Text Classification..." -ForegroundColor Cyan
$classifyData = @{
    text = "I want to book a hotel room for next week"
    modelType = "INTENT_CLASSIFICATION"
    userId = "user123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/classification/classify" -Method POST -Body $classifyData -ContentType "application/json"
    Write-Host "✅ SUCCESS: Text Classification" -ForegroundColor Green
    Write-Host "   Category: $($response.data.classifications[0].category)" -ForegroundColor White
} catch {
    Write-Host "❌ FAILED: Text Classification - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Insights Stats
Write-Host "`n💡 Testing Insights Stats..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/stats" -Method GET
    Write-Host "✅ SUCCESS: Insights Stats" -ForegroundColor Green
    Write-Host "   Total Insights: $($response.data.totalInsights)" -ForegroundColor White
} catch {
    Write-Host "❌ FAILED: Insights Stats - $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Generate Insights
Write-Host "`n🔮 Testing Generate Insights..." -ForegroundColor Cyan
$insightsData = @{
    userId = "user123"
    timeframe = "7d"
    categories = @("sentiment", "content_quality")
    insightTypes = @("SENTIMENT_TREND", "CONTENT_QUALITY")
    priority = "NORMAL"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/insights/generate" -Method POST -Body $insightsData -ContentType "application/json"
    Write-Host "✅ SUCCESS: Generate Insights" -ForegroundColor Green
    Write-Host "   Insights Generated: $($response.data.Count)" -ForegroundColor White
} catch {
    Write-Host "❌ FAILED: Generate Insights - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🏁 Algorithm Modules Testing Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Yellow

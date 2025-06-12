# Test Classification Endpoint
$BASE_URL = "http://localhost:8082"

Write-Host "Testing Classification Endpoint..." -ForegroundColor Green

# Test Classification
$classifyData = @{
    text = "I want to book a hotel room for next week"
    modelType = "INTENT_CLASSIFICATION"
    userId = "user123"
} | ConvertTo-Json

Write-Host "Request Body:" -ForegroundColor Yellow
Write-Host $classifyData

try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/classification/classify" -Method POST -Body $classifyData -ContentType "application/json"
    Write-Host "SUCCESS: Text Classification" -ForegroundColor Green
    Write-Host "   Category: $($response.data.classifications[0].category)" -ForegroundColor White
    Write-Host "   Confidence: $($response.data.classifications[0].confidence)" -ForegroundColor White
} catch {
    Write-Host "FAILED: Text Classification - $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Red
}

Write-Host "Classification Test Complete!" -ForegroundColor Green

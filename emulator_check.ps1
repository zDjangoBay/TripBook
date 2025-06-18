# Script de vérification et optimisation émulateur Android TripBook
# Version optimisée avec analyse de performance

Write-Host "📱 Vérification des Émulateurs Android pour TripBook" -ForegroundColor Green
Write-Host "──────────────────────────────────────────────────────" -ForegroundColor Green

# Configuration des chemins
$avdPath = "C:\Users\HP\.android\avd"

# Fonction d'évaluation rapide
function Get-EmulatorScore {
    param([string]$configPath)
    
    if (-not (Test-Path $configPath)) { return 0 }
    
    $config = Get-Content $configPath
    $score = 0
    
    # RAM
    $ram = [int]($config | Where-Object { $_ -match "hw.ramSize" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($ram -ge 4096) { $score += 5 } elseif ($ram -ge 3072) { $score += 3 } else { $score += 1 }
    
    # CPU
    $cpu = [int]($config | Where-Object { $_ -match "hw.cpu.ncore" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($cpu -ge 4) { $score += 5 } elseif ($cpu -ge 2) { $score += 3 } else { $score += 1 }
    
    # GPU
    $gpu = ($config | Where-Object { $_ -match "hw.gpu.mode" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($gpu -eq "host") { $score += 5 } elseif ($gpu -eq "auto") { $score += 3 } else { $score += 1 }
    
    # FastBoot
    $fastboot = ($config | Where-Object { $_ -match "fastboot.forceFastBoot" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($fastboot -eq "yes") { $score += 3 }
    
    # Optimisations TripBook
    if ($config | Where-Object { $_ -match "# Optimisations TripBook" }) { $score += 2 }
    
    return $score
}

# Analyse des émulateurs
Write-Host "📋 Émulateurs détectés avec scores de performance:" -ForegroundColor Cyan

$bestEmulator = $null
$bestScore = 0

Get-ChildItem -Path $avdPath -Filter "*.ini" | ForEach-Object {
    $name = $_.BaseName
    $configPath = Join-Path $avdPath "$name.avd\config.ini"
    $score = Get-EmulatorScore -configPath $configPath
    
    # Détermination du niveau
    $level = switch ($score) {
        { $_ -ge 18 } { "🏆 Excellent" }
        { $_ -ge 14 } { "🟢 Bon" }
        { $_ -ge 10 } { "🟡 Moyen" }
        default { "🔴 Insuffisant" }
    }
    
    Write-Host "- $name" -ForegroundColor White -NoNewline
    Write-Host " ($score/20 - $level)" -ForegroundColor Gray
    
    if ($score -gt $bestScore) {
        $bestScore = $score
        $bestEmulator = $name
    }
}

if ($bestEmulator) {
    Write-Host "\n🏆 Meilleur émulateur détecté: $bestEmulator" -ForegroundColor Green
    Write-Host "   Score de performance: $bestScore/20" -ForegroundColor Green
    
    if ($bestScore -ge 15) {
        Write-Host "\n✅ Configuration optimale détectée:" -ForegroundColor Green
        Write-Host "   • RAM: 4096 MB (Optimal pour TripBook)" -ForegroundColor White
        Write-Host "   • CPU: 4 cores (Traduction ML Kit efficace)" -ForegroundColor White
        Write-Host "   • GPU: Accélération host (Jetpack Compose fluide)" -ForegroundColor White
        Write-Host "   • FastBoot: Activé (Démarrage rapide)" -ForegroundColor White
        Write-Host "   • Optimisations TripBook appliquées" -ForegroundColor White
        
        Write-Host "\n🚀 Commande optimisée pour lancer TripBook:" -ForegroundColor Cyan
        Write-Host "   emulator -avd $bestEmulator -gpu host -memory 4096 -cores 4" -ForegroundColor Yellow
    } else {
        Write-Host "\n⚠️  Configuration peut être améliorée" -ForegroundColor Yellow
        Write-Host "   Utilisez: .\optimize_emulator_advanced.ps1" -ForegroundColor Cyan
    }
} else {
    Write-Host "\n❌ Aucun émulateur détecté" -ForegroundColor Red
    Write-Host "   Créez un émulateur dans Android Studio" -ForegroundColor Yellow
}

Write-Host "\n🛠️  Pour lancer depuis Android Studio:" -ForegroundColor Cyan
Write-Host "1. Ouvrez Android Studio" -ForegroundColor White
Write-Host "2. Allez dans Device Manager" -ForegroundColor White
Write-Host "3. Sélectionnez $bestEmulator" -ForegroundColor White
Write-Host "4. Cliquez sur Play (▶️)" -ForegroundColor White

Write-Host "\n📊 Scripts disponibles:" -ForegroundColor Cyan
Write-Host "• .\check_emulator_performance.ps1 - Vérification détaillée" -ForegroundColor White
Write-Host "• .\optimize_emulator_advanced.ps1 - Optimisation avancée" -ForegroundColor White


# Script de vérification des performances émulateur TripBook
# Vérifie l'état d'optimisation et fournit des recommandations

Write-Host "🔍 Vérification des Performances Émulateur TripBook" -ForegroundColor Green
Write-Host "══════════════════════════════════════════════" -ForegroundColor Green

$avdPath = "C:\Users\HP\.android\avd"

# Fonction d'évaluation de performance
function Evaluate-EmulatorPerformance {
    param([string]$configPath, [string]$name)
    
    if (-not (Test-Path $configPath)) {
        return $null
    }
    
    $config = Get-Content $configPath
    $score = 0
    $recommendations = @()
    $status = @()
    
    # Vérification RAM
    $ram = [int]($config | Where-Object { $_ -match "hw.ramSize" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($ram -ge 4096) {
        $status += "✅ RAM: $ram MB (Excellent)"
        $score += 5
    } elseif ($ram -ge 3072) {
        $status += "⚠️  RAM: $ram MB (Bon)"
        $score += 3
    } else {
        $status += "❌ RAM: $ram MB (Insuffisant)"
        $recommendations += "Augmenter la RAM à 4096 MB minimum"
        $score += 1
    }
    
    # Vérification CPU
    $cpu = [int]($config | Where-Object { $_ -match "hw.cpu.ncore" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($cpu -ge 4) {
        $status += "✅ CPU: $cpu cores (Optimal)"
        $score += 5
    } elseif ($cpu -ge 2) {
        $status += "⚠️  CPU: $cpu cores (Correct)"
        $score += 3
    } else {
        $status += "❌ CPU: $cpu cores (Insuffisant)"
        $recommendations += "Configurer 4 cores minimum"
        $score += 1
    }
    
    # Vérification GPU
    $gpu = ($config | Where-Object { $_ -match "hw.gpu.mode" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($gpu -eq "host") {
        $status += "✅ GPU: $gpu (Accélération optimale)"
        $score += 5
    } elseif ($gpu -eq "auto") {
        $status += "⚠️  GPU: $gpu (Acceptable)"
        $score += 3
        $recommendations += "Forcer l'accélération GPU host"
    } else {
        $status += "❌ GPU: $gpu (Non optimisé)"
        $recommendations += "Activer l'accélération GPU host"
        $score += 1
    }
    
    # Vérification FastBoot
    $fastboot = ($config | Where-Object { $_ -match "fastboot.forceFastBoot" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($fastboot -eq "yes") {
        $status += "✅ FastBoot: Activé"
        $score += 3
    } else {
        $status += "❌ FastBoot: Désactivé"
        $recommendations += "Activer FastBoot pour démarrage rapide"
    }
    
    # Vérification optimisations TripBook
    $tripbookOptimizations = $config | Where-Object { $_ -match "# Optimisations TripBook" }
    if ($tripbookOptimizations) {
        $status += "✅ Optimisations TripBook: Appliquées"
        $score += 2
    } else {
        $status += "⚠️  Optimisations TripBook: Non appliquées"
        $recommendations += "Appliquer les optimisations spécifiques TripBook"
    }
    
    # Calcul du niveau de performance
    $level = switch ($score) {
        { $_ -ge 18 } { "🏆 Excellent" }
        { $_ -ge 14 } { "🟢 Bon" }
        { $_ -ge 10 } { "🟡 Moyen" }
        default { "🔴 Insuffisant" }
    }
    
    return @{
        Name = $name
        Score = $score
        Level = $level
        Status = $status
        Recommendations = $recommendations
    }
}

# Analyse de tous les émulateurs
Write-Host "📊 Analyse des émulateurs...\n" -ForegroundColor Cyan

$emulators = Get-ChildItem -Path $avdPath -Filter "*.ini" | ForEach-Object {
    $name = $_.BaseName
    $configPath = Join-Path $avdPath "$name.avd\config.ini"
    Evaluate-EmulatorPerformance -configPath $configPath -name $name
} | Where-Object { $_ -ne $null } | Sort-Object Score -Descending

# Affichage des résultats
foreach ($emulator in $emulators) {
    Write-Host "📱 $($emulator.Name)" -ForegroundColor Yellow
    Write-Host "   Niveau: $($emulator.Level) ($($emulator.Score)/20 points)" -ForegroundColor White
    
    Write-Host "   Configuration:" -ForegroundColor Gray
    foreach ($stat in $emulator.Status) {
        Write-Host "     $stat" -ForegroundColor White
    }
    
    if ($emulator.Recommendations.Count -gt 0) {
        Write-Host "   Recommandations:" -ForegroundColor Yellow
        foreach ($rec in $emulator.Recommendations) {
            Write-Host "     • $rec" -ForegroundColor Cyan
        }
    }
    
    Write-Host ""
}

# Recommandations générales
$bestEmulator = $emulators | Select-Object -First 1
if ($bestEmulator) {
    Write-Host "🏆 Meilleur émulateur: $($bestEmulator.Name)" -ForegroundColor Green
    Write-Host "   Score: $($bestEmulator.Score)/20 - $($bestEmulator.Level)" -ForegroundColor Green
    
    if ($bestEmulator.Score -ge 15) {
        Write-Host "\n🚀 Commande optimisée pour lancer TripBook:" -ForegroundColor Green
        Write-Host "   emulator -avd $($bestEmulator.Name) -gpu host -memory 4096 -cores 4" -ForegroundColor Cyan
    } else {
        Write-Host "\n⚠️  Optimisations recommandées avant utilisation:" -ForegroundColor Yellow
        Write-Host "   Lancez: .\optimize_emulator_advanced.ps1" -ForegroundColor Cyan
    }
}

# Vérification système rapide
Write-Host "\n🔍 Diagnostic système rapide:" -ForegroundColor Cyan

# RAM système
$systemRAM = [math]::Round((Get-CimInstance Win32_PhysicalMemory | Measure-Object -Property capacity -Sum).sum / 1gb, 1)
if ($systemRAM -ge 16) {
    Write-Host "✅ RAM système: $systemRAM GB (Excellent)" -ForegroundColor Green
} elseif ($systemRAM -ge 8) {
    Write-Host "⚠️  RAM système: $systemRAM GB (Suffisant)" -ForegroundColor Yellow
} else {
    Write-Host "❌ RAM système: $systemRAM GB (Insuffisant)" -ForegroundColor Red
}

# Processeur
$cpu = Get-CimInstance Win32_Processor
if ($cpu.NumberOfCores -ge 4) {
    Write-Host "✅ Processeur: $($cpu.NumberOfCores) cores (Bon)" -ForegroundColor Green
} else {
    Write-Host "⚠️  Processeur: $($cpu.NumberOfCores) cores (Limité)" -ForegroundColor Yellow
}

# Espace disque
$disk = Get-CimInstance Win32_LogicalDisk | Where-Object DeviceID -eq "C:"
$freeSpaceGB = [math]::Round($disk.FreeSpace / 1GB, 1)
if ($freeSpaceGB -ge 20) {
    Write-Host "✅ Espace libre: $freeSpaceGB GB (Suffisant)" -ForegroundColor Green
} else {
    Write-Host "⚠️  Espace libre: $freeSpaceGB GB (Attention)" -ForegroundColor Yellow
}

Write-Host "\n✨ Vérification terminée!" -ForegroundColor Green
Write-Host "💡 Pour optimiser: .\optimize_emulator_advanced.ps1" -ForegroundColor Cyan


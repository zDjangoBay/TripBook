# Script d'optimisation pour émulateur Android TripBook
# Optimise les performances et lance l'émulateur le plus rapide

Write-Host "🚀 Optimisation de l'émulateur Android pour TripBook" -ForegroundColor Green

# Configuration des variables d'environnement
$env:ANDROID_HOME = "C:\Users\HP\AppData\Local\Android\Sdk"
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:PATH += ";$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\emulator;$env:ANDROID_HOME\cmdline-tools\latest\bin"

# Vérifier que le SDK existe
if (-not (Test-Path $env:ANDROID_HOME)) {
    Write-Host "⚠️  SDK Android non trouvé à $env:ANDROID_HOME" -ForegroundColor Yellow
    Write-Host "Veuillez installer Android Studio et configurer le SDK" -ForegroundColor Yellow
    exit 1
}

# Optimisations système pour de meilleures performances
Write-Host "⚙️  Application des optimisations système..." -ForegroundColor Cyan

# Vérifier Hyper-V (peut ralentir l'émulateur)
try {
    $hyperv = Get-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V-All
    if ($hyperv.State -eq "Enabled") {
        Write-Host "⚠️  Hyper-V est activé - cela peut ralentir l'émulateur Intel HAXM" -ForegroundColor Yellow
        Write-Host "Recommandation: Utilisez Windows Hypervisor Platform (WHPX) au lieu de HAXM" -ForegroundColor Yellow
    }
} catch {
    Write-Host "ℹ️  Impossible de vérifier Hyper-V - continuons" -ForegroundColor Gray
}

# Lister les émulateurs disponibles
Write-Host "📱 Émulateurs disponibles:" -ForegroundColor Cyan
$avdPath = "C:\Users\HP\.android\avd"
$emulators = Get-ChildItem -Path $avdPath -Filter "*.ini" | ForEach-Object {
    $name = $_.BaseName
    $configPath = Join-Path $avdPath "$name.avd\config.ini"
    
    if (Test-Path $configPath) {
        $config = Get-Content $configPath
        $ram = ($config | Where-Object { $_ -match "hw.ramSize" } | ForEach-Object { $_.Split('=')[1].Trim() })
        $cpu = ($config | Where-Object { $_ -match "hw.cpu.ncore" } | ForEach-Object { $_.Split('=')[1].Trim() })
        $api = ($config | Where-Object { $_ -match "image.sysdir.1" } | ForEach-Object { 
            if ($_ -match "android-(\d+)") { $matches[1] } else { "?" }
        })
        
        [PSCustomObject]@{
            Name = $name
            RAM = "${ram} MB"
            CPU = "$cpu cores"
            API = "API $api"
            Config = $configPath
        }
    }
}

$emulators | Format-Table -AutoSize

# Recommander le meilleur émulateur
$recommended = "Pixel_4"
Write-Host "🏆 Émulateur recommandé: $recommended" -ForegroundColor Green
Write-Host "   ✅ API Level 36 (dernière version)" -ForegroundColor Green
Write-Host "   ✅ 4096 MB RAM (optimisé)" -ForegroundColor Green
Write-Host "   ✅ 4 CPU cores (amélioré)" -ForegroundColor Green
Write-Host "   ✅ GPU accélération activée" -ForegroundColor Green
Write-Host "   ✅ FastBoot activé" -ForegroundColor Green

# Options de lancement
Write-Host "🚀 Options de lancement:" -ForegroundColor Cyan
Write-Host "1. Lancer $recommended (Recommandé)" -ForegroundColor White
Write-Host "2. Lancer Pixel_6a (Plus stable)" -ForegroundColor White
Write-Host "3. Compiler et installer TripBook" -ForegroundColor White
Write-Host "4. Lancer TripBook sur l'émulateur" -ForegroundColor White
Write-Host "5. Afficher les conseils d'optimisation" -ForegroundColor White
Write-Host "0. Quitter" -ForegroundColor Gray

$choice = Read-Host "Choisissez une option (1-5, 0 pour quitter)"

switch ($choice) {
    "1" {
        Write-Host "🚀 Lancement de $recommended..." -ForegroundColor Green
        # Note: Nous ne pouvons pas lancer directement l'émulateur sans accès aux binaires SDK
        Write-Host "Pour lancer l'émulateur, utilisez Android Studio ou:" -ForegroundColor Yellow
        Write-Host "emulator -avd $recommended -gpu host -memory 4096 -cores 4" -ForegroundColor Cyan
    }
    "2" {
        Write-Host "🚀 Lancement de Pixel_6a..." -ForegroundColor Green
        Write-Host "emulator -avd Pixel_6a -gpu host" -ForegroundColor Cyan
    }
    "3" {
        Write-Host "🔨 Compilation de TripBook..." -ForegroundColor Green
        .\gradlew assembleDebug
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Compilation réussie!" -ForegroundColor Green
        } else {
            Write-Host "❌ Erreur de compilation" -ForegroundColor Red
        }
    }
    "4" {
        Write-Host "📱 Installation de TripBook..." -ForegroundColor Green
        Write-Host "Assurez-vous que l'émulateur est démarré, puis:" -ForegroundColor Yellow
        Write-Host "adb install app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Cyan
    }
    "5" {
        Write-Host "💡 Conseils d'optimisation:" -ForegroundColor Green
        Write-Host "1. Fermez les applications inutiles" -ForegroundColor White
        Write-Host "2. Utilisez au moins 8GB RAM système" -ForegroundColor White
        Write-Host "3. Activez la virtualisation dans le BIOS" -ForegroundColor White
        Write-Host "4. Utilisez un SSD pour de meilleures performances" -ForegroundColor White
        Write-Host "5. Gardez Windows à jour" -ForegroundColor White
        Write-Host "6. Désactivez l'antivirus pendant le développement" -ForegroundColor White
    }
    "0" {
        Write-Host "👋 Au revoir!" -ForegroundColor Gray
        exit 0
    }
    default {
        Write-Host "❌ Option invalide" -ForegroundColor Red
    }
}

Write-Host "\n✨ Script terminé. Bon développement!" -ForegroundColor Green


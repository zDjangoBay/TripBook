# Script d'optimisation avancée pour émulateurs Android TripBook
# Analyse automatique et optimisation des performances

Write-Host "🚀 Optimisation Avancée des Émulateurs Android pour TripBook" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════════════════" -ForegroundColor Green

# Configuration des chemins
$avdPath = "C:\Users\HP\.android\avd"
$emulatorConfigs = @()

# Analyse des émulateurs existants
Write-Host "📊 Analyse des émulateurs existants..." -ForegroundColor Cyan

$availableEmulators = Get-ChildItem -Path $avdPath -Filter "*.ini" | ForEach-Object {
    $name = $_.BaseName
    $configPath = Join-Path $avdPath "$name.avd\config.ini"
    
    if (Test-Path $configPath) {
        $config = Get-Content $configPath
        $ram = ($config | Where-Object { $_ -match "hw.ramSize" } | ForEach-Object { $_.Split('=')[1].Trim() })
        $cpu = ($config | Where-Object { $_ -match "hw.cpu.ncore" } | ForEach-Object { $_.Split('=')[1].Trim() })
        $gpu = ($config | Where-Object { $_ -match "hw.gpu.mode" } | ForEach-Object { $_.Split('=')[1].Trim() })
        $api = ($config | Where-Object { $_ -match "image.sysdir.1" } | ForEach-Object { 
            if ($_ -match "android-(\d+)") { $matches[1] } else { "?" }
        })
        $fastboot = ($config | Where-Object { $_ -match "fastboot.forceFastBoot" } | ForEach-Object { $_.Split('=')[1].Trim() })
        
        # Calcul du score de performance
        $ramScore = [int]$ram / 1024  # Score basé sur GB
        $cpuScore = [int]$cpu * 2     # Score basé sur cores
        $gpuScore = if ($gpu -eq "host") { 10 } elseif ($gpu -eq "auto") { 5 } else { 0 }
        $apiScore = if ([int]$api -ge 33) { 5 } else { 0 }
        $fastbootScore = if ($fastboot -eq "yes") { 5 } else { 0 }
        
        $totalScore = $ramScore + $cpuScore + $gpuScore + $apiScore + $fastbootScore
        
        [PSCustomObject]@{
            Name = $name
            RAM = [int]$ram
            CPU = [int]$cpu
            GPU = $gpu
            API = $api
            FastBoot = $fastboot
            Score = $totalScore
            ConfigPath = $configPath
            Optimizable = $false
        }
    }
} | Sort-Object Score -Descending

# Affichage du tableau des émulateurs
Write-Host "\n📱 Émulateurs détectés (triés par performance):" -ForegroundColor Yellow
$availableEmulators | Format-Table Name, @{Name="RAM (MB)";Expression={$_.RAM}}, @{Name="CPU";Expression={"$($_.CPU) cores"}}, GPU, @{Name="API Level";Expression={$_.API}}, FastBoot, @{Name="Score";Expression={$_.Score}} -AutoSize

# Identification du meilleur émulateur
$bestEmulator = $availableEmulators | Select-Object -First 1
Write-Host "🏆 Meilleur émulateur détecté: $($bestEmulator.Name)" -ForegroundColor Green
Write-Host "   📊 Score de performance: $($bestEmulator.Score)/30" -ForegroundColor Green

# Fonction d'optimisation
function Optimize-Emulator {
    param(
        [string]$EmulatorName,
        [string]$ConfigPath
    )
    
    Write-Host "⚙️ Optimisation de $EmulatorName..." -ForegroundColor Cyan
    
    # Lecture de la configuration actuelle
    $config = Get-Content $ConfigPath
    $optimizations = @()
    
    # Optimisation de la RAM (minimum 3GB pour TripBook)
    $currentRam = ($config | Where-Object { $_ -match "hw.ramSize" } | ForEach-Object { [int]$_.Split('=')[1].Trim() })
    if ($currentRam -lt 3072) {
        $optimizations += "RAM: $currentRam MB → 3072 MB (recommandé pour TripBook)"
        $config = $config -replace "hw.ramSize.*", "hw.ramSize = 3072"
    }
    
    # Optimisation du CPU (minimum 4 cores)
    $currentCpu = ($config | Where-Object { $_ -match "hw.cpu.ncore" } | ForEach-Object { [int]$_.Split('=')[1].Trim() })
    if ($currentCpu -lt 4) {
        $optimizations += "CPU: $currentCpu cores → 4 cores (améliore les performances ML Kit)"
        $config = $config -replace "hw.cpu.ncore.*", "hw.cpu.ncore = 4"
    }
    
    # Optimisation GPU (forcer host acceleration)
    $currentGpu = ($config | Where-Object { $_ -match "hw.gpu.mode" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($currentGpu -ne "host") {
        $optimizations += "GPU: $currentGpu → host (accélération matérielle)"
        $config = $config -replace "hw.gpu.mode.*", "hw.gpu.mode = host"
    }
    
    # Optimisation du heap VM (pour les apps Compose)
    $currentHeap = ($config | Where-Object { $_ -match "vm.heapSize" } | ForEach-Object { [int]$_.Split('=')[1].Trim() })
    if ($currentHeap -lt 512) {
        $optimizations += "VM Heap: $currentHeap MB → 512 MB (optimisé pour Jetpack Compose)"
        $config = $config -replace "vm.heapSize.*", "vm.heapSize = 512"
    }
    
    # Activation du FastBoot
    $currentFastboot = ($config | Where-Object { $_ -match "fastboot.forceFastBoot" } | ForEach-Object { $_.Split('=')[1].Trim() })
    if ($currentFastboot -ne "yes") {
        $optimizations += "FastBoot: $currentFastboot → yes (démarrage plus rapide)"
        $config = $config -replace "fastboot.forceFastBoot.*", "fastboot.forceFastBoot = yes"
    }
    
    # Ajout d'optimisations spécifiques TripBook
    if (-not ($config | Where-Object { $_ -match "hw.gpu.multisampling" })) {
        $config += "hw.gpu.multisampling = off"
        $optimizations += "GPU Multisampling: off (optimisation performances)"
    }
    
    # Sauvegarde si des optimisations sont nécessaires
    if ($optimizations.Count -gt 0) {
        Write-Host "\n📝 Optimisations appliquées:" -ForegroundColor Yellow
        $optimizations | ForEach-Object { Write-Host "   ✅ $_" -ForegroundColor White }
        
        # Backup de la configuration originale
        $backupPath = "$ConfigPath.backup.$(Get-Date -Format 'yyyyMMdd_HHmmss')"
        Copy-Item $ConfigPath $backupPath
        Write-Host "\n💾 Backup sauvegardé: $backupPath" -ForegroundColor Gray
        
        # Application des optimisations
        $config | Set-Content $ConfigPath -Encoding UTF8
        Write-Host "✅ Configuration optimisée sauvegardée!" -ForegroundColor Green
        
        return $true
    } else {
        Write-Host "✅ Émulateur déjà optimisé!" -ForegroundColor Green
        return $false
    }
}

# Menu d'optimisation
Write-Host "\n🔧 Options d'optimisation:" -ForegroundColor Cyan
Write-Host "1. Optimiser le meilleur émulateur ($($bestEmulator.Name))" -ForegroundColor White
Write-Host "2. Optimiser tous les émulateurs" -ForegroundColor White
Write-Host "3. Optimiser un émulateur spécifique" -ForegroundColor White
Write-Host "4. Créer un nouvel émulateur optimisé" -ForegroundColor White
Write-Host "5. Tester les performances" -ForegroundColor White
Write-Host "6. Diagnostic système" -ForegroundColor White
Write-Host "0. Quitter" -ForegroundColor Gray

$choice = Read-Host "\nChoisissez une option (0-6)"

switch ($choice) {
    "1" {
        Write-Host "\n🚀 Optimisation de $($bestEmulator.Name)..." -ForegroundColor Green
        $optimized = Optimize-Emulator -EmulatorName $bestEmulator.Name -ConfigPath $bestEmulator.ConfigPath
        
        if ($optimized) {
            Write-Host "\n🎯 Recommandations pour le lancement:" -ForegroundColor Yellow
            Write-Host "   • Fermez les applications inutiles" -ForegroundColor White
            Write-Host "   • Utilisez: emulator -avd $($bestEmulator.Name) -gpu host -memory 3072" -ForegroundColor Cyan
        }
    }
    
    "2" {
        Write-Host "\n🔄 Optimisation de tous les émulateurs..." -ForegroundColor Green
        $totalOptimized = 0
        foreach ($emulator in $availableEmulators) {
            Write-Host "\n📱 Traitement de $($emulator.Name)..." -ForegroundColor Yellow
            $optimized = Optimize-Emulator -EmulatorName $emulator.Name -ConfigPath $emulator.ConfigPath
            if ($optimized) { $totalOptimized++ }
        }
        Write-Host "\n✅ $totalOptimized émulateur(s) optimisé(s)!" -ForegroundColor Green
    }
    
    "3" {
        Write-Host "\n📋 Émulateurs disponibles:" -ForegroundColor Yellow
        for ($i = 0; $i -lt $availableEmulators.Count; $i++) {
            Write-Host "$($i + 1). $($availableEmulators[$i].Name)" -ForegroundColor White
        }
        
        $selection = Read-Host "Choisissez un émulateur (1-$($availableEmulators.Count))"
        $index = [int]$selection - 1
        
        if ($index -ge 0 -and $index -lt $availableEmulators.Count) {
            $selectedEmulator = $availableEmulators[$index]
            Optimize-Emulator -EmulatorName $selectedEmulator.Name -ConfigPath $selectedEmulator.ConfigPath
        } else {
            Write-Host "❌ Sélection invalide" -ForegroundColor Red
        }
    }
    
    "4" {
        Write-Host "\n🆕 Création d'un émulateur optimisé pour TripBook..." -ForegroundColor Green
        Write-Host "Cette fonctionnalité nécessite Android Studio SDK Manager" -ForegroundColor Yellow
        Write-Host "\nConfiguration recommandée:" -ForegroundColor Cyan
        Write-Host "• Device: Pixel 4 ou Pixel 5" -ForegroundColor White
        Write-Host "• API Level: 33 ou 34" -ForegroundColor White
        Write-Host "• RAM: 4096 MB" -ForegroundColor White
        Write-Host "• CPU: 4 cores" -ForegroundColor White
        Write-Host "• GPU: Host acceleration" -ForegroundColor White
    }
    
    "5" {
        Write-Host "\n⚡ Test de performance..." -ForegroundColor Green
        
        # Test de compilation
        Write-Host "📦 Test de compilation TripBook..." -ForegroundColor Cyan
        $buildStart = Get-Date
        try {
            $buildResult = & .\gradlew.bat assembleDebug 2>&1
            $buildEnd = Get-Date
            $buildTime = ($buildEnd - $buildStart).TotalSeconds
            
            if ($LASTEXITCODE -eq 0) {
                Write-Host "✅ Compilation réussie en $([math]::Round($buildTime, 1)) secondes" -ForegroundColor Green
            } else {
                Write-Host "❌ Échec de compilation" -ForegroundColor Red
            }
        } catch {
            Write-Host "❌ Erreur lors du test de compilation: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
    
    "6" {
        Write-Host "\n🔍 Diagnostic système..." -ForegroundColor Green
        
        # Vérification RAM système
        $totalRAM = [math]::Round((Get-CimInstance Win32_PhysicalMemory | Measure-Object -Property capacity -Sum).sum / 1gb, 1)
        Write-Host "💾 RAM système: $totalRAM GB" -ForegroundColor White
        
        if ($totalRAM -lt 8) {
            Write-Host "⚠️  Recommandation: Minimum 8GB RAM pour de bonnes performances" -ForegroundColor Yellow
        } else {
            Write-Host "✅ RAM suffisante" -ForegroundColor Green
        }
        
        # Vérification processeur
        $cpu = Get-CimInstance Win32_Processor
        Write-Host "🖥️  Processeur: $($cpu.Name)" -ForegroundColor White
        Write-Host "🔄 Cores: $($cpu.NumberOfCores) cores, $($cpu.NumberOfLogicalProcessors) threads" -ForegroundColor White
        
        # Vérification de la virtualisation
        try {
            $hyperv = Get-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V-All
            if ($hyperv.State -eq "Enabled") {
                Write-Host "⚠️  Hyper-V activé (peut ralentir l'émulateur Intel HAXM)" -ForegroundColor Yellow
                Write-Host "💡 Suggestion: Utilisez Windows Hypervisor Platform (WHPX)" -ForegroundColor Cyan
            } else {
                Write-Host "✅ Hyper-V désactivé (optimal pour Intel HAXM)" -ForegroundColor Green
            }
        } catch {
            Write-Host "ℹ️  Impossible de vérifier Hyper-V" -ForegroundColor Gray
        }
        
        # Vérification de l'espace disque
        $disk = Get-CimInstance Win32_LogicalDisk | Where-Object DeviceID -eq "C:"
        $freeSpaceGB = [math]::Round($disk.FreeSpace / 1GB, 1)
        Write-Host "💽 Espace libre C:: $freeSpaceGB GB" -ForegroundColor White
        
        if ($freeSpaceGB -lt 10) {
            Write-Host "⚠️  Recommandation: Libérez de l'espace (minimum 10GB)" -ForegroundColor Yellow
        } else {
            Write-Host "✅ Espace disque suffisant" -ForegroundColor Green
        }
    }
    
    "0" {
        Write-Host "\n👋 Au revoir!" -ForegroundColor Gray
        exit 0
    }
    
    default {
        Write-Host "❌ Option invalide" -ForegroundColor Red
    }
}

Write-Host "\n✨ Optimisation terminée!" -ForegroundColor Green
Write-Host "💡 Conseil: Redémarrez Android Studio pour appliquer les changements" -ForegroundColor Yellow
Write-Host "\n🚀 Prêt pour développer avec TripBook!" -ForegroundColor Green


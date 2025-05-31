# TripBook Live Development Runner
# Flutter-style development experience for Android

$Host.UI.RawUI.WindowTitle = "TripBook Live Development"

# Colors
$InfoColor = "Cyan"
$SuccessColor = "Green"
$ErrorColor = "Red"
$WarningColor = "Yellow"
$LogColor = "White"

function Write-TimestampedLog {
    param($Message, $Color = "White")
    $timestamp = Get-Date -Format "HH:mm:ss"
    Write-Host "[$timestamp] $Message" -ForegroundColor $Color
}

function Write-Header {
    Clear-Host
    Write-Host ""
    Write-Host "████████╗██████╗ ██╗██████╗ ██████╗  ██████╗  ██████╗ ██╗  ██╗" -ForegroundColor Magenta
    Write-Host "╚══██╔══╝██╔══██╗██║██╔══██╗██╔══██╗██╔═══██╗██╔═══██╗██║ ██╔╝" -ForegroundColor Magenta
    Write-Host "   ██║   ██████╔╝██║██████╔╝██████╔╝██║   ██║██║   ██║█████╔╝ " -ForegroundColor Magenta
    Write-Host "   ██║   ██╔══██╗██║██╔═══╝ ██╔══██╗██║   ██║██║   ██║██╔═██╗ " -ForegroundColor Magenta
    Write-Host "   ██║   ██║  ██║██║██║     ██████╔╝╚██████╔╝╚██████╔╝██║  ██╗" -ForegroundColor Magenta
    Write-Host "   ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝     ╚═════╝  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝" -ForegroundColor Magenta
    Write-Host ""
    Write-Host "                    LIVE DEVELOPMENT MODE" -ForegroundColor Yellow
    Write-Host "                   Flutter-Style Experience" -ForegroundColor Yellow
    Write-Host ""
}

Write-Header

# Check if ADB is available
Write-TimestampedLog "Checking Android Debug Bridge..." $InfoColor
try {
    $null = & adb version 2>$null
    Write-TimestampedLog "✓ ADB found" $SuccessColor
} catch {
    Write-TimestampedLog "✗ ADB not found! Please install Android SDK." $ErrorColor
    Read-Host "Press Enter to exit"
    exit 1
}

# Check for connected devices
Write-TimestampedLog "Checking for Android devices..." $InfoColor
$devices = & adb devices | Select-String "device$"
if ($devices.Count -eq 0) {
    Write-TimestampedLog "✗ No Android devices found!" $ErrorColor
    Write-TimestampedLog "Please start an emulator or connect a device." $WarningColor
    Read-Host "Press Enter to exit"
    exit 1
}

$deviceName = ($devices[0] -split "\s+")[0]
Write-TimestampedLog "✓ Connected to: $deviceName" $SuccessColor
Write-Host ""

# Clear previous logs
Write-TimestampedLog "Clearing previous logs..." $InfoColor
& adb logcat -c

# Build and install
Write-TimestampedLog "Building TripBook..." $InfoColor
Write-Host "----------------------------------------" -ForegroundColor DarkGray

$buildResult = & .\gradlew.bat installDebug 2>&1
$buildSuccess = $LASTEXITCODE -eq 0

if ($buildSuccess) {
    Write-Host "----------------------------------------" -ForegroundColor DarkGray
    Write-TimestampedLog "✓ Build and install successful" $SuccessColor
} else {
    Write-Host "----------------------------------------" -ForegroundColor DarkGray
    Write-TimestampedLog "✗ Build failed!" $ErrorColor
    Write-Host $buildResult -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""

# Launch app
Write-TimestampedLog "Launching TripBook..." $InfoColor
& adb shell am start -n com.android.tripbook/.MainActivity
Write-TimestampedLog "✓ App launched" $SuccessColor
Write-Host ""

# Live logging header
Write-Host "╔════════════════════════════════════════════════════════════════════════════╗" -ForegroundColor Blue
Write-Host "║                              LIVE LOGS                                    ║" -ForegroundColor Blue
Write-Host "║                                                                            ║" -ForegroundColor Blue
Write-Host "║  Watching for:                                                             ║" -ForegroundColor Blue
Write-Host "║  • App interactions and user clicks                                       ║" -ForegroundColor Blue
Write-Host "║  • Errors, exceptions, and crashes                                        ║" -ForegroundColor Blue
Write-Host "║  • TripBook debug messages                                                ║" -ForegroundColor Blue
Write-Host "║                                                                            ║" -ForegroundColor Blue
Write-Host "║  Press Ctrl+C to stop monitoring                                         ║" -ForegroundColor Blue
Write-Host "╚════════════════════════════════════════════════════════════════════════════╝" -ForegroundColor Blue
Write-Host ""

# Start live logging
Write-TimestampedLog "Starting live log monitoring..." $InfoColor
Write-Host ""

try {
    & adb logcat -v time | ForEach-Object {
        $line = $_

        # Filter for relevant logs - catch everything for debugging
        if ($line -match "TripBook|AndroidRuntime|FATAL|ERROR|System\.err|Exception|Crash|MainActivity|DashboardActivity|ClassNotFoundException|NoClassDefFoundError|VerifyError|LinkageError") {
            $timestamp = Get-Date -Format "HH:mm:ss.fff"

            # Color code different types of logs
            if ($line -match "FATAL|CRASH") {
                Write-Host "[$timestamp] " -NoNewline -ForegroundColor DarkGray
                Write-Host $line -ForegroundColor Red
            }
            elseif ($line -match "ERROR|Exception") {
                Write-Host "[$timestamp] " -NoNewline -ForegroundColor DarkGray
                Write-Host $line -ForegroundColor Yellow
            }
            elseif ($line -match "TripBook.*INFO|TripBook.*DEBUG") {
                Write-Host "[$timestamp] " -NoNewline -ForegroundColor DarkGray
                Write-Host $line -ForegroundColor Green
            }
            elseif ($line -match "TripBook") {
                Write-Host "[$timestamp] " -NoNewline -ForegroundColor DarkGray
                Write-Host $line -ForegroundColor Cyan
            }
            else {
                Write-Host "[$timestamp] " -NoNewline -ForegroundColor DarkGray
                Write-Host $line -ForegroundColor White
            }
        }
    }
} catch {
    Write-TimestampedLog "Log monitoring stopped." $WarningColor
}

Write-Host ""
Write-TimestampedLog "Development session ended." $InfoColor

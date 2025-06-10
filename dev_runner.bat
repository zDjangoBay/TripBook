@echo off
setlocal EnableDelayedExpansion
title TripBook Live Development

REM Set console colors
color 0F

echo.
echo ████████╗██████╗ ██╗██████╗ ██████╗  ██████╗  ██████╗ ██╗  ██╗
echo ╚══██╔══╝██╔══██╗██║██╔══██╗██╔══██╗██╔═══██╗██╔═══██╗██║ ██╔╝
echo    ██║   ██████╔╝██║██████╔╝██████╔╝██║   ██║██║   ██║█████╔╝ 
echo    ██║   ██╔══██╗██║██╔═══╝ ██╔══██╗██║   ██║██║   ██║██╔═██╗ 
echo    ██║   ██║  ██║██║██║     ██████╔╝╚██████╔╝╚██████╔╝██║  ██╗
echo    ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝     ╚═════╝  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝
echo.
echo                    LIVE DEVELOPMENT MODE
echo.

REM Function to print with timestamp
set "timestamp="
for /f "tokens=2 delims==" %%a in ('wmic OS Get localdatetime /value') do set "dt=%%a"
set "timestamp=%dt:~8,2%:%dt:~10,2%:%dt:~12,2%"

echo [%timestamp%] Starting TripBook development session...
echo.

REM Check device
echo [INFO] Checking for Android device...
adb devices 2>nul | findstr "device$" >nul
if %errorlevel% neq 0 (
    echo [ERROR] No Android device found! Please start emulator.
    pause
    exit /b 1
)

for /f "tokens=1" %%i in ('adb devices ^| findstr "device$"') do (
    echo [INFO] Connected to: %%i
)
echo.

REM Clear logs
echo [INFO] Clearing previous logs...
adb logcat -c
echo.

REM Build and install
echo [BUILD] Building TripBook...
echo ----------------------------------------
call gradlew.bat installDebug
if %errorlevel% neq 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)
echo ----------------------------------------
echo [BUILD] ✓ Build and install successful
echo.

REM Launch app
echo [LAUNCH] Starting TripBook...
adb shell am start -n com.android.tripbook/.MainActivity
echo [LAUNCH] ✓ App launched on device
echo.

echo ╔════════════════════════════════════════════════════════════════════════════╗
echo ║                              LIVE LOGS                                    ║
echo ║                                                                            ║
echo ║  Watching for:                                                             ║
echo ║  • TripBook app logs                                                       ║
echo ║  • Errors and exceptions                                                   ║
echo ║  • User interactions                                                       ║
echo ║  • Crashes and fatal errors                                               ║
echo ║                                                                            ║
echo ║  Press Ctrl+C to stop                                                     ║
echo ╚════════════════════════════════════════════════════════════════════════════╝
echo.

REM Start live logging with better filtering
adb logcat -v time | findstr /I "TripBook AndroidRuntime FATAL ERROR System.err Exception Crash MainActivity DashboardActivity"

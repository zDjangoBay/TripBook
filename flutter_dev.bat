@echo off
setlocal EnableDelayedExpansion
title TripBook Development - Flutter Style

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    TripBook Development                      ║
echo ║                   Flutter-Style Runner                      ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

REM Check if device is connected
adb devices | findstr "device$" >nul
if %errorlevel% neq 0 (
    echo [ERROR] No Android device/emulator found!
    echo Please start an emulator or connect a device.
    pause
    exit /b 1
)

echo [INFO] Android device detected
echo.

REM Clear previous logs
adb logcat -c

REM Build and install in background
echo [BUILD] Building TripBook...
start /B gradlew.bat installDebug >build.log 2>&1

REM Wait for build to complete
:wait_build
timeout /t 2 /nobreak >nul
tasklist | findstr "java.exe" | findstr "gradle" >nul
if %errorlevel% equ 0 (
    echo [BUILD] Still building...
    goto wait_build
)

REM Check if build was successful
findstr "BUILD SUCCESSFUL" build.log >nul
if %errorlevel% neq 0 (
    echo [ERROR] Build failed! Check build.log for details
    type build.log
    pause
    exit /b 1
)

echo [BUILD] ✓ Build successful
echo [INSTALL] ✓ App installed
echo.

REM Launch the app
echo [LAUNCH] Starting TripBook...
adb shell am start -n com.android.tripbook/.MainActivity
echo [LAUNCH] ✓ App launched
echo.

echo ╔══════════════════════════════════════════════════════════════╗
echo ║                     LIVE DEVELOPMENT LOGS                   ║
echo ║                                                              ║
echo ║  • App interactions will appear below                       ║
echo ║  • Errors and crashes will be highlighted                   ║
echo ║  • Press Ctrl+C to stop monitoring                         ║
echo ║                                                              ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

REM Start live logging with filtering and formatting
adb logcat -v time | findstr /C:"TripBook" /C:"AndroidRuntime" /C:"FATAL" /C:"ERROR" /C:"System.err" /C:"Exception" /C:"Crash"

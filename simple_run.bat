@echo off
echo ========================================
echo    TripBook Simple Runner
echo ========================================
echo.

REM Check if adb is available
where adb >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Android SDK not found in PATH!
    echo Please make sure Android SDK is installed and added to PATH.
    pause
    exit /b 1
)
echo ✓ Android SDK found

REM Check if emulator is available  
where emulator >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Android Emulator not found in PATH!
    echo Please make sure Android SDK emulator is installed.
    pause
    exit /b 1
)
echo ✓ Android Emulator found

REM Simple AVD selection - just use the first one
echo Checking for available emulators...
echo.
echo Available AVDs:
emulator -list-avds
echo.

REM Let user choose or use default
set /p "chosen_avd=Enter AVD name (or press Enter for Medium_Phone_API_35): "
if "%chosen_avd%"=="" set "chosen_avd=Medium_Phone_API_35"

echo Using AVD: %chosen_avd%
echo.

REM Check if emulator is already running
adb devices | findstr "emulator" >nul
if %errorlevel% equ 0 (
    echo ✓ Emulator already running
) else (
    echo Starting emulator: %chosen_avd%
    echo This may take a few minutes...
    start "Android Emulator" emulator -avd "%chosen_avd%"
    
    echo Waiting for emulator to boot...
    :wait_loop
    timeout /t 5 /nobreak >nul
    adb devices | findstr "emulator.*device" >nul
    if %errorlevel% neq 0 (
        echo Still waiting...
        goto wait_loop
    )
    echo ✓ Emulator is ready
)

REM Build and install
echo.
echo Building and installing TripBook...
call gradlew.bat installDebug
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

REM Launch app
echo.
echo Launching TripBook...
adb shell am start -n com.android.tripbook/.MainActivity

echo.
echo ✓ TripBook is now running!
echo.
pause

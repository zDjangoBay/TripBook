@echo off
echo ========================================
echo    TripBook Development Runner
echo ========================================
echo.

REM Set colors for better visibility
color 0A

REM Check if Android SDK is available
echo [1/6] Checking Android SDK...
where adb >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Android SDK not found in PATH!
    echo Please make sure Android SDK is installed and added to PATH.
    echo Typical location: C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools
    pause
    exit /b 1
)
echo [OK] Android SDK found

REM Check if emulator is available
echo [2/6] Checking Android Emulator...
where emulator >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Android Emulator not found in PATH!
    echo Please make sure Android SDK emulator is installed.
    echo Typical location: C:\Users\%USERNAME%\AppData\Local\Android\Sdk\emulator
    pause
    exit /b 1
)
echo [OK] Android Emulator found

REM List available AVDs
echo [3/6] Checking available emulators...

REM Get list of AVDs and pick the first one
echo Scanning for available Android Virtual Devices...
emulator -list-avds > avd_list.tmp 2>nul

REM Check if file exists and has content
if not exist avd_list.tmp (
    echo ERROR: Could not list AVDs
    pause
    exit /b 1
)

REM Read the first line safely
set "first_avd="
for /f "tokens=*" %%a in (avd_list.tmp) do (
    if not defined first_avd set "first_avd=%%a"
)

REM Clean up temp file
del avd_list.tmp 2>nul

REM Check if we found an AVD
if not defined first_avd (
    echo ERROR: No Android Virtual Devices (AVDs) found!
    echo.
    echo Please create an AVD using Android Studio:
    echo 1. Open Android Studio
    echo 2. Go to Tools ^> AVD Manager
    echo 3. Create a new Virtual Device
    echo 4. Choose a device and system image
    echo 5. Finish setup
    echo.
    pause
    exit /b 1
)

echo [OK] Found AVD: %first_avd%

REM Check if emulator is already running
echo [4/6] Checking if emulator is running...
adb devices | findstr "emulator" >nul
if %errorlevel% equ 0 (
    echo [OK] Emulator already running
) else (
    echo Starting emulator: %first_avd%
    echo This may take a few minutes...
    start "Android Emulator" emulator -avd "%first_avd%"

    REM Wait for emulator to boot
    echo Waiting for emulator to boot...
    :wait_for_emulator
    timeout /t 5 /nobreak >nul
    adb devices | findstr "emulator.*device" >nul
    if %errorlevel% neq 0 (
        echo Still waiting for emulator...
        goto wait_for_emulator
    )
    echo [OK] Emulator is ready
)

REM Build and install the app
echo [5/6] Building and installing TripBook...
echo Running: gradlew.bat installDebug
call gradlew.bat installDebug
if %errorlevel% neq 0 (
    echo ERROR: Failed to build and install the app
    pause
    exit /b 1
)
echo [OK] App installed successfully

REM Launch the app
echo [6/6] Launching TripBook...
adb shell am start -n com.android.tripbook/.MainActivity
if %errorlevel% neq 0 (
    echo ERROR: Failed to launch the app
    pause
    exit /b 1
)
echo [OK] App launched successfully

echo.
echo ========================================
echo    TripBook is now running!
echo ========================================
echo.
echo Development Tips:
echo • Make code changes in Android Studio
echo • Use Ctrl+F9 to build changes
echo • Use Ctrl+F10 to apply changes (hot reload)
echo • Use gradlew.bat installDebug to reinstall
echo.
echo Press any key to open development menu...
pause >nul

:dev_menu
cls
echo ========================================
echo    TripBook Development Menu
echo ========================================
echo.
echo 1. Rebuild and reinstall app
echo 2. Launch app (if closed)
echo 3. View app logs
echo 4. Uninstall app
echo 5. Clean build
echo 6. Exit
echo.
set /p choice="Choose an option (1-6): "

if "%choice%"=="1" goto rebuild
if "%choice%"=="2" goto launch
if "%choice%"=="3" goto logs
if "%choice%"=="4" goto uninstall
if "%choice%"=="5" goto clean
if "%choice%"=="6" goto exit
echo Invalid choice. Please try again.
goto dev_menu

:rebuild
echo Rebuilding and reinstalling...
call gradlew.bat installDebug
if %errorlevel% equ 0 (
    echo [OK] App rebuilt and installed
    adb shell am start -n com.android.tripbook/.MainActivity
) else (
    echo ✗ Build failed
)
pause
goto dev_menu

:launch
echo Launching TripBook...
adb shell am start -n com.android.tripbook/.MainActivity
echo [OK] App launched
pause
goto dev_menu

:logs
echo Showing app logs (Press Ctrl+C to stop)...
adb logcat | findstr "TripBook\|AndroidRuntime\|System.err"
pause
goto dev_menu

:uninstall
echo Uninstalling TripBook...
adb uninstall com.android.tripbook
echo [OK] App uninstalled
pause
goto dev_menu

:clean
echo Cleaning build...
call gradlew.bat clean
echo [OK] Build cleaned
pause
goto dev_menu

:exit
echo.
echo Thank you for developing TripBook!
echo.
pause
exit /b 0

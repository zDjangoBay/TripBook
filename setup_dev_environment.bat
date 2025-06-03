@echo off
echo ========================================
echo    TripBook Development Setup
echo ========================================
echo.

REM Check Java installation
echo [1/5] Checking Java installation...
java -version >nul 2>nul
if %errorlevel% neq 0 (
    echo ✗ Java not found!
    echo Please install Java 11 or higher
    echo Download from: https://adoptium.net/
    pause
    exit /b 1
)
echo ✓ Java found

REM Check Android SDK
echo [2/5] Checking Android SDK...
if not exist "%ANDROID_HOME%" (
    if not exist "%LOCALAPPDATA%\Android\Sdk" (
        echo ✗ Android SDK not found!
        echo Please install Android Studio and SDK
        echo Download from: https://developer.android.com/studio
        pause
        exit /b 1
    ) else (
        set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
    )
)
echo ✓ Android SDK found at: %ANDROID_HOME%

REM Add Android SDK to PATH if not already there
echo [3/5] Configuring PATH...
where adb >nul 2>nul
if %errorlevel% neq 0 (
    echo Adding Android SDK to PATH for this session...
    set "PATH=%PATH%;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\emulator;%ANDROID_HOME%\tools\bin"
)
echo ✓ PATH configured

REM Check Gradle
echo [4/5] Checking Gradle...
if exist "gradlew.bat" (
    echo ✓ Gradle wrapper found
) else (
    echo ✗ Gradle wrapper not found!
    echo Make sure you're in the TripBook project directory
    pause
    exit /b 1
)

REM Test build
echo [5/5] Testing build...
echo This may take a few minutes for the first build...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo ✗ Build test failed!
    echo Please check the error messages above
    pause
    exit /b 1
)
echo ✓ Build test successful

echo.
echo ========================================
echo    Setup Complete!
echo ========================================
echo.
echo Your development environment is ready.
echo.
echo Next steps:
echo 1. Run 'run_tripbook_dev.bat' to start development
echo 2. Use 'hot_reload.bat' for quick updates during development
echo.
echo Development workflow:
echo • Make changes in Android Studio or your preferred editor
echo • Run hot_reload.bat to see changes immediately
echo • Use Android Studio's built-in hot reload (Ctrl+F10) for faster updates
echo.
pause

@echo off
echo ========================================
echo    TripBook Hot Reload
echo ========================================
echo.

REM Quick build and install for development
echo Building changes...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo ✗ Build failed!
    pause
    exit /b 1
)

echo Installing to device...
call gradlew.bat installDebug
if %errorlevel% neq 0 (
    echo ✗ Install failed!
    pause
    exit /b 1
)

echo Launching app...
adb shell am start -n com.android.tripbook/.MainActivity

echo ✓ Hot reload complete!
echo.
echo App updated and launched successfully.
timeout /t 3 /nobreak >nul

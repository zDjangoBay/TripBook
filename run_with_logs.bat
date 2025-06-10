@echo off
echo ===================================================
echo Building and installing Trip Book...
echo ===================================================
call gradlew.bat installDebug

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ===================================================
    echo Build failed with error code %ERRORLEVEL%
    echo.
    echo Try running: gradlew.bat clean build --stacktrace
    echo to see more detailed error information.
    echo ===================================================
    exit /b %ERRORLEVEL%
)

echo.
echo ===================================================
echo Starting Trip Book app...
echo ===================================================
adb shell am start -n com.android.tripbook/.MainActivity

echo.
echo ===================================================
echo Showing logs (press Ctrl+C to stop)...
echo Look for lines with TripBookApp, ReservationRepository, etc.
echo ===================================================
echo.

adb logcat -v time | findstr /i "TripBook tripbook AndroidRuntime Exception Error Fatal"

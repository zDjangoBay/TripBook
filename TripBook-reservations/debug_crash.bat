@echo off
echo ========================================
echo    TripBook Crash Debugger
echo ========================================
echo.

echo Clearing previous logs...
adb logcat -c

echo.
echo Starting TripBook and monitoring for crashes...
echo Press Ctrl+C to stop monitoring
echo.

REM Launch the app
adb shell am start -n com.android.tripbook/.MainActivity

echo.
echo Monitoring logs for crashes (showing last 50 lines)...
echo ========================================

REM Monitor logs for crashes and errors
adb logcat -v time | findstr /i "tripbook\|androidruntime\|fatal\|exception\|error\|crash"

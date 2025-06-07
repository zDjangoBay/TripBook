@echo off
echo ========================================
echo    TripBook Full Debug Logger
echo ========================================
echo.

echo Clearing previous logs...
adb logcat -c

echo.
echo Starting comprehensive logging...
echo Press Ctrl+C to stop
echo.

REM Start logging everything related to our app
adb logcat -v time *:V | findstr /i "tripbook\|androidruntime\|fatal\|exception\|error\|crash\|system.err"

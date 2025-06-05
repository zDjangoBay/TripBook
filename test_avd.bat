@echo off
echo Testing AVD detection...
echo.

echo Step 1: Running emulator -list-avds
emulator -list-avds

echo.
echo Step 2: Saving to file and reading back
emulator -list-avds > test_avds.txt 2>nul

echo.
echo Step 3: Showing file contents
type test_avds.txt

echo.
echo Step 4: Reading first line
set "first_avd="
for /f "tokens=*" %%a in (test_avds.txt) do (
    if not defined first_avd (
        set "first_avd=%%a"
        echo Found AVD: %%a
    )
)

echo.
echo Step 5: Final result
echo Selected AVD: "%first_avd%"

del test_avds.txt 2>nul
pause

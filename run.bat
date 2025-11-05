@echo off
echo Starting NFC Payment System...
echo.

REM Clean and compile with Maven
echo Compiling with Maven...
mvn clean compile

REM Check if compilation was successful
if %ERRORLEVEL% neq 0 (
    echo Maven compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM Run the application with Maven
echo Starting application...
mvn exec:java

pause
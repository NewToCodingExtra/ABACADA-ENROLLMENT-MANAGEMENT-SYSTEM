@echo off
echo ========================================
echo Setting up Distribution Folder
echo ========================================

set JDK_PATH=C:\Program Files\Java\zulu25.28.85-ca-fx-jdk25.0.0-win_x64
set PROJECT_PATH=%~dp0

echo.
echo [1/2] Copying JavaFX DLL files...
xcopy "%JDK_PATH%\bin\*.dll" "%PROJECT_PATH%dist\" /Y /Q
if errorlevel 1 (
    echo ERROR: Failed to copy DLL files!
    pause
    exit /b 1
)

echo [2/2] Copying JavaFX JAR files...
xcopy "%JDK_PATH%\lib\javafx.*.jar" "%PROJECT_PATH%dist\lib\" /Y /Q
if errorlevel 1 (
    echo ERROR: Failed to copy JAR files!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Setup Complete!
echo ========================================
echo.
echo Your dist folder is now ready for:
echo  - Testing: java --module-path lib --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar EnrollmentSystem.jar
echo  - Creating EXE with Launch4j
echo.
pause
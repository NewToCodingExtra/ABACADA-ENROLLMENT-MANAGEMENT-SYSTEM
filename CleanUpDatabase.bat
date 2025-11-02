@echo off
echo ========================================
echo  EnrollmentSystem Database Cleanup
echo ========================================
echo.

:: Kill any running MariaDB processes
echo [1/4] Killing MariaDB processes...
taskkill /F /IM mysqld.exe /T >nul 2>&1
timeout /t 2 /nobreak >nul

:: Kill Java processes (optional - uncomment if needed)
:: echo [2/4] Killing Java processes...
:: taskkill /F /IM java.exe /T >nul 2>&1
:: timeout /t 1 /nobreak >nul

:: Take ownership of the directory
echo [2/4] Taking ownership of files...
takeown /F "%USERPROFILE%\.enrollment_system" /R /D Y >nul 2>&1

:: Grant full permissions
echo [3/4] Granting permissions...
icacls "%USERPROFILE%\.enrollment_system" /grant Everyone:(F) /T /C /Q >nul 2>&1

:: Delete the directory
echo [4/4] Deleting database folder...
rd /s /q "%USERPROFILE%\.enrollment_system" >nul 2>&1

:: Verify deletion
if exist "%USERPROFILE%\.enrollment_system" (
    echo.
    echo [FAILED] Could not delete folder completely.
    echo Please restart your computer and run this script again.
    echo.
) else (
    echo.
    echo [SUCCESS] Database folder deleted successfully!
    echo You can now run the application for a clean start.
    echo.
)

pause
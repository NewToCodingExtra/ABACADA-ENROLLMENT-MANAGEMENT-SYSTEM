@echo off
REM ============================================
REM Enrollment System Database Backup Script
REM Configured for your FreeSQLDatabase account
REM ============================================

REM YOUR DATABASE CREDENTIALS
SET DB_HOST=sql12.freesqldatabase.com
SET DB_USER=sql12804580
SET DB_PASS=JSrtCUQHCb
SET DB_NAME=sql12804580
SET BACKUP_DIR=database_backups
SET MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin

REM Get current date and time for filename
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set DATE_TIME=%datetime:~0,8%_%datetime:~8,6%

echo ============================================
echo    Enrollment System Database Backup
echo ============================================
echo Starting backup at: %DATE% %TIME%
echo.

REM Create backup directory if it doesn't exist
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

REM ============================================
REM FULL DATABASE BACKUP
REM ============================================
echo Creating full database backup...
"%MYSQL_PATH%\mysqldump.exe" -h %DB_HOST% -u %DB_USER% -p%DB_PASS% %DB_NAME% --single-transaction --routines --triggers --events --skip-column-statistics > "%BACKUP_DIR%\enrollment_backup_%DATE_TIME%.sql"

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Full backup created!
    echo File: enrollment_backup_%DATE_TIME%.sql
    dir "%BACKUP_DIR%\enrollment_backup_%DATE_TIME%.sql" | find "enrollment_backup"
) else (
    echo [ERROR] Full backup failed!
    echo.
    echo Possible issues:
    echo - MySQL bin path is incorrect
    echo - Internet connection problem
    echo - Database credentials are wrong
    echo.
    pause
    exit /b 1
)

REM ============================================
REM SCHEMA ONLY BACKUP
REM ============================================
echo.
echo Creating schema-only backup...
"%MYSQL_PATH%\mysqldump.exe" -h %DB_HOST% -u %DB_USER% -p%DB_PASS% %DB_NAME% --no-data --routines --triggers --events --skip-column-statistics > "%BACKUP_DIR%\enrollment_schema_%DATE_TIME%.sql"

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Schema backup created!
    echo File: enrollment_schema_%DATE_TIME%.sql
) else (
    echo [WARNING] Schema backup failed!
)

REM ============================================
REM DATA ONLY BACKUP
REM ============================================
echo.
echo Creating data-only backup...
"%MYSQL_PATH%\mysqldump.exe" -h %DB_HOST% -u %DB_USER% -p%DB_PASS% %DB_NAME% --no-create-info --skip-triggers --skip-column-statistics > "%BACKUP_DIR%\enrollment_data_%DATE_TIME%.sql"

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Data backup created!
    echo File: enrollment_data_%DATE_TIME%.sql
) else (
    echo [WARNING] Data backup failed!
)

REM ============================================
REM SUMMARY
REM ============================================
echo.
echo ============================================
echo    Backup Summary
echo ============================================
echo Backup completed at: %DATE% %TIME%
echo Backup location: %CD%\%BACKUP_DIR%
echo.
echo Files created:
dir "%BACKUP_DIR%\*%DATE_TIME%.sql" /B
echo.
echo [SUCCESS] Backup completed successfully!
echo ============================================
echo.

REM ============================================
REM RESTORE INSTRUCTIONS
REM ============================================
echo To restore this backup, use:
echo mysql.exe -h %DB_HOST% -u %DB_USER% -p%DB_PASS% %DB_NAME% ^< %BACKUP_DIR%\enrollment_backup_%DATE_TIME%.sql
echo.
echo Or restore to local database:
echo mysql.exe -u root -p enrollment_system ^< %BACKUP_DIR%\enrollment_backup_%DATE_TIME%.sql
echo.

pause
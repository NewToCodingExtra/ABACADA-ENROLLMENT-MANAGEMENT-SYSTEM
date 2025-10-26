@echo off
REM ============================================
REM Enrollment System Database Backup & Restore
REM ============================================

REM === DATABASE CREDENTIALS ===
SET DB_HOST=sql12.freesqldatabase.com
SET DB_USER=sql12804580
SET DB_PASS=JSrtCUQHCb
SET DB_NAME=sql12804580

REM === PATH SETTINGS ===
SET BACKUP_DIR=database_backups
SET MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin

REM === TIMESTAMP FOR FILES ===
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set DATE_TIME=%datetime:~0,8%_%datetime:~8,6%

echo ============================================
echo    Enrollment System Database Backup
echo ============================================
echo Starting backup at: %DATE% %TIME%
echo.

REM === CREATE BACKUP DIRECTORY IF NOT EXISTS ===
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

REM === FULL DATABASE BACKUP INCLUDING DATA ===
echo [INFO] Creating full database backup (schema + data)...
"%MYSQL_PATH%\mysqldump.exe" -h %DB_HOST% -u %DB_USER% -p%DB_PASS% %DB_NAME% --single-transaction --routines --triggers --events --skip-column-statistics > "%BACKUP_DIR%\enrollment_backup_%DATE_TIME%.sql"

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Full backup created: enrollment_backup_%DATE_TIME%.sql
) else (
    echo [ERROR] Full backup failed!
    pause
    exit /b 1
)

REM === BACKUP SUMMARY ===
echo.
echo ============================================
echo    Backup Summary
echo ============================================
echo Backup completed at: %DATE% %TIME%
echo Backup location: %CD%\%BACKUP_DIR%
echo Files created:
dir "%BACKUP_DIR%\*%DATE_TIME%.sql" /B
echo ============================================
echo Backup completed successfully!
echo.

REM === OPTIONAL: RESTORE DATABASE WITH FK CHECKS DISABLED ===
set /p restore_choice=Do you want to restore the database now? (Y/N): 
if /I "%restore_choice%"=="Y" (
    echo [INFO] Restoring database and disabling foreign key checks...
    "%MYSQL_PATH%\mysql.exe" -h %DB_HOST% -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SET FOREIGN_KEY_CHECKS=0; SOURCE %BACKUP_DIR%\enrollment_backup_%DATE_TIME%.sql; SET FOREIGN_KEY_CHECKS=1;"
    
    if %ERRORLEVEL% EQU 0 (
        echo [SUCCESS] Database restored successfully with foreign key checks ignored during import.
    ) else (
        echo [ERROR] Database restore failed!
        pause
        exit /b 1
    )
) else (
    echo [INFO] Restore skipped.
)

echo.
pause

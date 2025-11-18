@echo off
title Setup - Fetch and Extract Java
setlocal

REM ===============================
REM Step 1: Pull latest from Git
REM ===============================
echo FETCHING THE LATEST FROM GITHUB...
git pull

REM ===============================
REM Step 2: Download java.zip with progress bar
REM ===============================
echo DOWNLOADING java.zip...
powershell -Command ^
"$ProgressPreference = 'Continue'; ^
$wc = New-Object System.Net.WebClient; ^
$uri = 'https://github.com/Clicker-games-Studio/noname1/releases/download/java/java.zip'; ^
$destination = 'java.zip'; ^
$wc.DownloadProgressChanged += { Write-Progress -Activity 'Downloading java.zip' -Status $('{0}% Complete' -f $($_.ProgressPercentage)) -PercentComplete $_.ProgressPercentage }; ^
$wc.DownloadFileAsync($uri, $destination); ^
while ($wc.IsBusy) { Start-Sleep -Milliseconds 200 }"

REM ===============================
REM Step 3: Create extraction folder
REM ===============================
set "EXTRACT_FOLDER=java"
if not exist "%EXTRACT_FOLDER%" mkdir "%EXTRACT_FOLDER%"

REM ===============================
REM Step 4: Extract java.zip into folder
REM ===============================
echo EXTRACTING java.zip into %EXTRACT_FOLDER%...
powershell -Command "Expand-Archive -Path 'java.zip' -DestinationPath '%EXTRACT_FOLDER%' -Force"

REM ===============================
REM Step 5: Test Java version
REM ===============================
echo.
echo TESTING JAVA...
"%EXTRACT_FOLDER%\bin\java.exe" -version

echo.
echo SETUP COMPLETE!
pause

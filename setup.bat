@echo off
title Setup - Fetch and Extract
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
REM Step 3: Extract java.zip
REM ===============================
echo EXTRACTING java.zip...
powershell -Command "Expand-Archive -Path 'java.zip' -DestinationPath '.' -Force"

echo DONE!
pause

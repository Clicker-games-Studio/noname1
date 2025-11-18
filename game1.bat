@echo off
title Snake Game Test

REM ===============================
REM Step 1: Compile SnakeGame
REM ===============================
echo Compiling SnakeGame...
java\bin\javac game1\SnakeGame.java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b
)

REM ===============================
REM Step 2: Run SnakeGame
REM ===============================
echo Running SnakeGame...
cd game1
..\\java\bin\java SnakeGame
cd ..

pause

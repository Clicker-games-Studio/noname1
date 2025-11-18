@echo off
title Snake Game Test

REM Compile
echo Compiling SnakeGame...
java\bin\javac game1\SnakeGame.java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b
)

REM Run
echo Running SnakeGame...
cd game1
..\\java\bin\java SnakeGame
cd ..

pause

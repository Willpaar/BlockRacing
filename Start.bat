@echo off
setlocal enabledelayedexpansion

:: Paths
set SCRIPT_DIR=%~dp0
set SRC=%SCRIPT_DIR%src
set BIN=%SCRIPT_DIR%bin
set JAR=%SCRIPT_DIR%App.jar
set MAINCLASS=gameengine.App
set JAVAFX=%SCRIPT_DIR%lib\javafx-sdk-21.0.8\lib
set LIB=%SCRIPT_DIR%lib

:: Build classpath for non-JavaFX jars (Jackson, etc.)
set CP=
for %%f in ("%LIB%\*.jar") do (
    if not "%%~nxf"=="javafx-sdk-21.0.8" (
        if defined CP (
            set CP=!CP!;%%f
        ) else (
            set CP=%%f
        )
    )
)

:: Clean bin folder
if exist "%BIN%" rmdir /s /q "%BIN%"
mkdir "%BIN%"

:: Collect all Java files
set FILES=
for /R "%SRC%" %%f in (*.java) do (
    set FILES=!FILES! "%%f"
)

:: Compile
echo Compiling Java files...
javac --module-path "%JAVAFX%" --add-modules javafx.controls,javafx.graphics ^
  -cp "!CP!" -d "%BIN%" %FILES%
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b
)

:: Create manifest
echo Manifest-Version: 1.0> "%SCRIPT_DIR%manifest.txt"
echo Main-Class: %MAINCLASS%>> "%SCRIPT_DIR%manifest.txt"

:: Package jar
echo Creating jar...
jar cfm "%JAR%" "%SCRIPT_DIR%manifest.txt" -C "%BIN%" .

:: Run program
echo Running program...
java --module-path "%JAVAFX%" --add-modules javafx.controls,javafx.graphics ^
  -cp "!CP!;%BIN%" %MAINCLASS%

pause

@echo off
@if "%DEBUG%" == "" @echo on
setlocal

:setup
pushd "%~dp0"

rem Skip the Gradle daemon if running as Administrator
net session >nul 2>&1
if %errorLevel% == 0 goto StartGradleAsDaemon

if /i "%GRADLE_HOME%" == "" (
  rem No GRADLE_HOME environment variable defined
  set GRADLE_HOME=%~dp0gradle
  if not exist "%GRADLE_HOME%\bin\gradle.bat" goto FailNoGradle
)

:StartGradleAsDaemon
call "%GRADLE_HOME%\bin\gradle.bat" %*

:End
popd
endlocal
goto :EOF

:FailNoGradle
echo.
echo Gradle version 8.7 not found. Please download it from https://gradle.org/install/
exit /b 1


@echo off
setlocal

rem Ruta raíz del proyecto. %~dp0 siempre termina con \ en Windows.
rem Si se pasa así entre comillas a Java, la barra final puede escapar la comilla
rem y Gradle recibe algo como: C:\repo-" :desktopApp:packageReleaseExe
rem Por eso se elimina la barra final antes de enviarla a GradleBootstrap.
set "APP_HOME=%~dp0"
if "%APP_HOME:~-1%"=="\" set "APP_HOME=%APP_HOME:~0,-1%"

java "%APP_HOME%\gradle\wrapper\GradleBootstrap.java" "%APP_HOME%" %*
exit /b %ERRORLEVEL%

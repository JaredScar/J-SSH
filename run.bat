@echo off
setlocal
cd /d "%~dp0out\artifacts\J_SSH"

set "JAVA_EXE=%USERPROFILE%\.jdks\corretto-23.0.2\bin\java.exe"
if not exist "%JAVA_EXE%" set "JAVA_EXE=java"

"%JAVA_EXE%" --module-path "%~dp0javafx\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml,javafx.web -cp "%~dp0target\classes;%USERPROFILE%\.m2\repository\com\jcraft\jsch\0.1.55\jsch-0.1.55.jar;%USERPROFILE%\.m2\repository\org\json\json\20240303\json-20240303.jar" com.j_ssh.main.MainApp
if errorlevel 1 pause
endlocal

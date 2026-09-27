@REM Maven Wrapper startup batch script
@REM Finds the locally-downloaded Maven and delegates to it.

@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0

@REM Locate Maven in the wrapper dists directory
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6

if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    "%MAVEN_HOME%\bin\mvn.cmd" %*
    goto end
)

@REM Fallback: download Maven if not found
set DIST_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip
set DIST_ZIP=%TEMP%\apache-maven-3.9.6-bin.zip
set DIST_DIR=%USERPROFILE%\.m2\wrapper\dists

echo Maven not found at %MAVEN_HOME%. Downloading...
powershell -Command "Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%DIST_ZIP%'"
powershell -Command "Expand-Archive -Path '%DIST_ZIP%' -DestinationPath '%DIST_DIR%' -Force"
del "%DIST_ZIP%" 2>nul

if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    "%MAVEN_HOME%\bin\mvn.cmd" %*
) else (
    echo ERROR: Could not find Maven after download.
    exit /b 1
)

:end
endlocal

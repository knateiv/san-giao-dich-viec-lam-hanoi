@echo off
setlocal EnableExtensions
chcp 65001 >nul
for %%I in ("%~dp0..") do set "ROOT_DIR=%%~fI"

if exist "C:\Program Files\nodejs\node.exe" set "PATH=%PATH%;C:\Program Files\nodejs"
for /d %%D in ("C:\Program Files\Microsoft\jdk-21*") do if exist "%%~fD\bin\java.exe" (
    set "JAVA_HOME=%%~fD"
    set "PATH=%%~fD\bin;%PATH%"
)
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-21*") do if exist "%%~fD\bin\java.exe" (
    set "JAVA_HOME=%%~fD"
    set "PATH=%%~fD\bin;%PATH%"
)
for /d %%D in ("C:\Program Files\Java\jdk-21*") do if exist "%%~fD\bin\java.exe" (
    set "JAVA_HOME=%%~fD"
    set "PATH=%%~fD\bin;%PATH%"
)

echo ============================================================================
echo   SAN GIAO DICH VIEC LAM HA NOI (NHOM SE.G01 - FIT HOU)
echo   Thu muc du an: %ROOT_DIR%
echo ============================================================================

where node >nul 2>&1
if errorlevel 1 (
    echo [LOI] May chua cai Node.js ^(khong tim thay lenh node^).
    echo Hay chay file Scripts\cai_moi_truong.bat de tu dong cai Node.js + JDK 21.
    pause
    exit /b 1
)

where java >nul 2>&1
if errorlevel 1 (
    echo [LOI] May chua cai Java JDK 21 ^(khong tim thay lenh java^).
    echo Hay chay file Scripts\cai_moi_truong.bat de tu dong cai Node.js + JDK 21.
    pause
    exit /b 1
)

echo [1/4] Khoi dong SQL Server LocalDB (MSSQLLocalDB)...
sqllocaldb start MSSQLLocalDB
if errorlevel 1 (
    echo [CANH BAO] Khong khoi dong duoc LocalDB. Neu dung SQL Server cong 1433, hay chay Backend voi --spring.profiles.active=sqlexpress
)

echo [2/4] Mo cau noi TCP 127.0.0.1:14333 vao LocalDB Named Pipe...
start "DVVL SQL Bridge (14333)" cmd /k "cd /d "%ROOT_DIR%" && node Scripts\sqlserver_localdb_bridge.mjs"
timeout /t 2 /nobreak >nul

echo [3/4] Kiem tra va khoi dong Backend Java 21 Spring Boot (Port 8080)...
if not exist "%ROOT_DIR%\Backend\target\san-giao-dich-viec-lam-1.0.0.jar" (
    echo     Chua co file JAR, dang bien dich Backend bang Maven Wrapper...
    pushd "%ROOT_DIR%\Backend"
    if exist "mvnw.cmd" (
        call mvnw.cmd package -DskipTests
    ) else (
        call mvn package -DskipTests
    )
    if errorlevel 1 (
        popd
        echo [LOI] Bien dich Backend that bai. Kiem tra ket noi mang lan dau hoac phien ban JDK 21.
        pause
        exit /b 1
    )
    popd
)
start "DVVL Backend Spring Boot (8080)" cmd /k "cd /d "%ROOT_DIR%\Backend" && java -jar target\san-giao-dich-viec-lam-1.0.0.jar"
timeout /t 8 /nobreak >nul

echo [4/4] Kiem tra va khoi dong Frontend Next.js 15 (Port 3000)...
if not exist "%ROOT_DIR%\Frontend\node_modules" (
    echo     Dang cai dat thu vien npm cho Frontend...
    pushd "%ROOT_DIR%\Frontend"
    call npm.cmd install
    if errorlevel 1 (
        popd
        echo [LOI] Cai dat npm install that bai.
        pause
        exit /b 1
    )
    popd
)
if not exist "%ROOT_DIR%\Frontend\.next" (
    echo     Dang build Frontend Next.js...
    pushd "%ROOT_DIR%\Frontend"
    call npm.cmd run build
    if errorlevel 1 (
        popd
        echo [LOI] Build Frontend that bai.
        pause
        exit /b 1
    )
    popd
)
start "DVVL Frontend Next.js (3000)" cmd /k "cd /d "%ROOT_DIR%\Frontend" && npm.cmd run start"
timeout /t 3 /nobreak >nul

echo.
echo Mo trinh duyet tai http://localhost:3000
start "" "http://localhost:3000"

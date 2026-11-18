@echo off
setlocal EnableExtensions
chcp 65001 >nul

echo ============================================================================
echo   CAI DAT MOI TRUONG TU DONG CHO MAY THANH VIEN (NHOM SE.G01)
echo   Cai dat: Node.js LTS (npm) + Microsoft OpenJDK 21 (Java 21)
echo ============================================================================
echo.

where winget >nul 2>&1
if errorlevel 1 (
    echo [CANH BAO] May khong co winget. Vui long tai va cai dat thu cong:
    echo   1. Node.js LTS: https://nodejs.org/
    echo   2. JDK 21 (Temurin): https://adoptium.net/temurin/releases/?version=21
    pause
    exit /b 1
)

echo [1/2] Dang kiem tra / cai dat Node.js LTS...
winget install -e --id OpenJS.NodeJS.LTS --accept-package-agreements --accept-source-agreements

echo.
echo [2/2] Dang kiem tra / cai dat Microsoft OpenJDK 21...
winget install -e --id Microsoft.OpenJDK.21 --accept-package-agreements --accept-source-agreements

echo.
echo [HOAN TAT] Da cai xong Node.js va JDK 21 (khong can cai Maven thu cong).
echo Hay DONG cua so nay va chay lai file Scripts\start_all.bat.
pause

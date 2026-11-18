@echo off
setlocal EnableExtensions
chcp 65001 >nul
for %%I in ("%~dp0..") do set "ROOT_DIR=%%~fI"

echo ============================================================================
echo   KHOI TAO CSDL SQL SERVER: db_SanGiaoDichViecLamHN (NHOM SE.G01)
echo ============================================================================

echo [1/3] Khoi dong SQL Server LocalDB (MSSQLLocalDB)...
sqllocaldb start MSSQLLocalDB
if errorlevel 1 (
    echo [LOI] Khong tim thay hoac khong khoi dong duoc MSSQLLocalDB.
    echo Hay cai dat SQL Server Express LocalDB / SQL Server va chay lai.
    pause
    exit /b 1
)

echo [2/3] Tao CSDL 10 bang, 30 quan/huyen, Views, Stored Procedures, Triggers...
sqlcmd -S "(localdb)\MSSQLLocalDB" -E -f 65001 -b -i "%ROOT_DIR%\Database\db_san_giao_dich_viec_lam_hn.sql"
if errorlevel 1 (
    echo [LOI] Chay script SQL that bai.
    pause
    exit /b 1
)

echo [3/3] Cau hinh tai khoan ket noi dvvl_app (Mixed Mode)...
sqlcmd -S "(localdb)\MSSQLLocalDB" -E -Q "IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = N'dvvl_app') CREATE LOGIN [dvvl_app] WITH PASSWORD = N'Dvvl@2026!HaNoi', CHECK_POLICY = OFF; EXEC sp_executesql N'USE db_SanGiaoDichViecLamHN; IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N''dvvl_app'') CREATE USER [dvvl_app] FOR LOGIN [dvvl_app]; ALTER ROLE [db_owner] ADD MEMBER [dvvl_app];';"
sqlcmd -S "(localdb)\MSSQLLocalDB" -E -Q "EXEC xp_instance_regwrite N'HKEY_LOCAL_MACHINE', N'Software\Microsoft\MSSQLServer\MSSQLServer', N'LoginMode', REG_DWORD, 2;" >nul 2>&1

sqllocaldb stop MSSQLLocalDB >nul 2>&1
sqllocaldb start MSSQLLocalDB

echo.
echo [THANH CONG] Da khoi tao CSDL db_SanGiaoDichViecLamHN san sang.
echo Buoc tiep theo: chay Scripts\start_all.bat de khoi chay Backend + Frontend.
pause

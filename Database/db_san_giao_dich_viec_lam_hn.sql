-- =============================================================================
-- CƠ SỞ DỮ LIỆU: db_SanGiaoDichViecLamHN (MICROSOFT SQL SERVER)
-- ĐỀ TÀI: Phát triển phần mềm Sàn giao việc làm trực tuyến - Trung tâm DVVL Hà Nội
-- NHÓM THỰC HIỆN: SE.G01 (FIT HOU - ĐH Mở Hà Nội)
-- QUY ƯỚC: Tuân thủ 100% Mục 2.2 (Naming Convention) & Mục 4.6 (10 Bảng Vật Lý)
-- =============================================================================

USE master;
GO

IF DB_ID(N'db_SanGiaoDichViecLamHN') IS NOT NULL
BEGIN
    ALTER DATABASE db_SanGiaoDichViecLamHN SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE db_SanGiaoDichViecLamHN;
END
GO

CREATE DATABASE db_SanGiaoDichViecLamHN COLLATE Vietnamese_CI_AS;
GO

USE db_SanGiaoDichViecLamHN;
GO

-- =============================================================================
-- PHẦN 1: KHỞI TẠO 10 BẢNG DỮ LIỆU VẬT LÝ (MỤC 4.6.2 & 4.6.3 BÁO CÁO)
-- =============================================================================

-- 1. Bảng Vai trò quyền hạn (tbl_VaiTro)
CREATE TABLE dbo.tbl_VaiTro (
    PK_MaVaiTro INT NOT NULL,
    sTenVaiTro NVARCHAR(100) NOT NULL,
    CONSTRAINT PK_tbl_VaiTro PRIMARY KEY (PK_MaVaiTro),
    CONSTRAINT UQ_tbl_VaiTro_Ten UNIQUE (sTenVaiTro)
);
GO

-- 2. Bảng Người dùng & Tài khoản (tbl_TaiKhoan)
CREATE TABLE dbo.tbl_TaiKhoan (
    PK_MaTaiKhoan CHAR(10) NOT NULL,
    sEmail VARCHAR(150) NOT NULL,
    sMatKhau CHAR(60) NOT NULL,
    FK_MaVaiTro INT NOT NULL,
    sHoTen NVARCHAR(100) NULL,
    sSDT VARCHAR(10) NULL,
    sDuongDanAnhDaiDien VARCHAR(255) NULL,
    bDaKichHoat BIT NOT NULL DEFAULT 0,
    bBiKhoa BIT NOT NULL DEFAULT 0,
    iSoLanDangNhapSai INT NOT NULL DEFAULT 0,
    dKhoaDenLuc DATETIME NULL,
    sLyDoKhoa NVARCHAR(255) NULL,
    bDaXoa BIT NOT NULL DEFAULT 0,
    dNgayTao DATETIME NOT NULL DEFAULT GETDATE(),
    dNgayCapNhat DATETIME NULL,
    CONSTRAINT PK_tbl_TaiKhoan PRIMARY KEY (PK_MaTaiKhoan),
    CONSTRAINT UQ_tbl_TaiKhoan_Email UNIQUE (sEmail),
    CONSTRAINT FK_tbl_TaiKhoan_VaiTro FOREIGN KEY (FK_MaVaiTro) REFERENCES dbo.tbl_VaiTro (PK_MaVaiTro),
    CONSTRAINT CHK_tbl_TaiKhoan_Ma CHECK (PK_MaTaiKhoan LIKE 'US[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')
);
GO

-- 3. Bảng Danh mục Quận / Huyện (tbl_QuanHuyen)
CREATE TABLE dbo.tbl_QuanHuyen (
    PK_MaQuanHuyen CHAR(5) NOT NULL,
    sTenQuanHuyen NVARCHAR(100) NOT NULL,
    sKhuVuc NVARCHAR(20) NULL,
    iThuTuHienThi INT NULL,
    bDaXoa BIT NOT NULL DEFAULT 0,
    CONSTRAINT PK_tbl_QuanHuyen PRIMARY KEY (PK_MaQuanHuyen),
    CONSTRAINT CHK_tbl_QuanHuyen_KhuVuc CHECK (sKhuVuc IS NULL OR sKhuVuc IN (N'Nội thành', N'Ngoại thành'))
);
GO

-- 4. Bảng Danh mục Ngành nghề (tbl_NganhNghe)
CREATE TABLE dbo.tbl_NganhNghe (
    PK_MaNganhNghe INT NOT NULL,
    sTenNganhNghe NVARCHAR(100) NOT NULL,
    sNhomNganh NVARCHAR(100) NULL,
    bTrangThaiHoatDong BIT NOT NULL DEFAULT 1,
    bDaXoa BIT NOT NULL DEFAULT 0,
    CONSTRAINT PK_tbl_NganhNghe PRIMARY KEY (PK_MaNganhNghe)
);
GO

-- 5. Bảng Hồ sơ Người tìm việc (tbl_HoSoNguoiTimViec)
CREATE TABLE dbo.tbl_HoSoNguoiTimViec (
    PK_MaUngVien CHAR(10) NOT NULL,
    FK_MaTaiKhoan CHAR(10) NOT NULL,
    sHoTen NVARCHAR(100) NOT NULL,
    sSoCCCD CHAR(12) NOT NULL,
    dNgaySinh DATE NOT NULL,
    sGioiTinh NVARCHAR(10) NOT NULL,
    sSDT VARCHAR(10) NOT NULL,
    sEmailLienHe VARCHAR(150) NULL,
    FK_MaQuanCuTru CHAR(5) NOT NULL,
    sTrinhDoVanHoa NVARCHAR(50) NULL,
    sTrinhDoChuyenMon NVARCHAR(100) NULL,
    sKyNangTinHoc NVARCHAR(200) NULL,
    sTrinhDoNgoaiNgu NVARCHAR(200) NULL,
    sKinhNghiemLamViec NVARCHAR(MAX) NULL,
    sViTriMongMuon NVARCHAR(200) NULL,
    fMucLuongKyVong FLOAT NULL,
    sDiaBanMongMuon NVARCHAR(200) NULL,
    sDuongDanCV VARCHAR(255) NULL,
    iTyLeHoanThien INT NOT NULL DEFAULT 0,
    bDangTimViecCongKhai BIT NOT NULL DEFAULT 1,
    bDaXoa BIT NOT NULL DEFAULT 0,
    CONSTRAINT PK_tbl_HoSoNguoiTimViec PRIMARY KEY (PK_MaUngVien),
    CONSTRAINT UQ_tbl_HoSoNguoiTimViec_TaiKhoan UNIQUE (FK_MaTaiKhoan),
    CONSTRAINT UQ_tbl_HoSoNguoiTimViec_CCCD UNIQUE (sSoCCCD),
    CONSTRAINT FK_tbl_HoSoNguoiTimViec_TaiKhoan FOREIGN KEY (FK_MaTaiKhoan) REFERENCES dbo.tbl_TaiKhoan (PK_MaTaiKhoan),
    CONSTRAINT FK_tbl_HoSoNguoiTimViec_QuanHuyen FOREIGN KEY (FK_MaQuanCuTru) REFERENCES dbo.tbl_QuanHuyen (PK_MaQuanHuyen),
    CONSTRAINT CHK_tbl_HoSoNguoiTimViec_Ma CHECK (PK_MaUngVien LIKE 'SK[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CHK_tbl_HoSoNguoiTimViec_CCCD CHECK (sSoCCCD LIKE '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CHK_tbl_HoSoNguoiTimViec_SDT CHECK (sSDT LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CHK_tbl_HoSoNguoiTimViec_TyLe CHECK (iTyLeHoanThien BETWEEN 0 AND 100)
);
GO

-- 6. Bảng Hồ sơ Doanh nghiệp (tbl_DoanhNghiep)
CREATE TABLE dbo.tbl_DoanhNghiep (
    PK_MaDoanhNghiep CHAR(10) NOT NULL,
    FK_MaTaiKhoan CHAR(10) NOT NULL,
    sTenDoanhNghiep NVARCHAR(200) NOT NULL,
    sTenVietTat NVARCHAR(100) NULL,
    sMaSoThue VARCHAR(14) NOT NULL,
    sDuongDanGiayPhep VARCHAR(255) NULL,
    sDiaChiTruSo NVARCHAR(255) NULL,
    FK_MaQuanTruSo CHAR(5) NULL,
    sLinhVucHoatDong NVARCHAR(200) NULL,
    sNguoiDaiDien NVARCHAR(100) NULL,
    sTenNguoiLienHe NVARCHAR(100) NULL,
    sSDTNguoiLienHe VARCHAR(10) NULL,
    sEmailNguoiLienHe VARCHAR(150) NULL,
    iTrangThaiThamDinh INT NOT NULL DEFAULT 0,
    sGhiChuThamDinh NVARCHAR(500) NULL,
    bDaXoa BIT NOT NULL DEFAULT 0,
    CONSTRAINT PK_tbl_DoanhNghiep PRIMARY KEY (PK_MaDoanhNghiep),
    CONSTRAINT UQ_tbl_DoanhNghiep_TaiKhoan UNIQUE (FK_MaTaiKhoan),
    CONSTRAINT UQ_tbl_DoanhNghiep_MST UNIQUE (sMaSoThue),
    CONSTRAINT FK_tbl_DoanhNghiep_TaiKhoan FOREIGN KEY (FK_MaTaiKhoan) REFERENCES dbo.tbl_TaiKhoan (PK_MaTaiKhoan),
    CONSTRAINT FK_tbl_DoanhNghiep_QuanHuyen FOREIGN KEY (FK_MaQuanTruSo) REFERENCES dbo.tbl_QuanHuyen (PK_MaQuanHuyen),
    CONSTRAINT CHK_tbl_DoanhNghiep_Ma CHECK (PK_MaDoanhNghiep LIKE 'DN[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CHK_tbl_DoanhNghiep_ThamDinh CHECK (iTrangThaiThamDinh IN (0, 1, 2))
);
GO

-- 7. Bảng Tin tuyển dụng (tbl_TinTuyenDung)
CREATE TABLE dbo.tbl_TinTuyenDung (
    PK_MaTin CHAR(10) NOT NULL,
    FK_MaDoanhNghiep CHAR(10) NOT NULL,
    sTieuDe NVARCHAR(200) NOT NULL,
    sViTriChucDanh NVARCHAR(200) NOT NULL,
    FK_MaNganhNghe INT NOT NULL,
    FK_MaQuanLamViec CHAR(5) NOT NULL,
    sDiaChiCuThe NVARCHAR(255) NULL,
    iSoLuongTuyen INT NOT NULL,
    sCapBac NVARCHAR(50) NULL,
    sHinhThucLamViec NVARCHAR(50) NULL,
    fLuongToiThieu FLOAT NOT NULL,
    fLuongToiDa FLOAT NOT NULL,
    sYeuCauKinhNghiem NVARCHAR(200) NULL,
    sYeuCauHocVan NVARCHAR(100) NULL,
    sMoTaCongViec NVARCHAR(MAX) NULL,
    sCheDoDaiNgo NVARCHAR(MAX) NULL,
    dHanNopHoSo DATE NOT NULL,
    iTrangThaiDuyet INT NOT NULL DEFAULT 0,
    FK_MaCanBoDuyet CHAR(10) NULL,
    dNgayDuyet DATETIME NULL,
    sLyDoTuChoi NVARCHAR(500) NULL,
    bDaDong BIT NOT NULL DEFAULT 0,
    bDaXoa BIT NOT NULL DEFAULT 0,
    CONSTRAINT PK_tbl_TinTuyenDung PRIMARY KEY (PK_MaTin),
    CONSTRAINT FK_tbl_TinTuyenDung_DoanhNghiep FOREIGN KEY (FK_MaDoanhNghiep) REFERENCES dbo.tbl_DoanhNghiep (PK_MaDoanhNghiep),
    CONSTRAINT FK_tbl_TinTuyenDung_NganhNghe FOREIGN KEY (FK_MaNganhNghe) REFERENCES dbo.tbl_NganhNghe (PK_MaNganhNghe),
    CONSTRAINT FK_tbl_TinTuyenDung_QuanHuyen FOREIGN KEY (FK_MaQuanLamViec) REFERENCES dbo.tbl_QuanHuyen (PK_MaQuanHuyen),
    CONSTRAINT FK_tbl_TinTuyenDung_CanBo FOREIGN KEY (FK_MaCanBoDuyet) REFERENCES dbo.tbl_TaiKhoan (PK_MaTaiKhoan),
    CONSTRAINT CHK_tbl_TinTuyenDung_Ma CHECK (PK_MaTin LIKE 'JB[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CHK_tbl_TinTuyenDung_SoLuong CHECK (iSoLuongTuyen >= 1),
    CONSTRAINT CHK_tbl_TinTuyenDung_Luong CHECK (fLuongToiThieu >= 0 AND fLuongToiDa >= fLuongToiThieu),
    CONSTRAINT CHK_tbl_TinTuyenDung_TrangThai CHECK (iTrangThaiDuyet IN (0, 1, 2, 3))
);
GO

-- 8. Bảng Giao dịch Ứng tuyển (tbl_UngTuyen)
CREATE TABLE dbo.tbl_UngTuyen (
    PK_MaUngTuyen CHAR(10) NOT NULL,
    FK_MaTin CHAR(10) NOT NULL,
    FK_MaUngVien CHAR(10) NOT NULL,
    dNgayNop DATETIME NOT NULL DEFAULT GETDATE(),
    iTrangThaiUngTuyen INT NOT NULL DEFAULT 0,
    dThoiGianPhongVan DATETIME NULL,
    sDiaDiemPhongVan NVARCHAR(255) NULL,
    sHinhThucPhongVan NVARCHAR(30) NULL,
    sGhiChuPhongVan NVARCHAR(500) NULL,
    bDaXoa BIT NOT NULL DEFAULT 0,
    CONSTRAINT PK_tbl_UngTuyen PRIMARY KEY (PK_MaUngTuyen),
    CONSTRAINT UQ_tbl_UngTuyen_Tin_UngVien UNIQUE (FK_MaTin, FK_MaUngVien),
    CONSTRAINT FK_tbl_UngTuyen_Tin FOREIGN KEY (FK_MaTin) REFERENCES dbo.tbl_TinTuyenDung (PK_MaTin),
    CONSTRAINT FK_tbl_UngTuyen_UngVien FOREIGN KEY (FK_MaUngVien) REFERENCES dbo.tbl_HoSoNguoiTimViec (PK_MaUngVien),
    CONSTRAINT CHK_tbl_UngTuyen_Ma CHECK (PK_MaUngTuyen LIKE 'AP[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CHK_tbl_UngTuyen_TrangThai CHECK (iTrangThaiUngTuyen BETWEEN 0 AND 5)
);
GO

-- 9. Bảng Nhật ký hệ thống - Audit Log (tbl_NhatKyHeThong)
CREATE TABLE dbo.tbl_NhatKyHeThong (
    PK_MaNhatKy BIGINT IDENTITY(1,1) NOT NULL,
    FK_MaTaiKhoan CHAR(10) NULL,
    sLoaiHanhDong NVARCHAR(50) NOT NULL,
    sMucDo VARCHAR(20) NOT NULL,
    sDiaChiIP VARCHAR(45) NULL,
    sTrinhDuyet NVARCHAR(255) NULL,
    dThoiGianGhi DATETIME NOT NULL DEFAULT GETDATE(),
    sDuLieuCu NVARCHAR(MAX) NULL,
    sDuLieuMoi NVARCHAR(MAX) NULL,
    CONSTRAINT PK_tbl_NhatKyHeThong PRIMARY KEY (PK_MaNhatKy),
    CONSTRAINT FK_tbl_NhatKyHeThong_TaiKhoan FOREIGN KEY (FK_MaTaiKhoan) REFERENCES dbo.tbl_TaiKhoan (PK_MaTaiKhoan),
    CONSTRAINT CHK_tbl_NhatKyHeThong_MucDo CHECK (sMucDo IN ('INFO', 'WARNING', 'ERROR', 'SECURITY_ACTION'))
);
GO

-- 10. Bảng Lịch sử trạng thái Ứng tuyển (tbl_LichSuUngTuyen)
CREATE TABLE dbo.tbl_LichSuUngTuyen (
    PK_MaLichSu BIGINT IDENTITY(1,1) NOT NULL,
    FK_MaUngTuyen CHAR(10) NOT NULL,
    iTrangThaiCu INT NULL,
    iTrangThaiMoi INT NOT NULL,
    FK_MaNguoiThucHien CHAR(10) NOT NULL,
    sGhiChu NVARCHAR(500) NULL,
    dThoiGianThayDoi DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_tbl_LichSuUngTuyen PRIMARY KEY (PK_MaLichSu),
    CONSTRAINT FK_tbl_LichSuUngTuyen_UngTuyen FOREIGN KEY (FK_MaUngTuyen) REFERENCES dbo.tbl_UngTuyen (PK_MaUngTuyen),
    CONSTRAINT FK_tbl_LichSuUngTuyen_TaiKhoan FOREIGN KEY (FK_MaNguoiThucHien) REFERENCES dbo.tbl_TaiKhoan (PK_MaTaiKhoan),
    CONSTRAINT CHK_tbl_LichSuUngTuyen_TrangThai CHECK (iTrangThaiMoi BETWEEN 0 AND 5)
);
GO

-- =============================================================================
-- PHẦN 2: CHỈ MỤC TỐI ƯU HIỆU NĂNG TRA CỨU <= 1.5 GIÂY (R31)
-- =============================================================================

CREATE INDEX IX_tbl_TinTuyenDung_TimKiemCongKhai
ON dbo.tbl_TinTuyenDung (iTrangThaiDuyet, bDaDong, bDaXoa, dHanNopHoSo, FK_MaNganhNghe, FK_MaQuanLamViec, fLuongToiThieu, fLuongToiDa);
GO

CREATE INDEX IX_tbl_HoSoNguoiTimViec_CongKhai
ON dbo.tbl_HoSoNguoiTimViec (bDangTimViecCongKhai, bDaXoa, FK_MaQuanCuTru);
GO

CREATE INDEX IX_tbl_UngTuyen_Tin_TrangThai
ON dbo.tbl_UngTuyen (FK_MaTin, FK_MaUngVien, iTrangThaiUngTuyen, bDaXoa);
GO

CREATE INDEX IX_tbl_NhatKyHeThong_ThoiGian
ON dbo.tbl_NhatKyHeThong (dThoiGianGhi DESC, sMucDo);
GO

-- =============================================================================
-- PHẦN 3: VIEWS (vv_*) THEO QUY ƯỚC MỤC 2.2 & QUY TẮC MỤC 4.6.6
-- =============================================================================

-- View 1: Tin tuyển dụng công khai thỏa mãn đủ 03 quy tắc Mục 4.6.6
CREATE VIEW dbo.vv_TinTuyenDungCongKhai AS
SELECT
    t.PK_MaTin,
    t.FK_MaDoanhNghiep,
    dn.sTenDoanhNghiep,
    dn.sTenVietTat,
    t.sTieuDe,
    t.sViTriChucDanh,
    t.FK_MaNganhNghe,
    nn.sTenNganhNghe,
    t.FK_MaQuanLamViec,
    qh.sTenQuanHuyen,
    t.sDiaChiCuThe,
    t.iSoLuongTuyen,
    t.sCapBac,
    t.sHinhThucLamViec,
    t.fLuongToiThieu,
    t.fLuongToiDa,
    t.sYeuCauKinhNghiem,
    t.sYeuCauHocVan,
    t.sMoTaCongViec,
    t.sCheDoDaiNgo,
    t.dHanNopHoSo,
    t.dNgayDuyet
FROM dbo.tbl_TinTuyenDung t
INNER JOIN dbo.tbl_DoanhNghiep dn ON t.FK_MaDoanhNghiep = dn.PK_MaDoanhNghiep
INNER JOIN dbo.tbl_NganhNghe nn ON t.FK_MaNganhNghe = nn.PK_MaNganhNghe
INNER JOIN dbo.tbl_QuanHuyen qh ON t.FK_MaQuanLamViec = qh.PK_MaQuanHuyen
WHERE t.iTrangThaiDuyet = 1
  AND t.dHanNopHoSo >= CAST(GETDATE() AS DATE)
  AND t.bDaDong = 0
  AND t.bDaXoa = 0
  AND dn.bDaXoa = 0
  AND dn.iTrangThaiThamDinh = 1;
GO

-- View 2: Ngân hàng hồ sơ ứng viên công khai (Ẩn CCCD, SĐT, Email theo NĐ 13/2023/NĐ-CP)
CREATE VIEW dbo.vv_HoSoUngVienCongKhai AS
SELECT
    hs.PK_MaUngVien,
    hs.sHoTen,
    hs.sGioiTinh,
    YEAR(GETDATE()) - YEAR(hs.dNgaySinh) AS iDoTuoi,
    hs.FK_MaQuanCuTru,
    qh.sTenQuanHuyen,
    hs.sTrinhDoVanHoa,
    hs.sTrinhDoChuyenMon,
    hs.sKyNangTinHoc,
    hs.sTrinhDoNgoaiNgu,
    hs.sKinhNghiemLamViec,
    hs.sViTriMongMuon,
    hs.fMucLuongKyVong,
    hs.sDiaBanMongMuon,
    hs.iTyLeHoanThien,
    CASE WHEN hs.sDuongDanCV IS NOT NULL THEN 1 ELSE 0 END AS bCoTepCV
FROM dbo.tbl_HoSoNguoiTimViec hs
INNER JOIN dbo.tbl_QuanHuyen qh ON hs.FK_MaQuanCuTru = qh.PK_MaQuanHuyen
INNER JOIN dbo.tbl_TaiKhoan tk ON hs.FK_MaTaiKhoan = tk.PK_MaTaiKhoan
WHERE hs.bDangTimViecCongKhai = 1
  AND hs.bDaXoa = 0
  AND tk.bDaXoa = 0
  AND tk.bBiKhoa = 0;
GO

-- View 3: Thống kê cung - cầu lao động theo ngành nghề (R24, R25)
CREATE VIEW dbo.vv_ThongKeCungCauTheoNganh AS
SELECT
    nn.PK_MaNganhNghe,
    nn.sTenNganhNghe,
    nn.sNhomNganh,
    ISNULL(tin.SoTinTuyenDung, 0) AS iSoTinTuyenDung,
    ISNULL(tin.TongChiTieuCau, 0) AS iTongCauChiTieu,
    ISNULL(uv.TongNguoiTimViec, 0) AS iTongCungHoSo,
    ISNULL(ut.TongLuotUngTuyen, 0) AS iTongLuotUngTuyen,
    ISNULL(ut.SoTrungTuyen, 0) AS iSoTrungTuyen
FROM dbo.tbl_NganhNghe nn
LEFT JOIN (
    SELECT FK_MaNganhNghe, COUNT(*) AS SoTinTuyenDung, SUM(iSoLuongTuyen) AS TongChiTieuCau
    FROM dbo.tbl_TinTuyenDung
    WHERE bDaXoa = 0 AND iTrangThaiDuyet = 1
    GROUP BY FK_MaNganhNghe
) tin ON nn.PK_MaNganhNghe = tin.FK_MaNganhNghe
LEFT JOIN (
    SELECT t.FK_MaNganhNghe, COUNT(DISTINCT u.FK_MaUngVien) AS TongNguoiTimViec,
           COUNT(*) AS TongLuotUngTuyen,
           SUM(CASE WHEN u.iTrangThaiUngTuyen = 3 THEN 1 ELSE 0 END) AS SoTrungTuyen
    FROM dbo.tbl_UngTuyen u
    INNER JOIN dbo.tbl_TinTuyenDung t ON u.FK_MaTin = t.PK_MaTin
    WHERE u.bDaXoa = 0 AND u.iTrangThaiUngTuyen <> 5
    GROUP BY t.FK_MaNganhNghe
) ut ON nn.PK_MaNganhNghe = ut.FK_MaNganhNghe
LEFT JOIN (
    SELECT nn2.PK_MaNganhNghe, COUNT(DISTINCT hs.PK_MaUngVien) AS TongNguoiTimViec
    FROM dbo.tbl_NganhNghe nn2
    INNER JOIN dbo.tbl_HoSoNguoiTimViec hs
        ON hs.bDaXoa = 0
       AND (
            hs.sViTriMongMuon LIKE N'%' + LEFT(nn2.sTenNganhNghe, 6) + N'%'
            OR hs.sTrinhDoChuyenMon LIKE N'%' + LEFT(nn2.sTenNganhNghe, 6) + N'%'
       )
    GROUP BY nn2.PK_MaNganhNghe
) uv ON nn.PK_MaNganhNghe = uv.PK_MaNganhNghe
WHERE nn.bDaXoa = 0;
GO

-- =============================================================================
-- PHẦN 4: STORED PROCEDURES (sp_*) THEO QUY ƯỚC MỤC 2.2
-- =============================================================================

-- SP 1: Tìm kiếm việc làm đa tiêu chí (toán tử AND)
CREATE PROCEDURE dbo.sp_TimKiemViecLamDaTieuChi
    @sTuKhoa NVARCHAR(200) = NULL,
    @iMaNganhNghe INT = NULL,
    @sMaQuanHuyen CHAR(5) = NULL,
    @fLuongToiThieu FLOAT = NULL,
    @sYeuCauHocVan NVARCHAR(100) = NULL,
    @sHinhThucLamViec NVARCHAR(50) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SELECT *
    FROM dbo.vv_TinTuyenDungCongKhai
    WHERE (@sTuKhoa IS NULL OR LTRIM(RTRIM(@sTuKhoa)) = N'' OR
           sTieuDe LIKE N'%' + @sTuKhoa + N'%' OR
           sViTriChucDanh LIKE N'%' + @sTuKhoa + N'%' OR
           sTenDoanhNghiep LIKE N'%' + @sTuKhoa + N'%')
      AND (@iMaNganhNghe IS NULL OR FK_MaNganhNghe = @iMaNganhNghe)
      AND (@sMaQuanHuyen IS NULL OR LTRIM(RTRIM(@sMaQuanHuyen)) = '' OR FK_MaQuanLamViec = @sMaQuanHuyen)
      AND (@fLuongToiThieu IS NULL OR fLuongToiDa >= @fLuongToiThieu OR fLuongToiThieu = 0)
      AND (@sYeuCauHocVan IS NULL OR LTRIM(RTRIM(@sYeuCauHocVan)) = N'' OR sYeuCauHocVan LIKE N'%' + @sYeuCauHocVan + N'%')
      AND (@sHinhThucLamViec IS NULL OR LTRIM(RTRIM(@sHinhThucLamViec)) = N'' OR sHinhThucLamViec = @sHinhThucLamViec)
    ORDER BY dNgayDuyet DESC, PK_MaTin DESC;
END;
GO

-- SP 2: Thống kê cung - cầu lao động theo khoảng thời gian, ngành nghề và quận/huyện
CREATE PROCEDURE dbo.sp_ThongKeCungCauLaoDong
    @dTuNgay DATE = NULL,
    @dDenNgay DATE = NULL,
    @iMaNganhNghe INT = NULL,
    @sMaQuanHuyen CHAR(5) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        qh.PK_MaQuanHuyen,
        qh.sTenQuanHuyen,
        qh.sKhuVuc,
        (SELECT COUNT(*) FROM dbo.tbl_HoSoNguoiTimViec hs
         WHERE hs.FK_MaQuanCuTru = qh.PK_MaQuanHuyen AND hs.bDaXoa = 0 AND hs.bDangTimViecCongKhai = 1) AS iTongCung,
        (SELECT ISNULL(SUM(t.iSoLuongTuyen), 0) FROM dbo.tbl_TinTuyenDung t
         WHERE t.FK_MaQuanLamViec = qh.PK_MaQuanHuyen
           AND t.bDaXoa = 0
           AND t.iTrangThaiDuyet = 1
           AND (@iMaNganhNghe IS NULL OR t.FK_MaNganhNghe = @iMaNganhNghe)
           AND (@dTuNgay IS NULL OR CAST(ISNULL(t.dNgayDuyet, GETDATE()) AS DATE) >= @dTuNgay)
           AND (@dDenNgay IS NULL OR CAST(ISNULL(t.dNgayDuyet, GETDATE()) AS DATE) <= @dDenNgay)) AS iTongCau,
        (SELECT COUNT(*) FROM dbo.tbl_UngTuyen u
         INNER JOIN dbo.tbl_TinTuyenDung t2 ON u.FK_MaTin = t2.PK_MaTin
         WHERE t2.FK_MaQuanLamViec = qh.PK_MaQuanHuyen
           AND u.bDaXoa = 0
           AND (@iMaNganhNghe IS NULL OR t2.FK_MaNganhNghe = @iMaNganhNghe)
           AND (@dTuNgay IS NULL OR CAST(u.dNgayNop AS DATE) >= @dTuNgay)
           AND (@dDenNgay IS NULL OR CAST(u.dNgayNop AS DATE) <= @dDenNgay)) AS iSoHoSoNop,
        (SELECT COUNT(*) FROM dbo.tbl_UngTuyen u2
         INNER JOIN dbo.tbl_TinTuyenDung t3 ON u2.FK_MaTin = t3.PK_MaTin
         WHERE t3.FK_MaQuanLamViec = qh.PK_MaQuanHuyen
           AND u2.bDaXoa = 0
           AND u2.iTrangThaiUngTuyen = 3
           AND (@iMaNganhNghe IS NULL OR t3.FK_MaNganhNghe = @iMaNganhNghe)
           AND (@dTuNgay IS NULL OR CAST(u2.dNgayNop AS DATE) >= @dTuNgay)
           AND (@dDenNgay IS NULL OR CAST(u2.dNgayNop AS DATE) <= @dDenNgay)) AS iSoTrungTuyen
    FROM dbo.tbl_QuanHuyen qh
    WHERE qh.bDaXoa = 0
      AND (@sMaQuanHuyen IS NULL OR LTRIM(RTRIM(@sMaQuanHuyen)) = '' OR qh.PK_MaQuanHuyen = @sMaQuanHuyen)
    ORDER BY qh.iThuTuHienThi ASC;
END;
GO

-- =============================================================================
-- PHẦN 5: TRIGGERS BẢO VỆ APPEND-ONLY & CHẶN XÓA CỨNG (MỤC 4.6.3 & 4.6.6)
-- =============================================================================

-- Trigger 1: Bảo vệ bảng Nhật ký hệ thống chỉ cho phép thêm mới (Append-Only)
CREATE TRIGGER dbo.trg_BaoVeNhatKyHeThong
ON dbo.tbl_NhatKyHeThong
INSTEAD OF UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    RAISERROR (N'Vi phạm bảo mật R28: Bảng tbl_NhatKyHeThong là nhật ký chỉ thêm mới (Append-Only), nghiêm cấm chỉnh sửa hoặc xóa.', 16, 1);
END;
GO

-- Trigger 2: Bảo vệ bảng Lịch sử ứng tuyển chỉ cho phép thêm mới (Append-Only)
CREATE TRIGGER dbo.trg_BaoVeLichSuUngTuyen
ON dbo.tbl_LichSuUngTuyen
INSTEAD OF UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    RAISERROR (N'Vi phạm tính toàn vẹn dữ liệu: Bảng tbl_LichSuUngTuyen là bảng lịch sử chỉ thêm mới (Append-Only).', 16, 1);
END;
GO

-- Trigger 3: Chặn xóa cứng danh mục Quận/Huyện (R27 & Mục 4.6.6 Soft Delete)
CREATE TRIGGER dbo.trg_ChanXoaCungQuanHuyen
ON dbo.tbl_QuanHuyen
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    RAISERROR (N'Quy tắc R27: Không được phép xóa cứng danh mục Quận/Huyện Hà Nội. Hãy cập nhật cờ xóa mềm bDaXoa = 1.', 16, 1);
END;
GO

-- =============================================================================
-- PHẦN 6: NẠP DỮ LIỆU MẪU (SEED DATA) ĐỦ 4 VAI TRÒ, 30 QUẬN/HUYỆN HÀ NỘI
-- Mật khẩu chung toàn bộ tài khoản mẫu: MatKhau@123
-- Hash BCrypt 60 ký tự hợp lệ: $2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy
-- =============================================================================

-- 6.1. Vai trò RBAC 4 cấp
INSERT INTO dbo.tbl_VaiTro (PK_MaVaiTro, sTenVaiTro) VALUES
(1, N'Người tìm việc'),
(2, N'Doanh nghiệp'),
(3, N'Cán bộ Trung tâm'),
(4, N'Quản trị viên');
GO

-- 6.2. Đầy đủ 30 Quận, Huyện, Thị xã Thành phố Hà Nội (R27)
INSERT INTO dbo.tbl_QuanHuyen (PK_MaQuanHuyen, sTenQuanHuyen, sKhuVuc, iThuTuHienThi, bDaXoa) VALUES
('QH001', N'Quận Ba Đình', N'Nội thành', 1, 0),
('QH002', N'Quận Hoàn Kiếm', N'Nội thành', 2, 0),
('QH003', N'Quận Hai Bà Trưng', N'Nội thành', 3, 0),
('QH004', N'Quận Đống Đa', N'Nội thành', 4, 0),
('QH005', N'Quận Tây Hồ', N'Nội thành', 5, 0),
('QH006', N'Quận Cầu Giấy', N'Nội thành', 6, 0),
('QH007', N'Quận Thanh Xuân', N'Nội thành', 7, 0),
('QH008', N'Quận Hoàng Mai', N'Nội thành', 8, 0),
('QH009', N'Quận Long Biên', N'Nội thành', 9, 0),
('QH010', N'Quận Hà Đông', N'Nội thành', 10, 0),
('QH011', N'Quận Bắc Từ Liêm', N'Nội thành', 11, 0),
('QH012', N'Quận Nam Từ Liêm', N'Nội thành', 12, 0),
('QH013', N'Thị xã Sơn Tây', N'Ngoại thành', 13, 0),
('QH014', N'Huyện Ba Vì', N'Ngoại thành', 14, 0),
('QH015', N'Huyện Chương Mỹ', N'Ngoại thành', 15, 0),
('QH016', N'Huyện Đan Phượng', N'Ngoại thành', 16, 0),
('QH017', N'Huyện Đông Anh', N'Ngoại thành', 17, 0),
('QH018', N'Huyện Gia Lâm', N'Ngoại thành', 18, 0),
('QH019', N'Huyện Hoài Đức', N'Ngoại thành', 19, 0),
('QH020', N'Huyện Mê Linh', N'Ngoại thành', 20, 0),
('QH021', N'Huyện Mỹ Đức', N'Ngoại thành', 21, 0),
('QH022', N'Huyện Phú Xuyên', N'Ngoại thành', 22, 0),
('QH023', N'Huyện Phúc Thọ', N'Ngoại thành', 23, 0),
('QH024', N'Huyện Quốc Oai', N'Ngoại thành', 24, 0),
('QH025', N'Huyện Sóc Sơn', N'Ngoại thành', 25, 0),
('QH026', N'Huyện Thạch Thất', N'Ngoại thành', 26, 0),
('QH027', N'Huyện Thanh Oai', N'Ngoại thành', 27, 0),
('QH028', N'Huyện Thanh Trì', N'Ngoại thành', 28, 0),
('QH029', N'Huyện Thường Tín', N'Ngoại thành', 29, 0),
('QH030', N'Huyện Ứng Hòa', N'Ngoại thành', 30, 0);
GO

-- 6.3. Danh mục Ngành nghề chuẩn Việt Nam (R26)
INSERT INTO dbo.tbl_NganhNghe (PK_MaNganhNghe, sTenNganhNghe, sNhomNganh, bTrangThaiHoatDong, bDaXoa) VALUES
(1, N'Công nghệ thông tin / Phần mềm', N'Kỹ thuật & Công nghệ', 1, 0),
(2, N'Kế toán / Kiểm toán / Tài chính', N'Tài chính & Kế toán', 1, 0),
(3, N'Kinh doanh / Bán hàng', N'Thương mại & Dịch vụ', 1, 0),
(4, N'Hành chính / Nhân sự / Văn phòng', N'Hành chính & Quản trị', 1, 0),
(5, N'Cơ khí / Điện / Tự động hóa', N'Sản xuất & Công nghiệp', 1, 0),
(6, N'Kho vận / Logistics / Giao nhận', N'Vận tải & Chuỗi cung ứng', 1, 0),
(7, N'Xây dựng / Kiến trúc / Bất động sản', N'Xây dựng & Hạ tầng', 1, 0),
(8, N'Marketing / Truyền thông / PR', N'Tiếp thị & Nội dung', 1, 0),
(9, N'Giáo dục / Đào tạo / Ngôn ngữ', N'Giáo dục & Nghiên cứu', 1, 0),
(10, N'Y tế / Dược phẩm / Chăm sóc sức khỏe', N'Y tế & An sinh', 1, 0),
(11, N'Du lịch / Nhà hàng / Khách sạn', N'Dịch vụ & Lưu trú', 1, 0),
(12, N'Lao động phổ thông / Thời vụ', N'Sản xuất & Dịch vụ cơ bản', 1, 0);
GO

-- 6.4. Tài khoản người dùng (Đủ 4 vai trò RBAC + Tài khoản bị khóa phục vụ kiểm thử)
INSERT INTO dbo.tbl_TaiKhoan (
    PK_MaTaiKhoan, sEmail, sMatKhau, FK_MaVaiTro, sHoTen, sSDT, bDaKichHoat, bBiKhoa, iSoLanDangNhapSai, sLyDoKhoa, bDaXoa, dNgayTao
) VALUES
('US00000001', 'minhanh@dvvlhanoi.vn', '$2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy', 1, N'Nguyễn Minh Anh', '0912345678', 1, 0, 0, NULL, 0, DATEADD(day, -30, GETDATE())),
('US00000002', 'thuha@dvvlhanoi.vn', '$2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy', 1, N'Phạm Thu Hà', '0987654321', 1, 0, 0, NULL, 0, DATEADD(day, -25, GETDATE())),
('US00000003', 'vanhung@dvvlhanoi.vn', '$2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy', 1, N'Trần Văn Hùng', '0934567890', 1, 0, 0, NULL, 0, DATEADD(day, -20, GETDATE())),
('US00000004', 'tuyendung@minhan.vn', '$2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy', 2, N'Trần Hoàng Nam', '0901112233', 1, 0, 0, NULL, 0, DATEADD(day, -28, GETDATE())),
('US00000005', 'hr@thanglongtech.vn', '$2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy', 2, N'Lê Thị Phương', '0904445566', 1, 0, 0, NULL, 0, DATEADD(day, -18, GETDATE())),
('US00000006', 'contact@hoangha.vn', '$2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy', 2, N'Nguyễn Quốc Bảo', '0907778899', 1, 0, 0, NULL, 0, DATEADD(day, -5, GETDATE())),
('US00000007', 'canbo.lan@dvvlhanoi.gov.vn', '$2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy', 3, N'Phạm Thu Lan', '0977888999', 1, 0, 0, NULL, 0, DATEADD(day, -60, GETDATE())),
('US00000008', 'admin.quang@dvvlhanoi.gov.vn', '$2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy', 4, N'Hoàng Quốc Quang', '0999888777', 1, 0, 0, NULL, 0, DATEADD(day, -90, GETDATE())),
('US00000009', 'vipham@dvvlhanoi.vn', '$2a$10$1/lxftMFyNPMMjNRmHIB9utCz4He5VIFjfs.0jTSQpm12W2Lz0.Gy', 1, N'Ngô Văn Kiên', '0966554433', 1, 1, 0, N'Tài khoản bị tạm khóa do spam hồ sơ không đúng quy chế.', 0, DATEADD(day, -15, GETDATE()));
GO

-- 6.5. Hồ sơ Người tìm việc chuẩn hóa (R06–R09)
INSERT INTO dbo.tbl_HoSoNguoiTimViec (
    PK_MaUngVien, FK_MaTaiKhoan, sHoTen, sSoCCCD, dNgaySinh, sGioiTinh, sSDT, sEmailLienHe,
    FK_MaQuanCuTru, sTrinhDoVanHoa, sTrinhDoChuyenMon, sKyNangTinHoc, sTrinhDoNgoaiNgu,
    sKinhNghiemLamViec, sViTriMongMuon, fMucLuongKyVong, sDiaBanMongMuon, iTyLeHoanThien, bDangTimViecCongKhai, bDaXoa
) VALUES
('SK00000001', 'US00000001', N'Nguyễn Minh Anh', '001199012345', '1999-08-15', N'Nữ', '0912345678', 'minhanh@dvvlhanoi.vn',
 'QH006', N'12/12', N'Đại học - Kế toán Kiểm toán', N'MS Excel nâng cao, phần mềm kế toán MISA, Bravo', N'Tiếng Anh B2 (TOEIC 750)',
 N'03 năm làm Kế toán tổng hợp tại Công ty CP Thương mại Thành Công; lập báo cáo tài chính và kê khai thuế GTGT/TNDN.',
 N'Kế toán tổng hợp', 14000000, N'Quận Cầu Giấy, Quận Đống Đa, Quận Nam Từ Liêm', 100, 1, 0),
('SK00000002', 'US00000002', N'Phạm Thu Hà', '001198054321', '1998-04-20', N'Nữ', '0987654321', 'thuha@dvvlhanoi.vn',
 'QH004', N'12/12', N'Đại học - Quản trị nhân lực', N'Thành thạo Word, Excel, phần mềm chấm công & BHXH', N'Tiếng Anh B1',
 N'02 năm làm Chuyên viên Hành chính Nhân sự: tuyển dụng, tính lương và giải quyết chế độ bảo hiểm xã hội.',
 N'Chuyên viên Hành chính Nhân sự', 12000000, N'Quận Đống Đa, Quận Thanh Xuân, Quận Hai Bà Trưng', 100, 1, 0),
('SK00000003', 'US00000003', N'Trần Văn Hùng', '001095098765', '1995-11-10', N'Nam', '0934567890', 'vanhung@dvvlhanoi.vn',
 'QH017', N'12/12', N'Cao đẳng - Kỹ thuật Điện công nghiệp', N'Đọc bản vẽ AutoCAD 2D, vận hành tủ điện PLC cơ bản', N'Tiếng Anh giao tiếp cơ bản',
 N'04 năm làm Kỹ thuật viên bảo trì hệ thống điện xưởng sản xuất tại Khu công nghiệp Bắc Thăng Long.',
 N'Kỹ thuật viên Điện / Tự động hóa', 13000000, N'Huyện Đông Anh, Quận Long Biên', 100, 1, 0);
GO

-- 6.6. Doanh nghiệp tuyển dụng (02 Đã xác minh, 01 Chờ thẩm định phục vụ R14)
INSERT INTO dbo.tbl_DoanhNghiep (
    PK_MaDoanhNghiep, FK_MaTaiKhoan, sTenDoanhNghiep, sTenVietTat, sMaSoThue, sDuongDanGiayPhep,
    sDiaChiTruSo, FK_MaQuanTruSo, sLinhVucHoatDong, sNguoiDaiDien, sTenNguoiLienHe, sSDTNguoiLienHe, sEmailNguoiLienHe,
    iTrangThaiThamDinh, sGhiChuThamDinh, bDaXoa
) VALUES
('DN00000001', 'US00000004', N'Công ty TNHH Thương mại và Dịch vụ Minh An', N'Minh An Co., Ltd', '0101234567', NULL,
 N'Số 18 phố Dịch Vọng Hậu, phường Dịch Vọng Hậu', 'QH006', N'Thương mại, Kế toán & Phân phối hàng tiêu dùng',
 N'Trần Hoàng Nam', N'Nguyễn Lan Anh', '0901112233', 'tuyendung@minhan.vn', 1, N'Hồ sơ pháp lý hợp lệ theo cổng ĐKKD Quốc gia.', 0),
('DN00000002', 'US00000005', N'Công ty Cổ phần Công nghệ Thăng Long', N'Thăng Long Tech', '0109876543', NULL,
 N'Tầng 8, Tòa nhà CIC, số 2 ngõ 219 Trung Kính', 'QH006', N'Phát triển phần mềm & Hạ tầng chuyển đổi số',
 N'Lê Thị Phương', N'Đặng Minh Tuấn', '0904445566', 'hr@thanglongtech.vn', 1, N'Đã xác minh Giấy phép ĐKKD và mã số thuế.', 0),
('DN00000003', 'US00000006', N'Công ty TNHH Cơ khí Hoàng Hà Hà Nội', N'Cơ khí Hoàng Hà', '0105566778', NULL,
 N'Lô CN-04 Khu công nghiệp Thạch Thất - Quốc Oai', 'QH026', N'Gia công cơ khí chính xác & Lắp đặt dây chuyền',
 N'Nguyễn Quốc Bảo', N'Nguyễn Quốc Bảo', '0907778899', 'contact@hoangha.vn', 0, N'Hồ sơ mới nộp, đang chờ Cán bộ Trung tâm DVVL thẩm định.', 0);
GO

-- 6.7. Tin tuyển dụng (Đủ 4 trạng thái: Đã duyệt công khai, Chờ duyệt R15, Từ chối, Hết hạn)
INSERT INTO dbo.tbl_TinTuyenDung (
    PK_MaTin, FK_MaDoanhNghiep, sTieuDe, sViTriChucDanh, FK_MaNganhNghe, FK_MaQuanLamViec, sDiaChiCuThe,
    iSoLuongTuyen, sCapBac, sHinhThucLamViec, fLuongToiThieu, fLuongToiDa, sYeuCauKinhNghiem, sYeuCauHocVan,
    sMoTaCongViec, sCheDoDaiNgo, dHanNopHoSo, iTrangThaiDuyet, FK_MaCanBoDuyet, dNgayDuyet, sLyDoTuChoi, bDaDong, bDaXoa
) VALUES
('JB00000001', 'DN00000001', N'Tuyển Kế toán tổng hợp làm việc tại Cầu Giấy', N'Kế toán tổng hợp', 2, 'QH006', N'Số 18 Dịch Vọng Hậu, Cầu Giấy, Hà Nội',
 2, N'Nhân viên chính thức', N'Toàn thời gian', 12000000, 16000000, N'2 năm trở lên', N'Đại học',
 N'Kiểm tra hóa đơn chứng từ, hạch toán chi phí doanh thu trên MISA, đối chiếu công nợ và lập báo cáo thuế quý/năm.',
 N'Lương tháng 13, đóng BHXH/BHYT đầy đủ ngay sau thử việc, phụ cấp ăn trưa 800.000đ/tháng.',
 DATEADD(day, 60, GETDATE()), 1, 'US00000007', DATEADD(day, -7, GETDATE()), NULL, 0, 0),

('JB00000002', 'DN00000002', N'Lập trình viên Java Spring Boot / Next.js (Full-stack)', N'Kỹ sư phần mềm Full-stack', 1, 'QH006', N'Số 2 ngõ 219 Trung Kính, Cầu Giấy, Hà Nội',
 3, N'Nhân viên / Senior', N'Toàn thời gian', 18000000, 28000000, N'1 - 3 năm', N'Đại học',
 N'Phát triển các hệ thống dịch vụ công và thương mại điện tử trên nền Java Spring Boot, SQL Server và React/Next.js.',
 N'Thưởng dự án theo quý, khám sức khỏe định kỳ, nghỉ phép 15 ngày/năm, làm việc thứ 2 đến thứ 6.',
 DATEADD(day, 75, GETDATE()), 1, 'US00000007', DATEADD(day, -5, GETDATE()), NULL, 0, 0),

('JB00000003', 'DN00000001', N'Nhân viên Hành chính Nhân sự & Tuyển dụng', N'Chuyên viên Nhân sự', 4, 'QH004', N'Số 125 Thái Hà, Đống Đa, Hà Nội',
 1, N'Nhân viên', N'Toàn thời gian', 10000000, 14000000, N'1 năm trở lên', N'Cao đẳng / Đại học',
 N'Thực hiện công tác chấm công, lưu trữ hồ sơ nhân sự, hỗ trợ đăng tin tuyển dụng và làm thủ tục bảo hiểm.',
 N'Môi trường thân thiện, hỗ trợ gửi xe và ăn trưa tại văn phòng, tăng lương định kỳ hằng năm.',
 DATEADD(day, 50, GETDATE()), 1, 'US00000007', DATEADD(day, -4, GETDATE()), NULL, 0, 0),

('JB00000004', 'DN00000002', N'Kỹ thuật viên Hỗ trợ vận hành hệ thống & Helpdesk', N'Kỹ thuật viên CNTT', 1, 'QH012', N'Khu văn phòng Mỹ Đình, Nam Từ Liêm, Hà Nội',
 2, N'Nhân viên', N'Toàn thời gian', 9000000, 13000000, N'Không yêu cầu kinh nghiệm', N'Cao đẳng',
 N'Tiếp nhận xử lý sự cố mạng nội bộ, cài đặt máy trạm và hỗ trợ người dùng vận hành phần mềm.',
 N'Được đào tạo bài bản từ đầu, đầy đủ chế độ phúc lợi theo Luật Lao động.',
 DATEADD(day, 45, GETDATE()), 1, 'US00000007', DATEADD(day, -3, GETDATE()), NULL, 0, 0),

('JB00000005', 'DN00000001', N'Nhân viên Kho vận & Giao nhận nội thành Hà Nội', N'Nhân viên Kho vận', 6, 'QH009', N'Khu công nghiệp Sài Đồng B, Long Biên, Hà Nội',
 4, N'Lao động kỹ thuật / Phổ thông', N'Toàn thời gian', 8500000, 11500000, N'Dưới 1 năm', N'THPT / Trung cấp',
 N'Kiểm đếm hàng hóa xuất nhập kho theo phiếu, sắp xếp hàng hóa đúng vị trí và bàn giao cho đơn vị vận chuyển.',
 N'Phụ cấp chuyên cần, phụ cấp độc hại nhẹ, hỗ trợ nhà trọ cho lao động ngoại tỉnh.',
 DATEADD(day, 40, GETDATE()), 0, NULL, NULL, NULL, 0, 0),

('JB00000006', 'DN00000002', N'Chuyên viên Kiểm thử Phần mềm (QA / Tester)', N'Tester / QA Engineer', 1, 'QH006', N'Tòa nhà CIC Trung Kính, Cầu Giấy, Hà Nội',
 2, N'Nhân viên', N'Toàn thời gian', 12000000, 17000000, N'1 năm', N'Đại học',
 N'Thiết kế Test Plan, Test Case theo đặc tả SRS, kiểm thử API bằng Postman và kiểm thử giao diện Responsive.',
 N'Đầy đủ BHXH, thưởng lễ Tết, lộ trình thăng tiến lên QA Lead rõ ràng.',
 DATEADD(day, 55, GETDATE()), 0, NULL, NULL, NULL, 0, 0),

('JB00000007', 'DN00000001', N'Nhân viên Kinh doanh Thị trường (Đã hết hạn thử nghiệm)', N'Nhân viên Kinh doanh', 3, 'QH008', N'Giải Phóng, Hoàng Mai, Hà Nội',
 5, N'Nhân viên', N'Toàn thời gian', 9000000, 18000000, N'1 năm', N'Trung cấp',
 N'Tin tuyển dụng mẫu đã quá hạn nộp hồ sơ để kiểm chứng bộ lọc tự động ẩn tin hết hạn ở Mục 4.6.6.',
 N'Lương cứng + hoa hồng.',
 DATEADD(day, -10, GETDATE()), 1, 'US00000007', DATEADD(day, -40, GETDATE()), NULL, 0, 0);
GO

-- 6.8. Giao dịch Ứng tuyển mẫu (Đa dạng trạng thái 0, 1, 2, 3)
INSERT INTO dbo.tbl_UngTuyen (
    PK_MaUngTuyen, FK_MaTin, FK_MaUngVien, dNgayNop, iTrangThaiUngTuyen,
    dThoiGianPhongVan, sDiaDiemPhongVan, sHinhThucPhongVan, sGhiChuPhongVan, bDaXoa
) VALUES
('AP00000001', 'JB00000001', 'SK00000001', DATEADD(day, -3, GETDATE()), 2,
 DATEADD(day, 4, DATEADD(hour, 9, CAST(CAST(GETDATE() AS DATE) AS DATETIME))),
 N'Phòng họp tầng 3, số 18 Dịch Vọng Hậu, Cầu Giấy, Hà Nội', N'Trực tiếp',
 N'Vui lòng mang theo bản sao bằng đại học và chứng chỉ kế toán khi đến phỏng vấn.', 0),
('AP00000002', 'JB00000003', 'SK00000002', DATEADD(day, -2, GETDATE()), 1,
 NULL, NULL, NULL, N'Doanh nghiệp đã tiếp nhận hồ sơ và đang đánh giá kinh nghiệm.', 0),
('AP00000003', 'JB00000003', 'SK00000001', DATEADD(day, -1, GETDATE()), 0,
 NULL, NULL, NULL, N'Hồ sơ mới nộp, chờ doanh nghiệp xem xét.', 0),
('AP00000004', 'JB00000004', 'SK00000003', DATEADD(day, -10, GETDATE()), 3,
 DATEADD(day, -5, GETDATE()),
 N'Tòa nhà CIC Trung Kính, Cầu Giấy, Hà Nội', N'Trực tiếp',
 N'Chúc mừng ứng viên đã trúng tuyển vị trí Kỹ thuật viên Hỗ trợ.', 0);
GO

-- 6.9. Lịch sử chuyển đổi trạng thái ứng tuyển (Append-Only)
INSERT INTO dbo.tbl_LichSuUngTuyen (FK_MaUngTuyen, iTrangThaiCu, iTrangThaiMoi, FK_MaNguoiThucHien, sGhiChu, dThoiGianThayDoi) VALUES
('AP00000001', NULL, 0, 'US00000001', N'Ứng viên nộp hồ sơ trực tuyến vào vị trí Kế toán tổng hợp.', DATEADD(day, -3, GETDATE())),
('AP00000001', 0, 1, 'US00000004', N'Nhà tuyển dụng đã xem chi tiết hồ sơ và CV.', DATEADD(day, -2, GETDATE())),
('AP00000001', 1, 2, 'US00000004', N'Hẹn phỏng vấn trực tiếp tại văn phòng Cầu Giấy.', DATEADD(day, -1, GETDATE())),
('AP00000002', NULL, 0, 'US00000002', N'Ứng viên nộp hồ sơ trực tuyến vào vị trí Chuyên viên Nhân sự.', DATEADD(day, -2, GETDATE())),
('AP00000002', 0, 1, 'US00000004', N'Nhà tuyển dụng đã tiếp nhận hồ sơ.', DATEADD(day, -1, GETDATE())),
('AP00000003', NULL, 0, 'US00000001', N'Ứng viên nộp hồ sơ vào vị trí Chuyên viên Nhân sự.', DATEADD(day, -1, GETDATE())),
('AP00000004', NULL, 0, 'US00000003', N'Ứng viên nộp hồ sơ vào vị trí Kỹ thuật viên CNTT.', DATEADD(day, -10, GETDATE())),
('AP00000004', 0, 2, 'US00000005', N'Hẹn phỏng vấn chuyên môn trực tiếp.', DATEADD(day, -7, GETDATE())),
('AP00000004', 2, 3, 'US00000005', N'Ứng viên đạt yêu cầu chuyên môn, thông báo trúng tuyển.', DATEADD(day, -4, GETDATE()));
GO

-- 6.10. Nhật ký hệ thống khởi tạo (Append-Only)
INSERT INTO dbo.tbl_NhatKyHeThong (FK_MaTaiKhoan, sLoaiHanhDong, sMucDo, sDiaChiIP, sTrinhDuyet, dThoiGianGhi, sDuLieuCu, sDuLieuMoi) VALUES
('US00000007', N'THAM_DINH_DOANH_NGHIEP', 'INFO', '127.0.0.1', N'System Seed', DATEADD(day, -7, GETDATE()), N'{"iTrangThaiThamDinh":0}', N'{"PK_MaDoanhNghiep":"DN00000001","iTrangThaiThamDinh":1}'),
('US00000007', N'PHE_DUYET_TIN_TUYEN_DUNG', 'INFO', '127.0.0.1', N'System Seed', DATEADD(day, -7, GETDATE()), N'{"iTrangThaiDuyet":0}', N'{"PK_MaTin":"JB00000001","iTrangThaiDuyet":1}'),
('US00000008', N'KHOA_TAI_KHOAN', 'SECURITY_ACTION', '127.0.0.1', N'System Seed', DATEADD(day, -2, GETDATE()), N'{"bBiKhoa":0}', N'{"PK_MaTaiKhoan":"US00000009","bBiKhoa":1,"sLyDo":"Spam"}');
GO

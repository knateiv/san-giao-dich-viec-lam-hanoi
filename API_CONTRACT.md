# API_CONTRACT.md — HỢP ĐỒNG DỮ LIỆU, PHÂN QUYỀN & NGHIỆP VỤ (`SE.G01`)

Tài liệu đặc tả hợp đồng giao tiếp giữa **Frontend Next.js**, **Backend Java Spring Boot (`Controller/C*`, `Dto/V*`, `Model/M*`)** và **SQL Server (`db_SanGiaoDichViecLamHN`)** cho toàn bộ **28 chức năng (`R01` → `R28`)**.

---

## 1. Phân Quyền RBAC 4 Vai Trò (`tbl_VaiTro`)

| `PK_MaVaiTro` | Tên vai trò (`sTenVaiTro`) | Đường dẫn đích sau đăng nhập | Phân hệ & Chức năng được phép |
| :---: | :--- | :--- | :--- |
| **Chưa đăng nhập** | Khách vãng lai | `/` | Xem trang chủ, tìm việc (`R18`), đăng ký (`R01`), đăng nhập (`R02`) |
| `1` | **Người tìm việc** | `/ung-vien/ho-so` | Tài khoản (`R04`), Hồ sơ & CV PDF (`R06`–`R09`), Tìm việc (`R18`), Ứng tuyển/Theo dõi/Hủy (`R20`, `R22`, `R23`) |
| `2` | **Doanh nghiệp** | `/doanh-nghiep` | Tài khoản (`R04`), Pháp lý DN (`R10`), Tin tuyển dụng (`R11`–`R13`), Tra cứu ứng viên (`R19`), Xét duyệt & Lịch PV (`R21`), Xem việc (`R18`) |
| `3` | **Cán bộ Trung tâm** | `/can-bo` | Tài khoản (`R04`), Thẩm định DN (`R14`), Duyệt/Từ chối/Gỡ khẩn cấp tin (`R15`, `R16`), Cấp Giấy giới thiệu PDF (`R17`), In Phiếu NTV (`R09`), Tìm việc & Ứng viên (`R18`, `R19`), Thống kê & Báo cáo (`R24`, `R25`) |
| `4` | **Quản trị viên** | `/quan-tri` | Toàn bộ quyền quản trị tài khoản/RBAC/khóa mở (`R05`), Gỡ tin khẩn cấp (`R16`), Khóa tự động (`R03`), Ngành nghề & 30 Quận/Huyện (`R26`, `R27`), Thống kê & Báo cáo (`R24`, `R25`), Audit Log (`R28`), Tài khoản (`R04`) |

---

## 2. Quy Ước Đặt Tên Bảng & Cột SQL Server (§ 2.2 & § 4.6)

- **Tiền tố**: Database `db_`, Table `tbl_`, Khóa chính `PK_`, Khóa ngoại `FK_`, View `vv_`, Stored Procedure `sp_`.
- **Tiền tố kiểu cột**: `i` (`INT`), `f` (`FLOAT`), `s` (`CHAR`/`VARCHAR`/`NVARCHAR`), `d` (`DATE`/`DATETIME`), `b` (`BIT`).
- **Quy tắc hiển thị tin công khai (§ 4.6.6 — `vv_TinTuyenDungCongKhai`)**:
  1. `iTrangThaiDuyet = 1` (Đã duyệt bởi Cán bộ TT DVVL)
  2. `dHanNopHoSo >= CAST(GETDATE() AS DATE)` (Còn hạn nộp)
  3. `bDaDong = 0 AND bDaXoa = 0` (Chưa đóng và chưa xóa mềm)
- **Xóa mềm (Soft Delete)**: Mọi bảng nghiệp vụ & danh mục (trừ `tbl_VaiTro`, `tbl_NhatKyHeThong`, `tbl_LichSuUngTuyen`) sử dụng `bDaXoa = 1`, không `DELETE` vật lý.

---

## 3. Máy Trạng Thái Nghiệp Vụ

### 3.1. Thẩm định Doanh nghiệp (`tbl_DoanhNghiep.iTrangThaiThamDinh`)
- `0`: Chờ thẩm định *(mặc định khi mới gửi `R10`)*
- `1`: Đã xác minh *(mới được đăng tin `R11` và tra cứu ứng viên công khai `R19`)*
- `2`: Bị từ chối *(bắt buộc có `sGhiChuThamDinh`)*

### 3.2. Kiểm duyệt Tin tuyển dụng (`tbl_TinTuyenDung.iTrangThaiDuyet`)
- `0`: Chờ duyệt *(mặc định khi tạo `R11` hoặc khi sửa nội dung trọng yếu `R12`)*
- `1`: Đã duyệt *(công khai khi còn hạn & `bDaDong = 0`)*
- `2`: Từ chối *(bắt buộc nhập `sLyDoTuChoi`)*
- `3`: Bị gỡ khẩn cấp (`R16` — đồng thời đặt `bDaXoa = 1` và ghi `tbl_NhatKyHeThong`)

### 3.3. Tiến trình Ứng tuyển (`tbl_UngTuyen.iTrangThaiUngTuyen`)
- `0`: Chờ duyệt *(ngay khi NTV nộp `R20`)*
- `1`: Đã tiếp nhận / Đã xem *(tự động chuyển khi Doanh nghiệp mở xem chi tiết lần đầu `R21`, hoặc cập nhật thủ công)*
- `2`: Hẹn phỏng vấn *(bắt buộc `dThoiGianPhongVan`, `sDiaDiemPhongVan`, `sHinhThucPhongVan`)*
- `3`: Trúng tuyển
- `4`: Từ chối
- `5`: Đã hủy *(NTV chỉ được hủy `R23` khi trạng thái hiện tại là `0`)*

Mỗi lần chuyển trạng thái đều **ghi thêm 1 bản ghi Append-Only vào `tbl_LichSuUngTuyen`** và **lưu vết `tbl_NhatKyHeThong`**.

---

## 4. Hợp Đồng Endpoint RESTful API (`Controller/C*`)

| Controller | Prefix | Endpoints & Mã Yêu Cầu |
| :--- | :--- | :--- |
| `CTaiKhoan` | `/api/auth`, `/api/tai-khoan`, `/api/quan-tri/tai-khoan` | `POST /api/auth/dang-ky` (`R01`), `POST /api/auth/kich-hoat` (`R01`), `POST /api/auth/dang-nhap` (`R02`, `R03`), `POST /api/auth/dang-xuat`, `GET /api/auth/me`, `PUT /api/tai-khoan/cap-nhat` (`R04`), `PUT /api/tai-khoan/doi-mat-khau` (`R04`), `POST /api/tai-khoan/anh-dai-dien` (`R04`), `GET /api/quan-tri/tai-khoan` (`R05`), `PUT /api/quan-tri/tai-khoan/{id}/phan-quyen` (`R05`), `PUT /api/quan-tri/tai-khoan/{id}/khoa-mo` (`R03`, `R05`) |
| `CNguoiTimViec` | `/api/nguoi-tim-viec` | `GET /ho-so`, `PUT /ho-so` (`R06`), `PATCH /trang-thai-tim-viec` (`R07`), `POST /upload-cv` (`R08`), `GET /cv-file/{maUngVien}` (`R08`), `GET /phieu-dang-ky-pdf/{maUngVien}` (`R09`) |
| `CDoanhNghiep` | `/api/doanh-nghiep` | `GET /ho-so`, `PUT /ho-so` (`R10`), `POST /upload-giay-phep` (`R10`), `GET /giay-phep/{maDoanhNghiep}` (`R10`) |
| `CTinTuyenDung` | `/api/tin-tuyen-dung` | `GET /cua-toi`, `POST /` (`R11`), `PUT /{maTin}` (`R12`), `PATCH /{maTin}/dong-tin` (`R12`), `PATCH /{maTin}/gia-han` (`R12`), `GET /{maTin}/ung-vien` (`R13`) |
| `CPheDuyet` | `/api/kiem-duyet` | `GET /doanh-nghiep`, `PUT /doanh-nghiep/{maDoanhNghiep}` (`R14`), `GET /tin-tuyen-dung`, `PUT /tin-tuyen-dung/{maTin}` (`R15`), `PUT /tin-tuyen-dung/{maTin}/go-khan-cap` (`R16`), `GET /giay-gioi-thieu-pdf` (`R17`) |
| `CTimViec` | `/api/tim-viec` | `GET /` (`R18`), `GET /{maTin}` (`R18`), `GET /ung-vien-cong-khai` (`R19`), `POST /moi-ung-tuyen` (`R19`), `GET /danh-muc` (`R26`, `R27`) |
| `CUngTuyen` | `/api/ung-tuyen` | `POST /` (`R20`), `GET /chi-tiet/{maUngTuyen}` (`R21`), `PUT /{maUngTuyen}/tien-trinh` (`R21`), `GET /lich-su-cua-toi` (`R22`), `PATCH /{maUngTuyen}/xac-nhan-pv` (`R22`), `PUT /{maUngTuyen}/huy` (`R23`) |
| `CThongKeQuanTri` | `/api/thong-ke`, `/api/quan-tri` | `GET /api/thong-ke/cung-cau` (`R24`), `GET /api/thong-ke/xuat-pdf` (`R25`), `GET /api/thong-ke/xuat-excel` (`R25`), `GET/POST/PUT/DELETE /api/quan-tri/nganh-nghe` (`R26`), `GET/POST/PUT/DELETE /api/quan-tri/quan-huyen` (`R27`), `GET /api/quan-tri/nhat-ky` (`R28`), `GET /api/quan-tri/nhat-ky/xuat-excel` (`R28`) |

# Sàn Giao Dịch Việc Làm Trực Tuyến — Trung tâm Dịch vụ Việc làm Hà Nội (Nhóm SE.G01)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-15-black.svg)](https://nextjs.org/)
[![SQL Server](https://img.shields.io/badge/Database-MS%20SQL%20Server-red.svg)](https://www.microsoft.com/en-us/sql-server)

Hệ thống phần mềm hoàn chỉnh được phát triển theo **Phương án A (Mục 4.4 Báo cáo SE.G01)** — đáp ứng **100% 6 phân hệ nghiệp vụ / 28 yêu cầu chức năng (`R01` → `R28`)**, quy tắc đặt tên chuẩn **FIT HOU Mục 2.2** và lược đồ 10 bảng vật lý **Mục 4.6**.

## 1. Kiến trúc & Công nghệ (Option A)

| Tầng | Công nghệ | Thư mục / Cổng mặc định |
| :--- | :--- | :--- |
| **Cơ sở dữ liệu** | Microsoft SQL Server (`db_SanGiaoDichViecLamHN`, LocalDB TCP Bridge) | `Database/`, cổng `127.0.0.1:14333` |
| **Backend API** | Java 21 + Spring Boot 3.3.5 (Spring Security, BCrypt, JWT, JPA, OpenPDF UTF-8 + ZXing QR, Apache POI Excel) | `Backend/`, cổng `http://127.0.0.1:8080` |
| **Frontend Web** | Next.js 15 App Router + TypeScript + Tailwind CSS (Responsive Desktop/Mobile) | `Frontend/`, cổng `http://localhost:3000` |

## 2. Cấu trúc thư mục bàn giao

- `Database/db_san_giao_dich_viec_lam_hn.sql`: Khởi tạo CSDL `db_SanGiaoDichViecLamHN`, 10 bảng `tbl_*`, 4 chỉ mục tối ưu tra cứu (`IX_*`), Views (`vv_TinTuyenDungCongKhai`, `vv_HoSoUngVienCongKhai`, `vv_ThongKeCungCauTheoNganh`), Stored Procedures (`sp_TimKiemViecLamDaTieuChi`, `sp_ThongKeCungCauLaoDong`), Triggers bảo vệ Append-Only & chặn xóa cứng, cùng dữ liệu mẫu thực tế (30 quận/huyện Hà Nội, 12 ngành nghề, 9 tài khoản BCrypt).
- `Backend/`: Mã nguồn Spring Boot phân lớp `Model/M*`, `Dto/V*`, `Controller/C*`, `Repository/`, `Service/`, `Config/`, và kiểm thử tích hợp `SanGiaoDichIntegrationTests.java`.
- `Frontend/`: Ứng dụng Next.js 15 thực thi qua rewrite `/api/*` tới `:8080`, triển khai đủ giao diện cho Khách, Người tìm việc, Doanh nghiệp, Cán bộ DVVL và Admin.
- `Scripts/`:
  - `cai_moi_truong.bat`: Tự động cài **Node.js LTS** và **Microsoft OpenJDK 21** qua `winget` cho máy chưa có môi trường.
  - `init_database.bat`: Khởi tạo CSDL `db_SanGiaoDichViecLamHN` và tài khoản `dvvl_app` trên máy mới.
  - `start_all.bat`: Tự động build bằng Maven Wrapper (`mvnw.cmd`, không cần cài Maven riêng) & npm, rồi khởi động SQL Bridge (`14333`) + Backend (`8080`) + Frontend (`3000`).
  - `sqlserver_localdb_bridge.mjs`: Cầu nối TCP -> LocalDB Named Pipe.
- `BAO_CAO_CHUONG_5_6_VA_DONG_BO.md`: Nội dung hoàn chỉnh Chương 5, Chương 6 (ánh xạ đúng 12 thành viên Bảng 1.7 + Test Case IEEE 829) và danh sách đồng bộ báo cáo Word.

## 3. Tài khoản kiểm thử nhanh (Dữ liệu thật trong SQL Server)

Mật khẩu dùng chung: `MatKhau@123` (lưu dạng BCrypt 60 ký tự trong `tbl_TaiKhoan`)

| Vai trò | Email đăng nhập | Đường dẫn không gian làm việc |
| :--- | :--- | :--- |
| **Người tìm việc** | `minhanh@dvvlhanoi.vn` | `/ung-vien/ho-so`, `/ung-vien/ung-tuyen` |
| **Doanh nghiệp (Đã xác minh)** | `tuyendung@minhan.vn` | `/doanh-nghiep`, `/doanh-nghiep/dang-tin` |
| **Doanh nghiệp (Chờ thẩm định)** | `contact@hoangha.vn` | `/doanh-nghiep/phap-ly` |
| **Cán bộ Trung tâm DVVL** | `canbo.lan@dvvlhanoi.gov.vn` | `/can-bo`, `/can-bo/giay-gioi-thieu`, `/can-bo/bao-cao` |
| **Quản trị viên (Admin)** | `admin.quang@dvvlhanoi.gov.vn` | `/quan-tri`, `/quan-tri/danh-muc`, `/quan-tri/nhat-ky` |

## 4. Hướng dẫn cài đặt & chạy trên máy mới

Yêu cầu môi trường: **SQL Server LocalDB** (hoặc SQL Server Express), **JDK 21**, **Node.js 20+** *(đã tích hợp sẵn `mvnw.cmd`, không cần cài Maven rời)*.

```bash
git clone https://github.com/knateiv/san-giao-dich-viec-lam-hanoi.git
cd san-giao-dich-viec-lam-hanoi
```

1. **Bước 0 (Nếu máy báo thiếu `node` hoặc `java`)**: Chạy `Scripts\cai_moi_truong.bat` để tự cài Node.js LTS & OpenJDK 21.
2. **Bước 1 (chạy lần đầu)**: Chạy `Scripts\init_database.bat` để tự tạo CSDL `db_SanGiaoDichViecLamHN` trên SQL Server LocalDB.
   *(Nếu dùng SQL Server Express cổng 1433 thay cho LocalDB, chạy `db_san_giao_dich_viec_lam_hn.sql` trong SSMS rồi khởi chạy Backend với profile `--spring.profiles.active=sqlexpress`.)*
3. **Bước 2**: Chạy `Scripts\start_all.bat` — hệ thống tự động biên dịch (nếu chưa build), khởi động 3 tiến trình và mở trình duyệt tại `http://localhost:3000`.

## 5. Giấy phép Mã nguồn mở (License)

Dự án được phát hành theo giấy phép mã nguồn mở [MIT License](./LICENSE).

# ĐỀ XUẤT CHƯƠNG 5, CHƯƠNG 6 VÀ ĐỒNG BỘ BÁO CÁO BTL (`SE.G01`)

Tài liệu này tổng hợp nội dung hoàn thiện cho **Chương 5**, **Chương 6** và **4 điểm rà soát đồng bộ** đối chiếu trực tiếp với tệp báo cáo gốc (`AAW024.G01.Baocao.Template.2026.09_(1).docx`) và hệ thống phần mềm thực tế tại `D:\san-giao-dich-viec-lam-hanoi\`.

---

## PHẦN A — 4 ĐIỂM CẦN ĐỒNG BỘ TRONG BÁO CÁO WORD HIỆN TẠI

1. **Chuẩn hóa danh sách thành viên (Bảng 1.6 vs Bảng 1.7)**:
   - Tại Bảng 1.6 dòng 12 đang ghi thiếu chữ `h` (`Tiêu Thị Vân An`), cần sửa thành **`Tiêu Thị Vân Anh`** để khớp với Bảng 1.7 (dòng 4, 5, 12); đồng thời bỏ khoảng trắng thừa ở dòng 4 (`Vũ  Viết Long` -> `Vũ Viết Long`).
2. **Sửa lỗi chính tả tiêu đề PFD Mục 4.5.2 (R23 – R28)**:
   - Sửa khoảng trắng số thứ tự `PFD 4.5.2.2 3–` -> `PFD 4.5.2.23 –`, `PFD 4.5.2.2 4–` -> `PFD 4.5.2.24 –`.
   - Sửa lỗi chính tả `Quản lý doanh mục ngành nghề` / `Quản lý doanh mục địa bàn` -> **`Quản lý danh mục ngành nghề chuẩn` (`R26`)** và **`Quản lý danh mục địa bàn quận/huyện Hà Nội` (`R27`)**.
3. **Thống nhất tên mã nhóm & tên đề tài**:
   - Tên tệp mẫu là `AAW024.G01` trong khi trang bìa/dòng 8 và toàn bộ tài liệu ghi **`SE.G01`** — thống nhất sử dụng tên nhóm **`SE.G01`** và tên đề tài *"Phát triển phần mềm Sàn giao dịch việc làm trực tuyến - Trung tâm Dịch vụ việc làm Hà Nội"*.
4. **Điền hoàn chỉnh Chương 5 & Chương 6 thay thế các dấu `….` giữ chỗ**:
   - Đưa toàn bộ mục 5.1, 5.2 và 6.1–6.3 dưới đây (kèm ảnh chụp thực tế sau khi nghiệm thu) vào báo cáo Word.

---

## PHẦN B — NỘI DUNG CHƯƠNG 5. XÂY DỰNG VÀ KIỂM THỬ HỆ THỐNG

### 5.1. Kết quả xây dựng module/nhóm chức năng (Đạt 100% 6 phân hệ: R01 → R28)

Hệ thống được hiện thực hóa theo đúng **Phương án A (Mục 4.4)** với kiến trúc 3 tầng tách biệt: CSDL **Microsoft SQL Server (`db_SanGiaoDichViecLamHN`)**, Backend **Java 21 Spring Boot 3.3.5**, và Frontend **Next.js 15 + Tailwind CSS**.

#### 5.1.1. Phân công phát triển mã nguồn & CSDL theo Bảng 1.7

| STT | Nhóm chức năng / Thành phần | Công việc hiện thực hóa thực tế trong mã nguồn | Thành viên chịu trách nhiệm (theo Bảng 1.7) |
| :-: | :--- | :--- | :--- |
| 1 | **CSDL vật lý SQL Server (`db_SanGiaoDichViecLamHN`)** | Triển khai đủ 10 bảng `tbl_*`, 4 chỉ mục `IX_*`, Views `vv_*`, Stored Procedures `sp_TimKiemViecLamDaTieuChi`, `sp_ThongKeCungCauLaoDong`, Triggers Append-Only & chặn xóa cứng; dữ liệu thực 30 Quận/Huyện Hà Nội. | **P: Nguyễn Minh Đức**<br>S: Đỗ Quang Huy |
| 2 | **Nền tảng Backend, Bảo mật & Phân quyền (`R01`–`R05`, `R33`–`R37`)** | Xây dựng `SecurityConfig`, `JwtAuthenticationFilter` (tự hủy hiệu lực token khi Admin khóa hoặc đổi vai trò), BCrypt 60 ký tự, khóa tạm 15 phút sau 5 lần sai (`CTaiKhoan`), `AuditLogService` ghi vết. | **P: Đỗ Quang Huy**<br>S: Nguyễn Minh Đức |
| 3 | **Giao diện Responsive & Biểu mẫu PDF/Excel (`R09`, `R17`, `R25`, `R28`)** | Xây dựng 16 màn hình App Router chuẩn Civic UI `#215DA8` trên desktop & mobile, kết xuất PDF A4 UTF-8 Times New Roman + mã QR (`PdfExportService`) và Excel `.xlsx` có công thức `SUM` (`ExcelExportService`). | **P: Vũ Viết Long**<br>S: Phạm Dương Phúc |
| 4 | **Chuẩn hóa dữ liệu & Danh mục (`OBJ-03`: `R06`–`R09`, `R26`, `R27`)** | Phát triển `CNguoiTimViec` (hồ sơ ứng viên, CV PDF Magic Bytes `%PDF-`, công khai tìm việc, `TextNormalizer`), CRUD Ngành nghề & 30 Quận/Huyện (`CThongKeQuanTri`) với ràng buộc chống xóa cứng. | **P: Ngô Việt Anh**<br>S: Tiêu Thị Vân Anh |
| 5 | **Nghiệp vụ Doanh nghiệp tuyển dụng (`R10`–`R13`, `R19`, `R21`)** | Phát triển `CDoanhNghiep`, `CTinTuyenDung` (đăng/sửa/đóng/gia hạn tin), ngân hàng ứng viên công khai có che CCCD/SĐT/Email (`vv_HoSoUngVienCongKhai`) và mời ứng tuyển. | **P: Đỗ Quang Huy**<br>S: Nguyễn Minh Đức |
| 6 | **Kết nối Cung-Cầu & Tiến trình 2 giây (`OBJ-01, OBJ-04`: `R18`, `R20`–`R23`)** | Phát triển `CTimViec` (tìm kiếm đa tiêu chí AND qua Stored Procedure), `CUngTuyen` (nộp 1 chạm, auto-chuyển `0 -> 1 Đã xem`, mời phỏng vấn, lịch sử biến động, đồng bộ polling 2s & rút hồ sơ). | **P: Nguyễn Bảo Lâm**<br>S: Ngô Việt Anh |
| 7 | **Kiểm soát thông tin & Báo cáo Trung tâm (`OBJ-02, OBJ-05`: `R14`–`R17`, `R24`, `R25`, `R28`)** | Phát triển `CPheDuyet` (thẩm định ĐKKD, duyệt/từ chối/gỡ khẩn cấp tin vi phạm, cấp Giấy giới thiệu QR), thống kê cung-cầu (`ThongKeService`) + biểu đồ SVG, và giám sát Nhật ký hệ thống. | **P: Đỗ Minh Hoàng**<br>S: Ngô Quý Vinh |
| 8 | **Tích hợp mã nguồn & Xử lý lỗi tích hợp** | Cầu nối LocalDB TCP `sqlserver_localdb_bridge.mjs`, Next.js API Proxy `/api/*`, chuẩn hóa Jackson JSON camelCase trên toàn bộ Models/DTOs. | **P: Nguyễn Minh Đức**<br>S: Đỗ Minh Hoàng |

#### 5.1.2. Tuân thủ Quy tắc đặt tên chuẩn FIT HOU (Mục 2.2)

- **CSDL**: `db_SanGiaoDichViecLamHN`, bảng `tbl_VaiTro`, `tbl_TaiKhoan`, `tbl_QuanHuyen`, `tbl_NganhNghe`, `tbl_HoSoNguoiTimViec`, `tbl_DoanhNghiep`, `tbl_TinTuyenDung`, `tbl_UngTuyen`, `tbl_LichSuUngTuyen`, `tbl_NhatKyHeThong`; View `vv_*`; Stored Procedure `sp_*`; tiền tố cột `i/f/s/d/b` khớp 100% Mục 4.6.3.
- **Mã nguồn**: Backend phân lớp `Model/M*`, `Dto/V*`, `Controller/C*`; Frontend gán định danh chuẩn `tb*`, `cbb*`, `rdb*`, `cb*`, `dgv*`, `pn*`, `nud*`, `btn*` trên các control nghiệp vụ chính.

---

### 5.2. Kiểm thử hệ thống

#### 5.2.1. Tổng quan & 5.2.2. Mô hình kiểm thử áp dụng
Nhóm áp dụng **Mô hình kiểm thử chữ V (V-Model)** ở 3 cấp độ: Kiểm thử tích hợp Backend trên CSDL SQL Server thật (`SpringBootTest` + `MockMvc` tại `SanGiaoDichIntegrationTests.java`), Kiểm thử tĩnh & build sản phẩm (`mvn package` + `next build` + `tsc`), và Kiểm thử chấp nhận đầu-cuối đa vai trò trên trình duyệt thực (`Playwright E2E` Desktop & Mobile).

#### 5.2.3. Kế hoạch & Danh sách Test Case chuẩn IEEE 829

Phân công kiểm thử theo Bảng 1.7: **Chu Đình Vũ** (P #17 - Test Case & chức năng), **Vũ Viết Long** (P #18 - Responsive & in ấn PDF/Excel), **Phạm Dương Phúc** (P #19 - Tích hợp), **Phạm Thị Khánh & Đỗ Ngọc Hải** (Rà soát chất lượng & tổng hợp).

| Mã TC | Chức năng / Yêu cầu | Dữ liệu đầu vào & Các bước thực hiện | Kết quả mong đợi | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | `R01`, `R03` Đăng ký OTP & Khóa 15 phút | Đăng ký email mới -> Login trước OTP -> Kích hoạt OTP -> Nhập sai mật khẩu 5 lần liên tiếp | Chặn trước OTP (403); sai lần 1–4 báo 401; sai lần 5 khóa HTTP 423 (15 phút); nhập lại đúng mật khẩu vẫn bị 423 | **Pass** (Integration Test) |
| **TC-02** | `R02` Đăng nhập BCrypt & RBAC 4 cấp | Đăng nhập 4 tài khoản thật (`NTV`, `DN`, `CANBO`, `ADMIN`) với `MatKhau@123`; thử gọi API `/api/quan-tri/tai-khoan` bằng token `NTV` | Cấp `DVVL_TOKEN` + điều hướng đúng `duongDanDich`; `NTV` gọi API Admin bị chặn HTTP 403 | **Pass** (Integration & E2E) |
| **TC-03** | `R18` Tìm kiếm đa tiêu chí & Danh mục | Gọi `GET /api/tim-viec/danh-muc` và `GET /api/tim-viec?tuKhoa=Kế toán` | Trả về đủ 30 Quận/Huyện Hà Nội, 12 Ngành nghề, tìm được tin công khai còn hạn qua `sp_TimKiemViecLamDaTieuChi` | **Pass** (Integration & E2E) |
| **TC-04** | `R09`, `R17` Kết xuất PDF A4 + QR | Xuất Phiếu ĐK tìm việc (`SK00000001`) và Giấy giới thiệu việc làm (`SK00000001` -> `JB00000001`) | Trả về `application/pdf`, byte đầu `%PDF-`, font nhúng Unicode Times New Roman và mã QR | **Pass** (Integration & E2E) |
| **TC-05** | `R25`, `R28` Kết xuất Excel `.xlsx` & PDF Báo cáo | Xuất Báo cáo Cung-Cầu PDF/Excel và Nhật ký hệ thống Excel | Định dạng `.pdf` (`%PDF-`) và `.xlsx` (`PK` OOXML hợp lệ kèm công thức `SUM`) | **Pass** (Integration & E2E) |
| **TC-06** | `R06`–`R08`, `R20`–`R23` Luồng NTV & DN thời gian thực | Kiểm tra hiển thị UI NTV, lịch sử ứng tuyển polling 2s, DN quản lý tin & che CCCD/SĐT ứng viên công khai | Dữ liệu hiển thị đúng tiếng Việt, số CCCD/SĐT bị che (`****`), không lỗi Console / 4xx / 5xx | **Pass** (E2E) |
| **TC-07** | Responsive & Kiểm định `/hallmark` | Chụp & đo DOM trên Viewport Desktop (`1440x900`) và Mobile (`390x844`) | Không tràn ngang (`scrollWidth <= innerWidth`), 0 lỗi Console, 0 tên bảng `dbo.*` lộ trên UI | **Pass** (E2E) |

#### 5.2.4. Kết quả kiểm thử
- **Backend (`mvn test`)**: 4/4 nhóm ca kiểm thử tích hợp trên SQL Server thật (`r02_bcrypDangNhapVaPhanQuyenRbac`, `r01_r03_dangKyOtpVaKhoa15PhutSau5LanSai`, `r18_timKiemViecLamDaTieuChiVaDanhMuc`, `r09_r17_r25_r28_xuatPdfVaExcelChuanDinhDang`) đạt **100% Pass (0 Failures, 0 Errors)**.
- **Frontend (`next build`)**: Biên dịch thành công toàn bộ 16 màn hình & TypeScript strict mode không lỗi.
- **End-to-End (`Playwright`)**: Kiểm chứng thực tế cả 4 vai trò trên stack `:3000 -> :8080 -> :14333` (LocalDB).

---

## PHẦN C — NỘI DUNG CHƯƠNG 6. KẾT LUẬN

### 6.1. Đánh giá chất lượng phần mềm
Phần mềm **Sàn Giao Dịch Việc Làm Trực Tuyến — Trung tâm Dịch vụ việc làm Hà Nội** đã hoàn thiện đầy đủ 6 phân hệ (28 yêu cầu chức năng `R01`–`R28`) cùng các yêu cầu phi chức năng (`R29`–`R37`). Hệ thống vận hành thông suốt trên bộ công nghệ chuẩn công nghiệp **SQL Server + Spring Boot + Next.js**, bảo đảm an toàn dữ liệu thông qua BCrypt, JWT RBAC, Trigger bảo vệ Audit Log Append-Only, chuẩn hóa văn bản đầu vào chống dính chữ và che dữ liệu nhạy cảm người lao động.

### 6.2. Ưu điểm và Hạn chế của đề tài
- **Ưu điểm**:
  1. **Khớp nghiệp vụ nhà nước & 30 địa bàn Hà Nội**: Hỗ trợ đầy đủ quy trình 2 tầng kiểm duyệt (thẩm định GPKD doanh nghiệp trước khi duyệt tin đăng) và kết xuất ngay biểu mẫu PDF A4 kèm mã QR phục vụ công tác điều phối hành chính.
  2. **Đồng bộ chặt chẽ Thiết kế ↔ CSDL ↔ Mã nguồn**: Từ lược đồ 10 bảng, Views/Stored Procedures đến giao diện và quy ước đặt tên Mục 2.2 đều nhất quán.
  3. **Trải nghiệm trực quan & đáp ứng đa thiết bị**: Giao diện sáng sạch chuẩn Civic UI, thao tác 1 chạm cho ứng viên và cập nhật trạng thái xét duyệt gần thời gian thực.
- **Hạn chế**:
  1. Chức năng gửi mã OTP kích hoạt tài khoản đang chạy ở chế độ mô phỏng hiển thị phục vụ môi trường kiểm thử nội bộ (chưa đấu nối SMTP/SMS Brandname thực tế).
  2. Chức năng tra cứu tin mới chỉ lọc theo thuộc tính cấu trúc trên SQL Server, chưa tích hợp gợi ý ghép cặp CV – Việc làm bằng trí tuệ nhân tạo.

### 6.3. Hướng phát triển
1. Tích hợp dịch vụ gửi thông báo OTP qua SMTP / Zalo ZNS / SMS Brandname và định danh điện tử VNeID chính thức.
2. Bổ sung công cụ **AI Matching** gợi ý việc làm phù hợp theo nội dung CV PDF của ứng viên và cảnh báo tin tuyển dụng có dấu hiệu lừa đảo.
3. Mở rộng cổng API liên thông báo cáo định kỳ trực tuyến với hệ thống dữ liệu thị trường lao động quốc gia.

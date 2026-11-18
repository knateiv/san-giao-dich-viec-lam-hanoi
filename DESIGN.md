# DESIGN.md — HỆ THỐNG THIẾT KẾ GIAO DIỆN ("HANOI CIVIC EMPLOYMENT PORTAL UI")

> **Project Design Lock (`D:\san-giao-dich-viec-lam-hanoi\DESIGN.md`)**
> - **Dự án**: Phát triển phần mềm Sàn giao việc làm trực tuyến cho Trung tâm Dịch vụ việc làm Hà Nội (`SE.G01`)
> - **Kiến trúc UI thực tế**: **Next.js 15 (App Router) + React 19 + Tailwind CSS** kết nối trực tiếp Backend Java Spring Boot & SQL Server (`db_SanGiaoDichViecLamHN`) — đã loại bỏ hoàn toàn thanh chuyển wireframe tĩnh.
> - **Surface Mode (`impeccable`)**: Kết hợp **Persuade** (`/` Trang chủ cổng việc làm) và **Operate** (Các không gian làm việc theo RBAC 4 vai trò: Ứng viên, Doanh nghiệp, Cán bộ Trung tâm DVVL, Quản trị viên).

---

## 1. Hệ Thống Bảng Màu Token (`:root` CSS Custom Properties)

Toàn bộ màu sắc và kiểu chữ bắt buộc đi qua biến `:root` trong `Frontend/src/app/globals.css`, giữ nguyên tông Xanh Hành Chính Hà Nội chuẩn Figma & WCAG AA/AAA:

| Token CSS | Mã màu | Vai trò sử dụng |
| :--- | :--- | :--- |
| `--bg-canvas` | `#F6F8FB` | Nền tổng thể không gian làm việc (App Canvas Background) |
| `--bg-surface` | `#FFFFFF` | Nền thẻ nội dung, bảng dữ liệu, thanh điều hướng và hộp thoại |
| `--bg-tint` | `#ECF3FC` | Nền vùng đang chọn, huy hiệu số liệu và thẻ hướng dẫn |
| `--bg-subtle` | `#F1F5F9` | Nền tiêu đề cột bảng (`thead`), nút bị khóa |
| `--brand-primary` | `#215DA8` | Xanh Hành Chính Hà Nội (Civic Blue) — Thanh nhận diện, nút CTA chính |
| `--brand-primary-hover` | `#1A4B88` | Trạng thái `:hover` nút CTA chính |
| `--brand-primary-active` | `#143A6B` | Trạng thái `:active` nút CTA chính |
| `--text-primary` | `#192536` | Màu chữ chính (Tương phản `14.8:1`) |
| `--text-secondary` | `#5F6C7B` | Màu chữ phụ (Tương phản `5.3:1`) |
| `--border-default` | `#DCE2E9` | Đường viền mảnh `1px solid` |
| `--border-focus` | `#215DA8` | Vòng `:focus-visible` tức thì (`0ms`) |
| `--status-success-bg` / `--status-success-ink` | `#ECFDF3` / `#15803D` | Đã duyệt, Đã xác minh, Trúng tuyển, Hoàn thiện CV |
| `--status-warning-bg` / `--status-warning-ink` | `#FFFBEB` / `#B45309` | Chờ duyệt, Chờ thẩm định, Đang xem, Hẹn phỏng vấn |
| `--status-danger-bg` / `--status-danger-ink` | `#FEF2F2` / `#B91C1C` | Từ chối, Bị khóa, Gỡ khẩn cấp, Lỗi kiểm tra dữ liệu |

---

## 2. Typography, Craft Floor & Quy Ước Ký Hiệu FIT HOU (§ 2.2)

- **Font chữ**: `'Inter'` (tiếng Việt UTF-8 đầy đủ) và `'JetBrains Mono'` (mã định danh `US00000001`, `JB00000001`, mã số thuế, IP Audit Log).
- **Bắt buộc `font-variant-numeric: tabular-nums`**: Trên 100% KPI, bảng dữ liệu, số liệu cung – cầu, mức lương, phần trăm hoàn thiện hồ sơ và thời gian phản hồi.
- **Hallmark & Impeccable Floor**:
  - `overflow-x: clip` trên `html, body` (không dùng `100vw`).
  - Không dùng `transition: all`; chỉ chuyển thuộc tính màu/opacity/transform tường minh kèm `@media (prefers-reduced-motion: reduce)`.
  - Không đặt nhãn tracked-uppercase trên table head, tag hay link điều hướng (dùng sentence case chuẩn đọc nhanh).
  - Không card-inside-a-card, không emoji thay icon (dùng `lucide-react` / inline SVG sạch).
  - Không bịa số liệu thống kê: mọi KPI và bảng đều truy vấn trực tiếp từ SQL Server qua API.
- **Design Convention IDs (§ 2.2 Báo cáo)**: Các thành phần tương tác gắn `id` và `data-control-id` chuẩn quy ước: `lb*` (Label), `tb*` (Textbox), `cb*` (Checkbox), `rdb*` (Radio), `cbb*` (Combobox), `dgv*` (DataGridView/Table), `pn*` (Panel), `nud*` (Numeric), `btn*` (Button).

---

## 3. Cấu Trúc Route Thực Tế Theo 4 Vai Trò RBAC

| Đường dẫn (Route) | Đối tượng sử dụng | Chức năng đáp ứng (`R01`–`R28`) |
| :--- | :--- | :--- |
| `/` | Công khai & Mọi vai trò | Trang chủ cổng việc làm, thanh tìm kiếm nhanh, KPI thị trường thực, tin mới nhất đã duyệt |
| `/viec-lam`, `/viec-lam/[maTin]` | Công khai & Ứng viên | Lọc việc làm đa tiêu chí AND $\le 1{,}5\text{s}$ (`R18`), Xem chi tiết & Nộp hồ sơ (`R20`) |
| `/dang-ky`, `/dang-nhap` | Khách vãng lai | Đăng ký & kích hoạt (`R01`), Đăng nhập RBAC (`R02`), Chống Brute-Force khóa 15 phút (`R03`) |
| `/tai-khoan` | Mọi tài khoản | Cập nhật thông tin, upload ảnh đại diện, đổi mật khẩu (`R04`) |
| `/ung-vien/ho-so` | Người tìm việc (`1`) | Chuẩn hóa hồ sơ (`R06`), Bật/tắt công khai (`R07`), Upload CV PDF (`R08`), In/Xuất Phiếu đăng ký PDF A4 (`R09`) |
| `/ung-vien/ung-tuyen` | Người tìm việc (`1`) | Theo dõi tiến trình & lịch sử thời gian thực $\le 2{,}5\text{s}$ (`R22`), Hủy ứng tuyển (`R23`) |
| `/doanh-nghiep`, `/phap-ly`, `/dang-tin`, `/ho-so-ung-tuyen`, `/tim-ung-vien` | Doanh nghiệp (`2`) | Thẩm định GPKD (`R10`), Đăng & quản lý vòng đời tin (`R11`, `R12`), Tải CV & Cập nhật 5 trạng thái / Lịch PV (`R13`, `R21`), Ngân hàng ứng viên che dữ liệu nhạy cảm (`R19`) |
| `/can-bo`, `/giay-gioi-thieu`, `/bao-cao` | Cán bộ TT DVVL (`3`) | Thẩm định DN (`R14`), Duyệt/Từ chối/Gỡ khẩn cấp tin (`R15`, `R16`), Cấp Giấy giới thiệu PDF A4 (`R17`), In Phiếu NTV (`R09`), Thống kê & Xuất PDF/Excel Sở LĐ-TB&XH (`R24`, `R25`) |
| `/quan-tri`, `/danh-muc`, `/nhat-ky` | Quản trị viên (`4`) | Quản trị phân quyền & Khóa/Mở tài khoản (`R05`), Gỡ tin khẩn cấp (`R16`), Quản trị Ngành nghề (`R26`) & 30 Quận/Huyện (`R27`), Giám sát & Xuất Excel Audit Log (`R28`) |

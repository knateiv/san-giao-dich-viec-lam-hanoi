package vn.gov.hanoi.dvvl.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.gov.hanoi.dvvl.Config.UserPrincipal;
import vn.gov.hanoi.dvvl.Dto.ApiResponse;
import vn.gov.hanoi.dvvl.Dto.VTaiKhoan;
import vn.gov.hanoi.dvvl.Model.MDoanhNghiep;
import vn.gov.hanoi.dvvl.Model.MTaiKhoan;
import vn.gov.hanoi.dvvl.Model.MVaiTro;
import vn.gov.hanoi.dvvl.Repository.DoanhNghiepRepository;
import vn.gov.hanoi.dvvl.Repository.TaiKhoanRepository;
import vn.gov.hanoi.dvvl.Repository.VaiTroRepository;
import vn.gov.hanoi.dvvl.Service.AuditLogService;
import vn.gov.hanoi.dvvl.Service.BusinessException;
import vn.gov.hanoi.dvvl.Service.FileStorageService;
import vn.gov.hanoi.dvvl.Service.IdGeneratorService;
import vn.gov.hanoi.dvvl.Service.JwtService;
import vn.gov.hanoi.dvvl.Service.TextNormalizer;

@RestController
public class CTaiKhoan {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");
    private static final Pattern TAX_PATTERN = Pattern.compile("^\\d{10}(\\d{3})?$");
    private static final Pattern STRONG_PWD = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
    private static final Random RANDOM = new Random();

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    private final TaiKhoanRepository taiKhoanRepo;
    private final VaiTroRepository vaiTroRepo;
    private final DoanhNghiepRepository doanhNghiepRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final IdGeneratorService idGenerator;
    private final AuditLogService auditLog;
    private final FileStorageService fileStorage;

    public CTaiKhoan(
            TaiKhoanRepository taiKhoanRepo,
            VaiTroRepository vaiTroRepo,
            DoanhNghiepRepository doanhNghiepRepo,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            IdGeneratorService idGenerator,
            AuditLogService auditLog,
            FileStorageService fileStorage
    ) {
        this.taiKhoanRepo = taiKhoanRepo;
        this.vaiTroRepo = vaiTroRepo;
        this.doanhNghiepRepo = doanhNghiepRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.idGenerator = idGenerator;
        this.auditLog = auditLog;
        this.fileStorage = fileStorage;
    }

    // R01: Đăng ký tài khoản mới (Người tìm việc = 1, Doanh nghiệp = 2)
    @PostMapping("/api/auth/dang-ky")
    @Transactional
    public ApiResponse<Map<String, Object>> dangKy(
            @Valid @RequestBody VTaiKhoan.DangKyRequest req,
            HttpServletRequest httpReq
    ) {
        int roleId = req.getFkMaVaiTro() != null ? req.getFkMaVaiTro() : 1;
        if (roleId != 1 && roleId != 2) {
            throw new BusinessException("Chỉ được phép đăng ký vai trò Người tìm việc (1) hoặc Doanh nghiệp (2).");
        }
        String email = TextNormalizer.normalizeText(req.getSEmail()).toLowerCase();
        String sdt = TextNormalizer.normalizeText(req.getSSDT());
        String hoTen = TextNormalizer.normalizeProperName(req.getSHoTen());

        if (!PHONE_PATTERN.matcher(sdt).matches()) {
            throw new BusinessException("Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số.");
        }
        if (taiKhoanRepo.existsActiveByEmail(email)) {
            throw new BusinessException("Địa chỉ Email này đã tồn tại trên hệ thống.");
        }
        if (!STRONG_PWD.matcher(req.getSMatKhau()).matches()) {
            throw new BusinessException("Mật khẩu phải tối thiểu 8 ký tự, gồm chữ hoa, chữ thường, chữ số và ký tự đặc biệt.");
        }
        if (!req.getSMatKhau().equals(req.getXacNhanMatKhau())) {
            throw new BusinessException("Mật khẩu xác nhận không trùng khớp.");
        }
        if (roleId == 2) {
            if (req.getSTenDoanhNghiep() == null || req.getSTenDoanhNghiep().isBlank()) {
                throw new BusinessException("Doanh nghiệp vui lòng nhập Tên doanh nghiệp theo Giấy phép ĐKKD.");
            }
            String mst = TextNormalizer.normalizeText(req.getSMaSoThue());
            if (mst == null || !TAX_PATTERN.matcher(mst).matches()) {
                throw new BusinessException("Mã số thuế doanh nghiệp phải gồm đúng 10 hoặc 13 chữ số.");
            }
            if (doanhNghiepRepo.existsActiveByTaxCode(mst, null)) {
                throw new BusinessException("Mã số thuế này đã được đăng ký bởi một doanh nghiệp khác.");
            }
        }

        String maTk = idGenerator.nextCode("tbl_TaiKhoan", "PK_MaTaiKhoan", "US");
        MTaiKhoan tk = new MTaiKhoan();
        tk.setPkMaTaiKhoan(maTk);
        tk.setSEmail(email);
        tk.setSMatKhau(passwordEncoder.encode(req.getSMatKhau()));
        tk.setFkMaVaiTro(roleId);
        tk.setSHoTen(hoTen);
        tk.setSSDT(sdt);
        tk.setBDaKichHoat(false);
        tk.setBBiKhoa(false);
        tk.setISoLanDangNhapSai(0);
        tk.setBDaXoa(false);
        tk.setDNgayTao(LocalDateTime.now());
        taiKhoanRepo.saveAndFlush(tk);

        if (roleId == 2) {
            MDoanhNghiep dn = new MDoanhNghiep();
            dn.setPkMaDoanhNghiep(idGenerator.nextCode("tbl_DoanhNghiep", "PK_MaDoanhNghiep", "DN"));
            dn.setFkMaTaiKhoan(maTk);
            dn.setSTenDoanhNghiep(TextNormalizer.normalizeText(req.getSTenDoanhNghiep()));
            dn.setSMaSoThue(TextNormalizer.normalizeText(req.getSMaSoThue()));
            dn.setSTenNguoiLienHe(hoTen);
            dn.setSSDTNguoiLienHe(sdt);
            dn.setSEmailNguoiLienHe(email);
            dn.setITrangThaiThamDinh(0);
            dn.setSGhiChuThamDinh("Hồ sơ mới khởi tạo khi đăng ký, vui lòng hoàn thiện pháp lý.");
            dn.setBDaXoa(false);
            doanhNghiepRepo.saveAndFlush(dn);
        }

        String otp = String.format("%06d", RANDOM.nextInt(1000000));
        otpStore.put(email, otp);

        auditLog.ghiNhatKy(maTk, "DANG_KY_TAI_KHOAN", "INFO", null,
                String.format("{\"sEmail\":\"%s\",\"fkMaVaiTro\":%d}", email, roleId), httpReq);

        return ApiResponse.ok(
                "Đăng ký thành công! Vui lòng nhập mã OTP kích hoạt tài khoản.",
                Map.of("pkMaTaiKhoan", maTk, "sEmail", email, "maOtpMinhHoa", otp));
    }

    // R01: Kích hoạt tài khoản qua mã OTP Email
    @PostMapping("/api/auth/kich-hoat")
    @Transactional
    public ApiResponse<Void> kichHoat(
            @RequestBody VTaiKhoan.KichHoatRequest req,
            HttpServletRequest httpReq
    ) {
        String email = TextNormalizer.normalizeText(req.getSEmail()).toLowerCase();
        MTaiKhoan tk = taiKhoanRepo.findActiveByEmail(email)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản với Email đã nhập.", HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(tk.getBDaKichHoat())) {
            return ApiResponse.ok("Tài khoản đã được kích hoạt trước đó.", null);
        }
        String expected = otpStore.get(email);
        if (expected == null || !expected.equals(TextNormalizer.normalizeText(req.getMaOtp()))) {
            throw new BusinessException("Mã OTP xác thực không chính xác hoặc đã hết hạn.");
        }
        tk.setBDaKichHoat(true);
        tk.setDNgayCapNhat(LocalDateTime.now());
        taiKhoanRepo.save(tk);
        otpStore.remove(email);

        auditLog.ghiNhatKy(tk.getPkMaTaiKhoan(), "KICH_HOAT_TAI_KHOAN", "INFO", "{\"bDaKichHoat\":0}", "{\"bDaKichHoat\":1}", httpReq);
        return ApiResponse.ok("Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay.", null);
    }

    // R02 & R03: Đăng nhập, phân quyền điều hướng RBAC & Khóa tạm thời 15 phút sau 5 lần sai
    @PostMapping("/api/auth/dang-nhap")
    @Transactional(noRollbackFor = BusinessException.class)
    public ApiResponse<VTaiKhoan.TaiKhoanView> dangNhap(
            @Valid @RequestBody VTaiKhoan.DangNhapRequest req,
            HttpServletRequest httpReq,
            HttpServletResponse httpRes
    ) {
        String email = TextNormalizer.normalizeText(req.getSEmail()).toLowerCase();
        MTaiKhoan tk = taiKhoanRepo.findActiveByEmail(email).orElse(null);

        if (tk == null) {
            auditLog.ghiNhatKy(null, "DANG_NHAP_THAT_BAI", "WARNING", null,
                    "{\"sEmail\":\"" + email + "\",\"lyDo\":\"Không tồn tại\"}", httpReq);
            throw new BusinessException("Email hoặc mật khẩu không chính xác.", HttpStatus.UNAUTHORIZED);
        }

        // Kiểm tra khóa tài khoản (R03 tự động 15 phút hoặc R05 Admin khóa)
        if (Boolean.TRUE.equals(tk.getBBiKhoa())) {
            if (tk.getDKhoaDenLuc() != null) {
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(tk.getDKhoaDenLuc())) {
                    long phutConLai = Math.max(1, Duration.between(now, tk.getDKhoaDenLuc()).toMinutes() + 1);
                    throw new BusinessException(
                            "Tài khoản đang tạm thời bị khóa 15 phút do nhập sai mật khẩu 5 lần liên tiếp. Vui lòng thử lại sau " + phutConLai + " phút.",
                            HttpStatus.LOCKED);
                } else {
                    tk.setBBiKhoa(false);
                    tk.setISoLanDangNhapSai(0);
                    tk.setDKhoaDenLuc(null);
                    tk.setSLyDoKhoa(null);
                    taiKhoanRepo.save(tk);
                }
            } else {
                String lyDo = tk.getSLyDoKhoa() != null ? tk.getSLyDoKhoa() : "Tài khoản đã bị Quản trị viên khóa.";
                throw new BusinessException(lyDo, HttpStatus.LOCKED);
            }
        }

        if (!Boolean.TRUE.equals(tk.getBDaKichHoat())) {
            throw new BusinessException("Tài khoản chưa được kích hoạt qua Email OTP.", HttpStatus.FORBIDDEN);
        }

        // Kiểm tra BCrypt (R33)
        if (!passwordEncoder.matches(req.getSMatKhau(), tk.getSMatKhau())) {
            int sai = (tk.getISoLanDangNhapSai() != null ? tk.getISoLanDangNhapSai() : 0) + 1;
            tk.setISoLanDangNhapSai(sai);
            if (sai >= 5) {
                tk.setBBiKhoa(true);
                tk.setDKhoaDenLuc(LocalDateTime.now().plusMinutes(15));
                tk.setSLyDoKhoa("Tự động khóa tạm thời 15 phút do nhập sai mật khẩu 5 lần liên tiếp.");
                taiKhoanRepo.save(tk);
                auditLog.ghiNhatKy(tk.getPkMaTaiKhoan(), "TU_DONG_KHOA_15_PHUT", "WARNING",
                        "{\"iSoLanDangNhapSai\":4}", "{\"iSoLanDangNhapSai\":5,\"bBiKhoa\":1}", httpReq);
                throw new BusinessException(
                        "Tài khoản tạm thời bị khóa 15 phút do nhập sai mật khẩu 5 lần. Vui lòng thử lại sau hoặc liên hệ Quản trị viên.",
                        HttpStatus.LOCKED);
            }
            taiKhoanRepo.save(tk);
            auditLog.ghiNhatKy(tk.getPkMaTaiKhoan(), "DANG_NHAP_SAI_MAT_KHAU", "WARNING",
                    null, "{\"soLanSai\":" + sai + "}", httpReq);
            throw new BusinessException(
                    "Mật khẩu không chính xác. Bạn còn " + (5 - sai) + " lần thử trước khi bị khóa tạm 15 phút.",
                    HttpStatus.UNAUTHORIZED);
        }

        // Đăng nhập thành công -> reset bộ đếm sai + cấp JWT + Cookie HttpOnly
        tk.setISoLanDangNhapSai(0);
        tk.setBBiKhoa(false);
        tk.setDKhoaDenLuc(null);
        taiKhoanRepo.save(tk);

        String jwt = jwtService.generateToken(tk.getPkMaTaiKhoan(), tk.getFkMaVaiTro(), tk.getSEmail());
        Cookie cookie = new Cookie("DVVL_TOKEN", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        httpRes.addCookie(cookie);

        auditLog.ghiNhatKy(tk.getPkMaTaiKhoan(), "DANG_NHAP_THANH_CONG", "INFO", null,
                String.format("{\"fkMaVaiTro\":%d}", tk.getFkMaVaiTro()), httpReq);

        VTaiKhoan.TaiKhoanView view = toView(tk);
        view.setToken(jwt);
        return ApiResponse.ok("Đăng nhập thành công!", view);
    }

    @PostMapping("/api/auth/dang-xuat")
    public ApiResponse<Void> dangXuat(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest httpReq,
            HttpServletResponse httpRes
    ) {
        Cookie cookie = new Cookie("DVVL_TOKEN", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        httpRes.addCookie(cookie);
        if (principal != null) {
            auditLog.ghiNhatKy(principal.getPkMaTaiKhoan(), "DANG_XUAT", "INFO", null, "{}", httpReq);
        }
        return ApiResponse.ok("Đã đăng xuất an toàn.", null);
    }

    @GetMapping("/api/auth/me")
    public ApiResponse<VTaiKhoan.TaiKhoanView> me(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new BusinessException("Chưa đăng nhập.", HttpStatus.UNAUTHORIZED);
        }
        MTaiKhoan tk = taiKhoanRepo.findActiveById(principal.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại.", HttpStatus.UNAUTHORIZED));
        return ApiResponse.ok("Thông tin phiên hiện tại.", toView(tk));
    }

    // R04: Cập nhật thông tin tài khoản
    @PutMapping("/api/tai-khoan/cap-nhat")
    @Transactional
    public ApiResponse<VTaiKhoan.TaiKhoanView> capNhatThongTin(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VTaiKhoan.CapNhatTaiKhoanRequest req,
            HttpServletRequest httpReq
    ) {
        MTaiKhoan tk = taiKhoanRepo.findActiveById(principal.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản.", HttpStatus.NOT_FOUND));
        String sdt = TextNormalizer.normalizeText(req.getSSDT());
        if (!PHONE_PATTERN.matcher(sdt).matches()) {
            throw new BusinessException("Số điện thoại liên hệ phải có đúng 10 chữ số, bắt đầu bằng 0.");
        }
        String oldData = String.format("{\"sHoTen\":\"%s\",\"sSDT\":\"%s\"}", tk.getSHoTen(), tk.getSSDT());
        tk.setSHoTen(TextNormalizer.normalizeProperName(req.getSHoTen()));
        tk.setSSDT(sdt);
        tk.setDNgayCapNhat(LocalDateTime.now());
        taiKhoanRepo.save(tk);

        String newData = String.format("{\"sHoTen\":\"%s\",\"sSDT\":\"%s\"}", tk.getSHoTen(), tk.getSSDT());
        auditLog.ghiNhatKy(tk.getPkMaTaiKhoan(), "CAP_NHAT_TAI_KHOAN", "INFO", oldData, newData, httpReq);
        return ApiResponse.ok("Cập nhật tài khoản thành công.", toView(tk));
    }

    // R04: Đổi mật khẩu
    @PutMapping("/api/tai-khoan/doi-mat-khau")
    @Transactional
    public ApiResponse<Void> doiMatKhau(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VTaiKhoan.DoiMatKhauRequest req,
            HttpServletRequest httpReq
    ) {
        MTaiKhoan tk = taiKhoanRepo.findActiveById(principal.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản.", HttpStatus.NOT_FOUND));
        if (!passwordEncoder.matches(req.getMatKhauHienTai(), tk.getSMatKhau())) {
            throw new BusinessException("Mật khẩu hiện tại không chính xác.");
        }
        if (passwordEncoder.matches(req.getMatKhauMoi(), tk.getSMatKhau())) {
            throw new BusinessException("Mật khẩu mới không được trùng với mật khẩu hiện tại.");
        }
        if (!STRONG_PWD.matcher(req.getMatKhauMoi()).matches()) {
            throw new BusinessException("Mật khẩu mới tối thiểu 8 ký tự, gồm chữ hoa, chữ thường, chữ số và ký tự đặc biệt.");
        }
        if (!req.getMatKhauMoi().equals(req.getXacNhanMatKhauMoi())) {
            throw new BusinessException("Xác nhận mật khẩu mới không trùng khớp.");
        }
        tk.setSMatKhau(passwordEncoder.encode(req.getMatKhauMoi()));
        tk.setDNgayCapNhat(LocalDateTime.now());
        taiKhoanRepo.save(tk);

        auditLog.ghiNhatKy(tk.getPkMaTaiKhoan(), "DOI_MAT_KHAU", "SECURITY_ACTION", null, "{\"result\":\"SUCCESS\"}", httpReq);
        return ApiResponse.ok("Đổi mật khẩu thành công.", null);
    }

    // R04: Tải lên ảnh đại diện
    @PostMapping("/api/tai-khoan/anh-dai-dien")
    @Transactional
    public ApiResponse<VTaiKhoan.TaiKhoanView> uploadAnhDaiDien(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpReq
    ) {
        MTaiKhoan tk = taiKhoanRepo.findActiveById(principal.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản.", HttpStatus.NOT_FOUND));
        String relPath = fileStorage.saveAvatar(tk.getPkMaTaiKhoan(), file);
        tk.setSDuongDanAnhDaiDien("/api/tai-khoan/anh/" + relPath.replace("avatars/", ""));
        tk.setDNgayCapNhat(LocalDateTime.now());
        taiKhoanRepo.save(tk);
        auditLog.ghiNhatKy(tk.getPkMaTaiKhoan(), "CAP_NHAT_ANH_DAI_DIEN", "INFO", null, "{\"path\":\"" + relPath + "\"}", httpReq);
        return ApiResponse.ok("Cập nhật ảnh đại diện thành công.", toView(tk));
    }

    @GetMapping("/api/tai-khoan/anh/{fileName}")
    public ResponseEntity<Resource> xemAnh(@PathVariable String fileName) {
        Resource res = fileStorage.loadAsResource("avatars/" + fileName);
        MediaType mt = fileName.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(mt).body(res);
    }

    // R05: Quản trị danh sách tài khoản (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/quan-tri/tai-khoan")
    public ApiResponse<List<VTaiKhoan.TaiKhoanView>> danhSachTaiKhoan(
            @RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(required = false) String q
    ) {
        List<VTaiKhoan.TaiKhoanView> list = taiKhoanRepo.findAllActiveOrderByCreatedDesc().stream()
                .filter(t -> roleId == null || roleId.equals(t.getFkMaVaiTro()))
                .filter(t -> locked == null || locked.equals(Boolean.TRUE.equals(t.getBBiKhoa())))
                .filter(t -> {
                    if (q == null || q.isBlank()) return true;
                    String kw = q.trim().toLowerCase();
                    return (t.getSEmail() != null && t.getSEmail().toLowerCase().contains(kw))
                            || (t.getSHoTen() != null && t.getSHoTen().toLowerCase().contains(kw))
                            || t.getPkMaTaiKhoan().toLowerCase().contains(kw);
                })
                .map(this::toView)
                .toList();
        return ApiResponse.ok("Danh sách tài khoản hệ thống.", list);
    }

    // R05: Quản trị phân quyền vai trò (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/quan-tri/tai-khoan/{id}/phan-quyen")
    @Transactional
    public ApiResponse<VTaiKhoan.TaiKhoanView> phanQuyen(
            @AuthenticationPrincipal UserPrincipal admin,
            @PathVariable String id,
            @RequestBody VTaiKhoan.PhanQuyenRequest req,
            HttpServletRequest httpReq
    ) {
        if (admin.getPkMaTaiKhoan().equals(id)) {
            throw new BusinessException("Không thể tự thay đổi vai trò của tài khoản Quản trị đang đăng nhập.");
        }
        MTaiKhoan tk = taiKhoanRepo.findActiveById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản " + id, HttpStatus.NOT_FOUND));
        MVaiTro role = vaiTroRepo.findById(req.getFkMaVaiTro())
                .orElseThrow(() -> new BusinessException("Vai trò không hợp lệ."));
        int cu = tk.getFkMaVaiTro();
        tk.setFkMaVaiTro(role.getPkMaVaiTro());
        tk.setDNgayCapNhat(LocalDateTime.now());
        taiKhoanRepo.save(tk);

        auditLog.ghiNhatKy(admin.getPkMaTaiKhoan(), "PHAN_QUYEN_TAI_KHOAN", "SECURITY_ACTION",
                String.format("{\"target\":\"%s\",\"fkMaVaiTro\":%d}", id, cu),
                String.format("{\"target\":\"%s\",\"fkMaVaiTro\":%d}", id, role.getPkMaVaiTro()), httpReq);
        return ApiResponse.ok("Đã phân quyền vai trò \"" + role.getSTenVaiTro() + "\" cho tài khoản " + tk.getSEmail(), toView(tk));
    }

    // R05 & R03: Quản trị khóa / mở khóa tài khoản (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/quan-tri/tai-khoan/{id}/khoa-mo")
    @Transactional
    public ApiResponse<VTaiKhoan.TaiKhoanView> khoaMoTaiKhoan(
            @AuthenticationPrincipal UserPrincipal admin,
            @PathVariable String id,
            @RequestBody VTaiKhoan.KhoaMoRequest req,
            HttpServletRequest httpReq
    ) {
        if (admin.getPkMaTaiKhoan().equals(id)) {
            throw new BusinessException("Không thể tự khóa tài khoản Quản trị đang đăng nhập.");
        }
        MTaiKhoan tk = taiKhoanRepo.findActiveById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản " + id, HttpStatus.NOT_FOUND));
        boolean khoa = Boolean.TRUE.equals(req.getBBiKhoa());
        if (khoa && (req.getSLyDoKhoa() == null || req.getSLyDoKhoa().isBlank())) {
            throw new BusinessException("Vui lòng nhập rõ lý do khóa tài khoản.");
        }
        boolean cu = Boolean.TRUE.equals(tk.getBBiKhoa());
        tk.setBBiKhoa(khoa);
        if (khoa) {
            tk.setSLyDoKhoa(TextNormalizer.normalizeText(req.getSLyDoKhoa()));
            tk.setDKhoaDenLuc(null); // Khóa quản trị vô thời hạn tới khi mở
        } else {
            tk.setSLyDoKhoa(null);
            tk.setDKhoaDenLuc(null);
            tk.setISoLanDangNhapSai(0);
        }
        tk.setDNgayCapNhat(LocalDateTime.now());
        taiKhoanRepo.save(tk);

        auditLog.ghiNhatKy(admin.getPkMaTaiKhoan(), khoa ? "KHOA_TAI_KHOAN" : "MO_KHOA_TAI_KHOAN", "SECURITY_ACTION",
                String.format("{\"target\":\"%s\",\"bBiKhoa\":%s}", id, cu),
                String.format("{\"target\":\"%s\",\"bBiKhoa\":%s,\"lyDo\":\"%s\"}", id, khoa, tk.getSLyDoKhoa() == null ? "" : tk.getSLyDoKhoa()),
                httpReq);
        return ApiResponse.ok(khoa ? "Đã khóa tài khoản thành công." : "Đã mở khóa tài khoản thành công.", toView(tk));
    }

    private VTaiKhoan.TaiKhoanView toView(MTaiKhoan tk) {
        VTaiKhoan.TaiKhoanView v = new VTaiKhoan.TaiKhoanView();
        v.setPkMaTaiKhoan(tk.getPkMaTaiKhoan());
        v.setSEmail(tk.getSEmail());
        v.setFkMaVaiTro(tk.getFkMaVaiTro());
        String roleName = vaiTroRepo.findById(tk.getFkMaVaiTro()).map(MVaiTro::getSTenVaiTro).orElse("Không xác định");
        v.setSTenVaiTro(roleName);
        v.setSHoTen(tk.getSHoTen());
        v.setSSDT(tk.getSSDT());
        v.setSDuongDanAnhDaiDien(tk.getSDuongDanAnhDaiDien());
        v.setBDaKichHoat(tk.getBDaKichHoat());
        v.setBBiKhoa(tk.getBBiKhoa());
        v.setISoLanDangNhapSai(tk.getISoLanDangNhapSai());
        v.setDKhoaDenLuc(tk.getDKhoaDenLuc());
        v.setSLyDoKhoa(tk.getSLyDoKhoa());
        v.setDNgayTao(tk.getDNgayTao());
        v.setDuongDanDich(switch (tk.getFkMaVaiTro()) {
            case 1 -> "/ung-vien/ho-so";
            case 2 -> "/doanh-nghiep";
            case 3 -> "/can-bo";
            case 4 -> "/quan-tri";
            default -> "/";
        });
        return v;
    }
}
